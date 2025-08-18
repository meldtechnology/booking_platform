package com.jade.platform.dto;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating a new Catalog item.
 * Excludes publicId, createdOn, and updatedOn which are managed by the system.
 */
@Builder(toBuilder = true)
public record CreateCatalogDto(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
        String title,
        
        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 4000, message = "Description must be between 10 and 4000 characters")
        String description,
        
        @NotNull(message = "Industry ID is required")
        UUID industryId,
        
        @NotBlank(message = "Industry name is required")
        String industryName,
        
        @NotNull(message = "Categories cannot be null")
        List<String> categories,
        
        @NotNull(message = "Tags cannot be null")
        List<String> tags,
        
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
        BigDecimal price,
        
        @NotNull(message = "Merchant ID is required")
        UUID merchantId,
        
        @Min(value = 0, message = "Rating must be at least 0.0")
        @Max(value = 5, message = "Rating must be at most 5.0")
        double rating,
        
        @NotNull(message = "Compliance status is required")
        ComplianceStatus complianceStatus,
        
        @NotNull(message = "Availability status is required")
        AvailabilityStatus availabilityStatus
) {
    public CreateCatalogDto {
        // Defensive copies for immutability
        if (categories != null) {
            categories = List.copyOf(categories);
        }
        if (tags != null) {
            tags = List.copyOf(tags);
        }
    }
}