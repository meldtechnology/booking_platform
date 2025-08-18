package com.jade.platform.controller;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import com.jade.platform.domain.model.Catalog;
import com.jade.platform.dto.CatalogDTO;
import com.jade.platform.dto.CreateCatalogDto;
import com.jade.platform.mapper.CatalogMapper;
import com.jade.platform.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@WebFluxTest(CatalogController.class)
@SuppressWarnings("deprecation") // Suppress warning for deprecated MockBean
class CatalogControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private CatalogMapper catalogMapper;

    @Test
    void shouldCreateCatalog() {
        // Given
        UUID id = UUID.randomUUID();
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        Instant now = Instant.now();

        CreateCatalogDto requestDTO = CreateCatalogDto.builder()
                .title("Test Catalog")
                .description("Test Description with sufficient length for validation")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(Arrays.asList("Category1", "Category2"))
                .tags(Arrays.asList("Tag1", "Tag2"))
                .price(BigDecimal.valueOf(99.99))
                .merchantId(merchantId)
                .rating(4.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .build();

        Catalog entity = Catalog.builder()
                .title(requestDTO.title())
                .description(requestDTO.description())
                .industryId(requestDTO.industryId())
                .industryName(requestDTO.industryName())
                .categories(requestDTO.categories())
                .tags(requestDTO.tags())
                .price(requestDTO.price())
                .merchantId(requestDTO.merchantId())
                .rating(requestDTO.rating())
                .complianceStatus(requestDTO.complianceStatus())
                .availabilityStatus(requestDTO.availabilityStatus())
                .build();

        Catalog savedEntity = Catalog.builder()
                .id(1L) // Use a dummy ID for testing
                .publicId(id)
                .title(requestDTO.title())
                .description(requestDTO.description())
                .industryId(requestDTO.industryId())
                .industryName(requestDTO.industryName())
                .categories(requestDTO.categories())
                .tags(requestDTO.tags())
                .price(requestDTO.price())
                .merchantId(requestDTO.merchantId())
                .rating(requestDTO.rating())
                .complianceStatus(requestDTO.complianceStatus())
                .availabilityStatus(requestDTO.availabilityStatus())
                .createdOn(now)
                .updatedOn(now)
                .build();

        CatalogDTO responseDTO = CatalogDTO.builder()
                .publicId(id)
                .title(requestDTO.title())
                .description(requestDTO.description())
                .industryId(requestDTO.industryId())
                .industryName(requestDTO.industryName())
                .categories(requestDTO.categories())
                .tags(requestDTO.tags())
                .price(requestDTO.price())
                .merchantId(requestDTO.merchantId())
                .rating(requestDTO.rating())
                .complianceStatus(requestDTO.complianceStatus())
                .availabilityStatus(requestDTO.availabilityStatus())
                .createdOn(now)
                .updatedOn(now)
                .build();

        // When
        Mockito.when(catalogMapper.toEntityFromCreateDto(requestDTO)).thenReturn(entity);
        Mockito.when(catalogService.create(Mockito.any(CreateCatalogDto.class))).thenReturn(Mono.just(responseDTO));

        // Then
        webTestClient.post()
                .uri("/api/v1/catalogs")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CatalogDTO.class)
                .isEqualTo(responseDTO);
    }

    @Test
    void shouldGetCatalogById() {
        // Given
        UUID id = UUID.randomUUID();
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        Instant now = Instant.now();

        Catalog entity = Catalog.builder()
                .id(1L) // Use a dummy ID for testing
                .publicId(id)
                .title("Test Catalog")
                .description("Test Description")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(Arrays.asList("Category1", "Category2"))
                .tags(Arrays.asList("Tag1", "Tag2"))
                .price(BigDecimal.valueOf(99.99))
                .merchantId(merchantId)
                .rating(4.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        CatalogDTO dto = CatalogDTO.builder()
                .title("Test Catalog")
                .description("Test Description")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(Arrays.asList("Category1", "Category2"))
                .tags(Arrays.asList("Tag1", "Tag2"))
                .price(BigDecimal.valueOf(99.99))
                .merchantId(merchantId)
                .rating(4.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        // When
        Mockito.when(catalogService.findByPublicId(id)).thenReturn(Mono.just(dto));

        // Then
        webTestClient.get()
                .uri("/api/v1/catalogs/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CatalogDTO.class)
                .isEqualTo(dto);
    }

    @Test
    void shouldUpdateCatalog() {
        // Given
        UUID id = UUID.randomUUID();
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        Instant createdOn = Instant.now().minusSeconds(3600);
        Instant updatedOn = Instant.now();

        CatalogDTO requestDTO = CatalogDTO.builder()
                .title("Updated Catalog")
                .description("Updated Description with sufficient length for validation")
                .industryId(industryId)
                .industryName("Updated Industry")
                .categories(Arrays.asList("UpdatedCategory"))
                .tags(Arrays.asList("UpdatedTag"))
                .price(BigDecimal.valueOf(59.99))
                .merchantId(merchantId)
                .rating(4.0)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .updatedOn(updatedOn)
                .build();

        Catalog existingEntity = Catalog.builder()
                .id(1L) // Use a dummy ID for testing
                .publicId(id)
                .title("Original Catalog")
                .description("Original Description")
                .industryId(industryId)
                .industryName("Original Industry")
                .categories(Arrays.asList("Category1"))
                .tags(Arrays.asList("Tag1"))
                .price(BigDecimal.valueOf(49.99))
                .merchantId(merchantId)
                .rating(3.5)
                .complianceStatus(ComplianceStatus.NON_COMPLIANT)
                .availabilityStatus(AvailabilityStatus.UNAVAILABLE)
                .createdOn(createdOn)
                .updatedOn(null)
                .build();

        Catalog updatedEntity = Catalog.builder()
                .id(1L) // Use a dummy ID for testing
                .publicId(id)
                .title(requestDTO.title())
                .description(requestDTO.description())
                .industryId(requestDTO.industryId())
                .industryName(requestDTO.industryName())
                .categories(requestDTO.categories())
                .tags(requestDTO.tags())
                .price(requestDTO.price())
                .merchantId(requestDTO.merchantId())
                .rating(requestDTO.rating())
                .complianceStatus(requestDTO.complianceStatus())
                .availabilityStatus(requestDTO.availabilityStatus())
                .createdOn(createdOn)
                .updatedOn(updatedOn)
                .build();

        CatalogDTO responseDTO = CatalogDTO.builder()
                .title(requestDTO.title())
                .description(requestDTO.description())
                .industryId(requestDTO.industryId())
                .industryName(requestDTO.industryName())
                .categories(requestDTO.categories())
                .tags(requestDTO.tags())
                .price(requestDTO.price())
                .merchantId(requestDTO.merchantId())
                .rating(requestDTO.rating())
                .complianceStatus(requestDTO.complianceStatus())
                .availabilityStatus(requestDTO.availabilityStatus())
                .createdOn(createdOn)
                .updatedOn(updatedOn)
                .build();

        // When
        Mockito.when(catalogService.updateByPublicId(Mockito.eq(id), Mockito.any(CatalogDTO.class))).thenReturn(Mono.just(responseDTO));

        // Then
        webTestClient.put()
                .uri("/api/v1/catalogs/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CatalogDTO.class)
                .isEqualTo(responseDTO);
    }

    @Test
    void shouldDeleteCatalog() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        Mockito.when(catalogService.delete(id)).thenReturn(Mono.empty());

        // Then
        webTestClient.delete()
                .uri("/api/v1/catalogs/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldListCatalogs() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID industryId = UUID.randomUUID();
        UUID merchantId = UUID.randomUUID();
        Instant now = Instant.now();

        Catalog entity1 = Catalog.builder()
                .id(1L) // Use a dummy ID for testing
                .publicId(id1)
                .title("Catalog 1")
                .description("Description 1")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(Arrays.asList("Category1"))
                .tags(Arrays.asList("Tag1"))
                .price(BigDecimal.valueOf(19.99))
                .merchantId(merchantId)
                .rating(3.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        Catalog entity2 = Catalog.builder()
                .id(2L) // Use a dummy ID for testing
                .publicId(id2)
                .title("Catalog 2")
                .description("Description 2")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(Arrays.asList("Category2"))
                .tags(Arrays.asList("Tag2"))
                .price(BigDecimal.valueOf(29.99))
                .merchantId(merchantId)
                .rating(4.0)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        CatalogDTO dto1 = CatalogDTO.builder()
                .title("Catalog 1")
                .description("Description 1")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(Arrays.asList("Category1"))
                .tags(Arrays.asList("Tag1"))
                .price(BigDecimal.valueOf(19.99))
                .merchantId(merchantId)
                .rating(3.5)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        CatalogDTO dto2 = CatalogDTO.builder()
                .title("Catalog 2")
                .description("Description 2")
                .industryId(industryId)
                .industryName("Test Industry")
                .categories(Arrays.asList("Category2"))
                .tags(Arrays.asList("Tag2"))
                .price(BigDecimal.valueOf(29.99))
                .merchantId(merchantId)
                .rating(4.0)
                .complianceStatus(ComplianceStatus.COMPLIANT)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .createdOn(now)
                .updatedOn(now)
                .build();

        // When
        Mockito.when(catalogService.findAll(Mockito.any())).thenReturn(Flux.just(dto1, dto2));

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/catalogs")
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CatalogDTO.class)
                .hasSize(2)
                .contains(dto1, dto2);
    }
}