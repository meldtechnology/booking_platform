package com.jade.platform.domain.repository;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import com.jade.platform.domain.model.Catalog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface CatalogRepository extends R2dbcRepository<Catalog, Long>, CustomCatalogRepository {
    
    Mono<Catalog> findByPublicId(UUID publicId);
    
    Flux<Catalog> findByTitleContainingIgnoreCase(String title);
    
    Flux<Catalog> findByIndustryId(UUID industryId);
    
    Flux<Catalog> findByMerchantId(UUID merchantId);
    
    Flux<Catalog> findByAvailabilityStatus(AvailabilityStatus status);
    
    Flux<Catalog> findByComplianceStatus(ComplianceStatus status);
    
    Flux<Catalog> findByPriceLessThanEqual(BigDecimal maxPrice);
    
    Flux<Catalog> findByRatingGreaterThanEqual(double minRating);
    
    @Query("SELECT * FROM catalog c WHERE :category = ANY(c.categories)")
    Flux<Catalog> findByCategory(String category);
    
    @Query("SELECT * FROM catalog c WHERE :tag = ANY(c.tags)")
    Flux<Catalog> findByTag(String tag);
    
    Mono<Long> countByMerchantId(UUID merchantId);
    
    Mono<Long> deleteByMerchantId(UUID merchantId);
    
    Mono<Void> deleteByPublicId(UUID publicId);
}