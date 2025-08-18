package com.jade.platform.dto;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Filter criteria for catalog searches")
public record CatalogFilter(
    @Schema(description = "Filter by title (case-insensitive, partial match)")
    String title,
    
    @Schema(description = "Filter by description (case-insensitive, partial match)")
    String description,
    
    @Schema(description = "Filter by industry ID")
    UUID industryId,
    
    @Schema(description = "Filter by industry name (case-insensitive, partial match)")
    String industryName,
    
    @Schema(description = "Filter by categories (matches if catalog contains ANY of the specified categories)")
    List<String> categories,
    
    @Schema(description = "Filter by tags (matches if catalog contains ANY of the specified tags)")
    List<String> tags,
    
    @Schema(description = "Filter by minimum price")
    BigDecimal minPrice,
    
    @Schema(description = "Filter by maximum price")
    BigDecimal maxPrice,
    
    @Schema(description = "Filter by merchant ID")
    UUID merchantId,
    
    @Schema(description = "Filter by minimum rating")
    Double minRating,
    
    @Schema(description = "Filter by maximum rating")
    Double maxRating,
    
    @Schema(description = "Filter by compliance status")
    ComplianceStatus complianceStatus,
    
    @Schema(description = "Filter by availability status")
    AvailabilityStatus availabilityStatus
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String description;
        private UUID industryId;
        private String industryName;
        private List<String> categories;
        private List<String> tags;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
        private UUID merchantId;
        private Double minRating;
        private Double maxRating;
        private ComplianceStatus complianceStatus;
        private AvailabilityStatus availabilityStatus;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder industryId(UUID industryId) {
            this.industryId = industryId;
            return this;
        }

        public Builder industryName(String industryName) {
            this.industryName = industryName;
            return this;
        }

        public Builder categories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder minPrice(BigDecimal minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public Builder maxPrice(BigDecimal maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder merchantId(UUID merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder minRating(Double minRating) {
            this.minRating = minRating;
            return this;
        }

        public Builder maxRating(Double maxRating) {
            this.maxRating = maxRating;
            return this;
        }

        public Builder complianceStatus(ComplianceStatus complianceStatus) {
            this.complianceStatus = complianceStatus;
            return this;
        }

        public Builder availabilityStatus(AvailabilityStatus availabilityStatus) {
            this.availabilityStatus = availabilityStatus;
            return this;
        }

        public CatalogFilter build() {
            return new CatalogFilter(
                title,
                description,
                industryId,
                industryName,
                categories,
                tags,
                minPrice,
                maxPrice,
                merchantId,
                minRating,
                maxRating,
                complianceStatus,
                availabilityStatus
            );
        }
    }
}