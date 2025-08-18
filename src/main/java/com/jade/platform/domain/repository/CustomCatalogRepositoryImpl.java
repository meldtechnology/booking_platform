package com.jade.platform.domain.repository;

import com.jade.platform.domain.model.Catalog;
import com.jade.platform.dto.CatalogFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomCatalogRepositoryImpl implements CustomCatalogRepository {

    private static final Logger logger = LoggerFactory.getLogger(CustomCatalogRepositoryImpl.class);
    private final R2dbcEntityTemplate template;

    public CustomCatalogRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Flux<Catalog> findByFilter(CatalogFilter filter) {
        Query query = buildFilterQuery(filter);
        logger.debug("Executing filter query: {}", query);
        return template.select(Catalog.class)
                .matching(query)
                .all();
    }

    @Override
    public Flux<Catalog> findByFilter(CatalogFilter filter, Pageable pageable) {
        Query query = buildFilterQuery(filter)
                .with(pageable);
        logger.debug("Executing pageable filter query: {}", query);
        return template.select(Catalog.class)
                .matching(query)
                .all();
    }

    @Override
    public Mono<Long> countByFilter(CatalogFilter filter) {
        Query query = buildFilterQuery(filter);
        logger.debug("Executing count filter query: {}", query);
        return template.count(query, Catalog.class);
    }

    private Query buildFilterQuery(CatalogFilter filter) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (filter == null) {
            return Query.empty();
        }

        // Add criteria for each filter field if it's not null
        if (StringUtils.hasText(filter.title())) {
            // Use startsWith for better index utilization if searching for beginning of title
            if (filter.title().endsWith("*") && !filter.title().startsWith("*")) {
                String titlePrefix = filter.title().substring(0, filter.title().length() - 1);
                criteriaList.add(Criteria.where("title").like(titlePrefix + "%").ignoreCase(true));
            } else {
                // Fall back to contains search if needed, but this is less efficient
                criteriaList.add(Criteria.where("title").like("%" + filter.title().replace("*", "") + "%").ignoreCase(true));
            }
        }

        if (StringUtils.hasText(filter.description())) {
            // For description searches, we'll use a combination of approaches
            if (filter.description().length() > 3) {
                // For longer search terms, we'd ideally use full-text search
                // Since Spring Data R2DBC's Criteria API doesn't directly support PostgreSQL's full-text search operators,
                // we'll use a more targeted LIKE query that can still benefit from the indexes
                criteriaList.add(Criteria.where("description").like("%" + filter.description() + "%").ignoreCase(true));
                
                // Note: In a production application, consider using a custom query method or native query
                // to leverage the full-text search capabilities of PostgreSQL with the tsvector column
            } else {
                // For very short search terms, use prefix matching to avoid excessive matches
                criteriaList.add(Criteria.where("description").like(filter.description() + "%").ignoreCase(true));
            }
        }

        // Exact matches are already optimal
        if (filter.industryId() != null) {
            criteriaList.add(Criteria.where("industry_id").is(filter.industryId()));
        }

        if (StringUtils.hasText(filter.industryName())) {
            // Industry name is likely to be a shorter, more specific field
            // Use startsWith for better performance if appropriate
            if (filter.industryName().length() > 2) {
                criteriaList.add(Criteria.where("industry_name").like(filter.industryName() + "%").ignoreCase(true));
            } else {
                criteriaList.add(Criteria.where("industry_name").is(filter.industryName()).ignoreCase(true));
            }
        }

        if (filter.merchantId() != null) {
            criteriaList.add(Criteria.where("merchant_id").is(filter.merchantId()));
        }

        // Range queries are already optimal
        if (filter.minPrice() != null) {
            criteriaList.add(Criteria.where("price").greaterThanOrEquals(filter.minPrice()));
        }

        if (filter.maxPrice() != null) {
            criteriaList.add(Criteria.where("price").lessThanOrEquals(filter.maxPrice()));
        }

        if (filter.minRating() != null) {
            criteriaList.add(Criteria.where("rating").greaterThanOrEquals(filter.minRating()));
        }

        if (filter.maxRating() != null) {
            criteriaList.add(Criteria.where("rating").lessThanOrEquals(filter.maxRating()));
        }

        if (filter.complianceStatus() != null) {
            criteriaList.add(Criteria.where("compliance_status").is(filter.complianceStatus().name()));
        }

        if (filter.availabilityStatus() != null) {
            criteriaList.add(Criteria.where("availability_status").is(filter.availabilityStatus().name()));
        }

        // Properly handle array fields using PostgreSQL's array operators
        if (filter.categories() != null && !filter.categories().isEmpty()) {
            // For PostgreSQL arrays, we should use a custom query with the @> operator
            // Since R2DBC criteria API doesn't directly support this, we'll use a workaround
            // In a real application, consider using a native query for this part
            
            // This is a simplified approach that checks if any category matches
            // It's not as efficient as using the @> operator directly
            List<Criteria> categoryCriteria = new ArrayList<>();
            for (String category : filter.categories()) {
                categoryCriteria.add(Criteria.where("categories").like("%" + category + "%"));
            }
            
            // Combine with OR since we want to match if any category is present
            criteriaList.add(categoryCriteria.stream()
                    .reduce(Criteria::or)
                    .orElse(Criteria.empty()));
        }

        if (filter.tags() != null && !filter.tags().isEmpty()) {
            // Similar approach for tags
            List<Criteria> tagCriteria = new ArrayList<>();
            for (String tag : filter.tags()) {
                tagCriteria.add(Criteria.where("tags").like("%" + tag + "%"));
            }
            
            criteriaList.add(tagCriteria.stream()
                    .reduce(Criteria::or)
                    .orElse(Criteria.empty()));
        }

        // Combine all criteria with AND
        return criteriaList.isEmpty()
                ? Query.empty()
                : Query.query(criteriaList.stream()
                        .reduce(Criteria::and)
                        .orElse(Criteria.empty()));
    }
}