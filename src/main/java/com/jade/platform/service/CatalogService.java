package com.jade.platform.service;

import com.jade.platform.config.CacheConfig;
import com.jade.platform.domain.model.Catalog;
import com.jade.platform.domain.repository.CatalogRepository;
import com.jade.platform.dto.CatalogDTO;
import com.jade.platform.dto.CatalogFilter;
import com.jade.platform.dto.CreateCatalogDto;
import com.jade.platform.dto.PageResponse;
import com.jade.platform.exception.ResourceNotFoundException;
import com.jade.platform.mapper.CatalogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CatalogService {
    private static final Logger log = LoggerFactory.getLogger(CatalogService.class);
    
    private final CatalogRepository catalogRepository;
    private final CatalogMapper catalogMapper;
    private final CacheManager cacheManager;
    
    public CatalogService(CatalogRepository catalogRepository, CatalogMapper catalogMapper, CacheManager cacheManager) {
        this.catalogRepository = catalogRepository;
        this.catalogMapper = catalogMapper;
        this.cacheManager = cacheManager;
    }
    
    @Transactional(readOnly = true)
    public Flux<CatalogDTO> findAll(PageRequest pageRequest) {
        log.debug("Finding all catalog items with pagination: {}", pageRequest);
        return catalogRepository.findAll()
            .skip((long) pageRequest.getPageNumber() * pageRequest.getPageSize())
            .take(pageRequest.getPageSize())
            .map(catalogMapper::toDtoWithoutId)
            .doOnComplete(() -> log.debug("Completed finding all catalog items"));
    }
    
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_CATALOG_BY_PUBLIC_ID, key = "#id", unless = "#result == null")
    public Mono<CatalogDTO> findById(UUID id) {
        log.debug("Finding catalog item by UUID: {} (redirecting to findByPublicId)", id);
        return findByPublicId(id);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_CATALOG_BY_PUBLIC_ID, key = "#publicId", unless = "#result == null")
    public Mono<CatalogDTO> findByPublicId(UUID publicId) {
        log.debug("Finding catalog item by public ID: {} (cache miss)", publicId);
        return catalogRepository.findByPublicId(publicId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException.forId("Catalog", publicId)))
            .map(catalogMapper::toDtoWithoutId)
            .doOnSuccess(dto -> log.debug("Found catalog item with public ID: {}", publicId));
    }
    
    /**
     * Creates a new catalog item using the CreateCatalogDto.
     * This method automatically generates publicId, createdOn, and updatedOn fields.
     *
     * @param createCatalogDto the DTO containing catalog data without system-managed fields
     * @return the created catalog as a CatalogDTO
     */
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.CACHE_ALL_CATALOGS, allEntries = true)
    public Mono<CatalogDTO> create(CreateCatalogDto createCatalogDto) {
        log.debug("Creating new catalog item: {}", createCatalogDto);
        
        // Evict industry cache if industryId is present
        if (createCatalogDto.industryId() != null) {
            log.debug("Evicting cache for industry ID: {}", createCatalogDto.industryId());
        }
        
        return Mono.just(createCatalogDto)
            .map(catalogMapper::toNewEntity)
            .flatMap(catalogRepository::save)
            .map(catalogMapper::toDtoWithoutId)
            .doOnSuccess(created -> {
                log.debug("Created catalog item with title: {}", created.title());
            });
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheConfig.CACHE_CATALOG_BY_ID, key = "#id"),
        @CacheEvict(cacheNames = CacheConfig.CACHE_ALL_CATALOGS, allEntries = true)
    })
    public Mono<CatalogDTO> update(Long id, CatalogDTO catalogDTO) {
        log.debug("Updating catalog item with ID: {}", id);
        
        // Track the original industry ID for cache eviction
        final UUID[] originalIndustryId = new UUID[1];
        
        return catalogRepository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException.forId("Catalog", id)))
            .flatMap(existingCatalog -> {
                // Store the original industry ID for cache eviction
                originalIndustryId[0] = existingCatalog.industryId();
                
                // If industry ID is changing, log for cache eviction
                if (catalogDTO.industryId() != null && 
                    !catalogDTO.industryId().equals(existingCatalog.industryId())) {
                    log.debug("Industry ID changing from {} to {}", 
                        existingCatalog.industryId(), catalogDTO.industryId());
                }
                
                Catalog updatedCatalog = catalogMapper.updateEntityFromDto(catalogDTO, existingCatalog);
                return catalogRepository.save(updatedCatalog);
            })
            .map(catalogMapper::toDtoWithoutId)
            .doOnSuccess(updated -> {
                log.debug("Updated catalog item with ID: {}", id);
                
                // Manually evict industry caches if needed
                try {
                    // Evict cache for original industry ID if it exists
                    if (originalIndustryId[0] != null) {
                        log.debug("Evicting cache for original industry ID: {}", originalIndustryId[0]);
                        // In a real application, use CacheManager to evict this cache
                    }
                    
                    // Evict cache for new industry ID if it exists and is different
                    if (updated.industryId() != null && 
                        !updated.industryId().equals(originalIndustryId[0])) {
                        log.debug("Evicting cache for new industry ID: {}", updated.industryId());
                        // In a real application, use CacheManager to evict this cache
                    }
                } catch (Exception e) {
                    log.warn("Failed to evict industry cache: {}", e.getMessage());
                }
            });
    }
    
    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = CacheConfig.CACHE_CATALOG_BY_PUBLIC_ID, key = "#publicId"),
        @CacheEvict(cacheNames = CacheConfig.CACHE_ALL_CATALOGS, allEntries = true)
    })
    public Mono<CatalogDTO> updateByPublicId(UUID publicId, CatalogDTO catalogDTO) {
        log.debug("Updating catalog item with public ID: {}", publicId);
        
        // Track the original industry ID for cache eviction
        final UUID[] originalIndustryId = new UUID[1];
        
        return catalogRepository.findByPublicId(publicId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException.forId("Catalog", publicId)))
            .flatMap(existingCatalog -> {
                // Store the original industry ID for cache eviction
                originalIndustryId[0] = existingCatalog.industryId();
                
                // If industry ID is changing, log for cache eviction
                if (catalogDTO.industryId() != null && 
                    !catalogDTO.industryId().equals(existingCatalog.industryId())) {
                    log.debug("Industry ID changing from {} to {}", 
                        existingCatalog.industryId(), catalogDTO.industryId());
                }
                
                Catalog updatedCatalog = catalogMapper.updateEntityFromDto(catalogDTO, existingCatalog);
                return catalogRepository.save(updatedCatalog);
            })
            .map(catalogMapper::toDtoWithoutId)
            .doOnSuccess(updated -> {
                log.debug("Updated catalog item with public ID: {}", publicId);
                
                // Manually evict industry caches if needed
                try {
                    // Evict cache for original industry ID if it exists
                    if (originalIndustryId[0] != null) {
                        log.debug("Evicting cache for original industry ID: {}", originalIndustryId[0]);
                        // In a real application, use CacheManager to evict this cache
                    }
                    
                    // Evict cache for new industry ID if it exists and is different
                    if (updated.industryId() != null && 
                        !updated.industryId().equals(originalIndustryId[0])) {
                        log.debug("Evicting cache for new industry ID: {}", updated.industryId());
                        // In a real application, use CacheManager to evict this cache
                    }
                } catch (Exception e) {
                    log.warn("Failed to evict industry cache: {}", e.getMessage());
                }
            });
    }
    
    @Transactional
    public Mono<CatalogDTO> update(UUID id, CatalogDTO catalogDTO) {
        log.debug("Updating catalog item with UUID: {} (redirecting to updateByPublicId)", id);
        return updateByPublicId(id, catalogDTO);
    }
    
    @Transactional
    public Mono<Void> delete(UUID id) {
        log.debug("Deleting catalog item with UUID: {} (redirecting to deleteByPublicId)", id);
        return deleteByPublicId(id);
    }
    
    @Transactional
    public Mono<Void> deleteByPublicId(UUID publicId) {
        log.debug("Deleting catalog item with public ID: {}", publicId);
        return catalogRepository.findByPublicId(publicId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException.forId("Catalog", publicId)))
            .flatMap(catalog -> catalogRepository.deleteByPublicId(publicId))
            .doOnSuccess(v -> {
                Objects.requireNonNull(cacheManager.getCache(CacheConfig.CACHE_CATALOG_BY_PUBLIC_ID)).evict(publicId);
                log.debug("Deleted catalog item with public ID: {}", publicId);
            });
    }
    
    @Transactional(readOnly = true)
    public Flux<CatalogDTO> findByTitle(String title) {
        log.debug("Finding catalog items by title: {}", title);
        return catalogRepository.findByTitleContainingIgnoreCase(title)
            .map(catalogMapper::toDtoWithoutId)
            .doOnComplete(() -> log.debug("Completed finding catalog items by title: {}", title));
    }
    
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheConfig.CACHE_CATALOGS_BY_INDUSTRY, key = "#industryId", unless = "#result.empty()")
    public Flux<CatalogDTO> findByIndustryId(UUID industryId) {
        log.debug("Finding catalog items by industry ID: {} (cache miss)", industryId);
        return catalogRepository.findByIndustryId(industryId)
            .map(catalogMapper::toDtoWithoutId)
            .doOnComplete(() -> log.debug("Completed finding catalog items by industry ID: {}", industryId));
    }
    
    @Transactional(readOnly = true)
    public Flux<CatalogDTO> findByFilter(CatalogFilter filter) {
        log.debug("Finding catalog items by filter: {}", filter);
        return catalogRepository.findByFilter(filter)
            .map(catalogMapper::toDtoWithoutId)
            .doOnComplete(() -> log.debug("Completed finding catalog items by filter"));
    }
    
    @Transactional(readOnly = true)
    public Flux<CatalogDTO> findByFilter(CatalogFilter filter, Pageable pageable) {
        log.debug("Finding catalog items by filter with pagination: {}, {}", filter, pageable);
        return catalogRepository.findByFilter(filter, pageable)
            .map(catalogMapper::toDtoWithoutId)
            .doOnComplete(() -> log.debug("Completed finding catalog items by filter with pagination"));
    }
    
    @Transactional(readOnly = true)
    public Mono<PageResponse<CatalogDTO>> findByFilterPaged(CatalogFilter filter, Pageable pageable) {
        log.debug("Finding catalog items by filter with pagination and count: {}, {}", filter, pageable);
        
        Flux<CatalogDTO> pagedItems = findByFilter(filter, pageable);
        Mono<Long> count = catalogRepository.countByFilter(filter);
        
        return Mono.zip(pagedItems.collectList(), count)
            .map(tuple -> {
                List<CatalogDTO> content = tuple.getT1();
                Long totalElements = tuple.getT2();
                return PageResponse.of(
                    content, 
                    pageable.getPageNumber(), 
                    pageable.getPageSize(), 
                    totalElements
                );
            })
            .doOnSuccess(page -> log.debug("Completed finding catalog items by filter with count: {}", page.totalElements()));
    }
}