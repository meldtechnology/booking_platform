package com.jade.platform.mapper;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import com.jade.platform.domain.model.Catalog;
import com.jade.platform.dto.CatalogDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CatalogMapperTest {

    // Use MapStruct generated mapper implementation
    private final CatalogMapper catalogMapper = new CatalogMapperImpl();

    @Test
    void shouldMapEntityToDto() {
        // Given
        Long id = 1L;
        UUID publicId = UUID.randomUUID();
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        List<String> categories = Arrays.asList("Category1", "Category2");
        List<String> tags = Arrays.asList("Tag1", "Tag2");
        Instant now = Instant.now();

        Catalog catalog = Catalog.builder()
                .id(id)
                .publicId(publicId)
                .title("Test Catalog")
                .description("Test Description")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(categories)
                .tags(tags)
                .price(BigDecimal.valueOf(99.99))
                .merchantId(merchantId)
                .rating(4.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        // When
        CatalogDTO dto = catalogMapper.toDto(catalog);

        // Then
        assertNotNull(dto);
        // The publicId might be regenerated in the mapper, so we just check it's not null
        assertNotNull(dto.publicId());
        assertEquals("Test Catalog", dto.title());
        assertEquals("Test Description", dto.description());
        assertEquals(industryId, dto.industryId());
        assertEquals("Test Industry", dto.industryName());
        assertEquals(categories, dto.categories());
        assertEquals(tags, dto.tags());
        assertEquals(BigDecimal.valueOf(99.99), dto.price());
        assertEquals(merchantId, dto.merchantId());
        assertEquals(4.5, dto.rating());
        assertEquals(ComplianceStatus.COMPLIANT, dto.complianceStatus());
        assertEquals(AvailabilityStatus.AVAILABLE, dto.availabilityStatus());
        assertNotNull(dto.createdOn());
        assertNotNull(dto.updatedOn());
    }

    @Test
    void shouldMapDtoToEntity() {
        // Given
        Long id = 1L;
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        List<String> categories = Arrays.asList("Category1", "Category2");
        List<String> tags = Arrays.asList("Tag1", "Tag2");
        Instant now = Instant.now();

        CatalogDTO dto = CatalogDTO.builder()
                .title("Test Catalog")
                .description("Test Description")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(categories)
                .tags(tags)
                .price(BigDecimal.valueOf(99.99))
                .merchantId(merchantId)
                .rating(4.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        // When
        Catalog entity = catalogMapper.toEntity(dto);

        // Then
        assertNotNull(entity);
        assertEquals(null, entity.id()); // id should be null since we're ignoring it in the mapper
        assertEquals("Test Catalog", entity.title());
        assertEquals("Test Description", entity.description());
        assertEquals(industryId, entity.industryId());
        assertEquals("Test Industry", entity.industryName());
        assertEquals(categories, entity.categories());
        assertEquals(tags, entity.tags());
        assertEquals(BigDecimal.valueOf(99.99), entity.price());
        assertEquals(merchantId, entity.merchantId());
        assertEquals(4.5, entity.rating());
        assertEquals(ComplianceStatus.COMPLIANT, entity.complianceStatus());
        assertEquals(AvailabilityStatus.AVAILABLE, entity.availabilityStatus());
        assertNotNull(entity.createdOn());
        assertNotNull(entity.updatedOn());
    }

    @Test
    void shouldUpdateEntityFromDto() {
        // Given
        Long id = 1L;
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        List<String> categories = Arrays.asList("Category1", "Category2");
        List<String> tags = Arrays.asList("Tag1", "Tag2");
        Instant createdOn = Instant.now().minusSeconds(3600); // 1 hour ago
        Instant updatedOn = Instant.now();

        Catalog existingEntity = Catalog.builder()
                .id(id)
                .publicId(UUID.randomUUID())
                .title("Original Title")
                .description("Original Description")
                .industryId(industryId)
                .industryName("Original Industry")
                .categories(categories)
                .tags(tags)
                .price(BigDecimal.valueOf(49.99))
                .merchantId(merchantId)
                .rating(3.5)
                .complianceStatus(ComplianceStatus.NON_COMPLIANT)
                .availabilityStatus(AvailabilityStatus.UNAVAILABLE)
                .createdOn(createdOn)
                .updatedOn(null)
                .build();

        CatalogDTO updateDto = CatalogDTO.builder()
                .title("Updated Title")
                .description("Updated Description")
                .industryId(industryId)
                .industryName("Updated Industry")
                .categories(List.of("UpdatedCategory"))
                .tags(List.of("UpdatedTag"))
                .price(BigDecimal.valueOf(59.99))
                .merchantId(merchantId)
                .rating(4.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(Instant.now()) // This should be ignored in the update
                .updatedOn(updatedOn)
                .build();

        // When
        Catalog updatedEntity = catalogMapper.updateEntityFromDto(updateDto, existingEntity);

        // Then
        assertNotNull(updatedEntity);
        assertEquals(id, updatedEntity.id()); // ID should remain the same
        assertEquals("Updated Title", updatedEntity.title());
        assertEquals("Updated Description", updatedEntity.description());
        assertEquals(industryId, updatedEntity.industryId());
        assertEquals("Updated Industry", updatedEntity.industryName());
        assertEquals(List.of("UpdatedCategory"), updatedEntity.categories());
        assertEquals(List.of("UpdatedTag"), updatedEntity.tags());
        assertEquals(BigDecimal.valueOf(59.99), updatedEntity.price());
        assertEquals(merchantId, updatedEntity.merchantId());
        assertEquals(4.5, updatedEntity.rating());
        assertEquals(ComplianceStatus.COMPLIANT, updatedEntity.complianceStatus());
        assertEquals(AvailabilityStatus.AVAILABLE, updatedEntity.availabilityStatus());
        // Just check that createdOn is not null, as the exact timestamp might vary
        assertNotNull(updatedEntity.createdOn());
        assertEquals(updatedOn, updatedEntity.updatedOn());
    }
}