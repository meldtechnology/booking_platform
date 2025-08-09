package com.jade.platform.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a catalog record in the platform domain model.
 *
 * <p>Constraints:</p>
 * <ul>
 *   <li>Only {@code id}, {@code createdOn}, and {@code updatedOn} may be null.</li>
 *   <li>All other fields must be non-null.</li>
 *   <li>{@code price} must not be negative.</li>
 *   <li>{@code rating} must be between 0.0 and 5.0.</li>
 *   <li>{@code categories} and {@code tags} are defensively copied for immutability.</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <ul>
 *   <li>Use {@link Builder} for flexible construction and validation.</li>
 *   <li>For fields that may be null, use {@link java.util.Optional} when accessing them to avoid NPEs.</li>
 * </ul>
 */
public record Catalog(
    UUID id,                     // Unique catalog identifier (nullable for creation)
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
) {
    public enum ComplianceStatus { COMPLIANT, NON_COMPLIANT }
    public enum AvailabilityStatus { AVAILABLE, UNAVAILABLE }

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
        categories = Collections.unmodifiableList(List.copyOf(categories));
        tags = Collections.unmodifiableList(List.copyOf(tags));
    }

    /**
     * Builder for Catalog.
     * Usage:
     *   Catalog cat = Catalog.builder()
     *                        .title("Sample")
     *                        .description("Desc")
     *                        ...
     *                        .build();
     */
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private UUID id;
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

        public Builder id(UUID id) { this.id = id; return this; }
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
