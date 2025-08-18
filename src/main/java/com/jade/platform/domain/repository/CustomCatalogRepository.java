package com.jade.platform.domain.repository;

import com.jade.platform.domain.model.Catalog;
import com.jade.platform.dto.CatalogFilter;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Custom repository interface for advanced Catalog filtering operations.
 * This interface defines methods for filtering catalogs based on multiple criteria.
 */
public interface CustomCatalogRepository {
    
    /**
     * Find catalogs matching the given filter criteria.
     *
     * @param filter the filter criteria to apply
     * @return a Flux of matching Catalog entities
     */
    Flux<Catalog> findByFilter(CatalogFilter filter);
    
    /**
     * Find catalogs matching the given filter criteria with pagination.
     *
     * @param filter the filter criteria to apply
     * @param pageable pagination information
     * @return a Flux of matching Catalog entities
     */
    Flux<Catalog> findByFilter(CatalogFilter filter, Pageable pageable);
    
    /**
     * Count catalogs matching the given filter criteria.
     *
     * @param filter the filter criteria to apply
     * @return a Mono with the count of matching Catalog entities
     */
    Mono<Long> countByFilter(CatalogFilter filter);
}