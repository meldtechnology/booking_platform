package com.jade.platform.domain.model;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Table("catalog")
public record Catalog (
        @Id
        Long id,                     // Unique catalog identifier (nullable for creation)
        UUID publicId,               // Unique catalog public identifier (nullable for creation)
        String title,                // Catalog item title (required, non-null, non-blank)
        String description,          // Catalog item description (required, non-null)
        UUID industryId,             // Related industry identifier (required, non-null)
        String industryName,         // Industry name (required, non-null)
        List<String> categories,     // List of categories (required, non-null, defensively copied)
        List<String> tags,           // List of tags (required, non-null, defensively copied)
        BigDecimal price,            // Price (required, non-null, must be >= 0)
        UUID merchantId,             // Merchant identifier (required, non-null)
        double rating,               // Catalog rating (required, 0.0 <= rating <= 5.0)
        ComplianceStatus complianceStatus, // Enum for compliance
        AvailabilityStatus availabilityStatus, // Enum for availability
        Instant createdOn,           // Creation timestamp (nullable)
        Instant updatedOn            // Last update timestamp (nullable)
) implements Persistable<Long> {

    public Catalog {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be null or blank");
        }
        if (description == null) {
            throw new IllegalArgumentException("description must not be null");
        }
        if (industryId == null) {
            throw new IllegalArgumentException("industryId must not be null");
        }
        if (industryName == null) {
            throw new IllegalArgumentException("industryName must not be null");
        }
        if (categories == null) {
            throw new IllegalArgumentException("categories must not be null");
        }
        if (tags == null) {
            throw new IllegalArgumentException("tags must not be null");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("price must not be null or negative");
        }
        if (merchantId == null) {
            throw new IllegalArgumentException("merchantId must not be null");
        }
        if (rating < 0.0 || rating > 5.0) {
            throw new IllegalArgumentException("rating must be between 0.0 and 5.0");
        }
        if (complianceStatus == null) {
            throw new IllegalArgumentException("complianceStatus must not be null");
        }
        if (availabilityStatus == null) {
            throw new IllegalArgumentException("availabilityStatus must not be null");
        }

        // Defensive copies for immutability
        categories = List.copyOf(categories);
        tags = List.copyOf(tags);
    }

    public static Builder builder() { return new Builder(); }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }

    public static class Builder {
        private Long id;
        private UUID publicId;
        private String title;
        private String description;
        private UUID industryId;
        private String industryName;
        private List<String> categories = List.of();
        private List<String> tags = List.of();
        private BigDecimal price;
        private UUID merchantId;
        private double rating;
        private ComplianceStatus complianceStatus = ComplianceStatus.NON_COMPLIANT;
        private AvailabilityStatus availabilityStatus = AvailabilityStatus.UNAVAILABLE;
        private Instant createdOn;
        private Instant updatedOn;

        public Builder id(Long id) { this.id = id; return this; }

        public Builder publicId(UUID publicId) { this.publicId = publicId; return this; }

        public Builder title(String title) { this.title = title; return this; }
        
        public Builder description(String description) { this.description = description; return this; }
        
        public Builder industryId(UUID industryId) { this.industryId = industryId; return this; }
        
        public Builder industryName(String industryName) { this.industryName = industryName; return this; }
        
        public Builder categories(List<String> categories) { this.categories = categories == null ? List.of() : List.copyOf(categories); return this; }
        
        public Builder tags(List<String> tags) { this.tags = tags == null ? List.of() : List.copyOf(tags); return this; }
        
        public Builder price(BigDecimal price) { this.price = price; return this; }
        
        public Builder merchantId(UUID merchantId) { this.merchantId = merchantId; return this; }
        
        public Builder rating(double rating) { this.rating = rating; return this; }
        
        public Builder complianceStatus(ComplianceStatus complianceStatus) { this.complianceStatus = complianceStatus; return this; }
        
        public Builder availabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; return this; }
        
        public Builder createdOn(Instant createdOn) { this.createdOn = createdOn; return this; }
        
        public Builder updatedOn(Instant updatedOn) { this.updatedOn = updatedOn; return this; }

        public Catalog build() {
            return new Catalog(
                id,
                publicId,
                title,
                description,
                industryId,
                industryName,
                categories,
                tags,
                price,
                merchantId,
                rating,
                complianceStatus,
                availabilityStatus,
                createdOn,
                updatedOn
            );
        }
    }
}