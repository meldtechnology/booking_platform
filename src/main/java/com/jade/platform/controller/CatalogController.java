package com.jade.platform.controller;

import com.jade.platform.domain.enums.AvailabilityStatus;
import com.jade.platform.domain.enums.ComplianceStatus;
import com.jade.platform.dto.CatalogDTO;
import com.jade.platform.dto.CatalogFilter;
import com.jade.platform.dto.CreateCatalogDto;
import com.jade.platform.dto.PageResponse;
import com.jade.platform.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalogs")
@Tag(name = "Catalog", description = "Catalog management API")
class CatalogController {
    private static final Logger log = LoggerFactory.getLogger(CatalogController.class);
    
    private final CatalogService catalogService;
    
    CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
    @Operation(
        summary = "Get all catalog items with pagination and sorting",
        description = "Returns a paginated list of all catalog items with sorting capabilities. " +
                "Default pagination values are provided if not specified. " +
                "Sorting can be applied using the sort and direction parameters. " +
                "Multiple sort fields can be specified by separating them with commas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved catalog items",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping
    Flux<CatalogDTO> getAllCatalogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        log.debug("REST request to get all Catalogs with pagination: page={}, size={}, sort={}, direction={}", 
                page, size, sort, direction);
        
        PageRequest pageRequest;
        if (sort != null && !sort.isEmpty()) {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
            
            // Handle multiple sort fields separated by commas
            String[] sortFields = sort.split(",");
            if (sortFields.length > 1) {
                Sort multiSort = Sort.by(sortDirection, sortFields[0].trim());
                for (int i = 1; i < sortFields.length; i++) {
                    multiSort = multiSort.and(Sort.by(sortDirection, sortFields[i].trim()));
                }
                pageRequest = PageRequest.of(page, size, multiSort);
            } else {
                pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort.trim()));
            }
        } else {
            pageRequest = PageRequest.of(page, size);
        }
        
        return catalogService.findAll(pageRequest);
    }
    
    @Operation(
        summary = "Get a catalog item by ID - Public Id",
        description = "Returns a single catalog item identified by its UUID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved the catalog item",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Catalog item not found",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping("/{id}")
    Mono<CatalogDTO> getCatalogById(@PathVariable UUID id) {
        log.debug("REST request to get Catalog by ID: {}", id);
        return catalogService.findByPublicId(id);
    }
    
    @Operation(
        summary = "Create a new catalog item",
        description = "Creates a new catalog item with the provided data. System fields (publicId, createdOn, updatedOn) are automatically generated."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Catalog item created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<CatalogDTO> createCatalog(@Valid @RequestBody CreateCatalogDto createCatalogDto) {
        log.debug("REST request to create Catalog: {}", createCatalogDto);
        return catalogService.create(createCatalogDto);
    }
    
    @Operation(
        summary = "Create multiple catalog items in a single request",
        description = "Creates multiple catalog items with the provided data in a single batch operation. System fields (publicId, createdOn, updatedOn) are automatically generated."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Catalog items created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    Flux<CatalogDTO> createCatalogsBatch(@Valid @RequestBody List<CreateCatalogDto> createCatalogDtos) {
        log.debug("REST request to create Catalogs in batch: {}", createCatalogDtos);
        return Flux.fromIterable(createCatalogDtos)
                .flatMap(catalogService::create);
    }
    
    @Operation(
        summary = "Update an existing catalog item",
        description = "Updates an existing catalog item identified by its UUID with the provided data"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Catalog item updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Catalog item not found",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PutMapping("/{id}")
    Mono<CatalogDTO> updateCatalog(
            @PathVariable UUID id, 
            @Valid @RequestBody CatalogDTO catalogDTO) {
        log.debug("REST request to update Catalog with ID: {}", id);
        return catalogService.updateByPublicId(id, catalogDTO);
    }
    
    @Operation(
        summary = "Update multiple catalog items in a single request",
        description = "Updates multiple catalog items with the provided data in a single batch operation. " +
                "Each DTO must include its ID to identify which catalog item to update."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Catalog items updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "One or more catalog items not found",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @PutMapping("/batch")
    Flux<CatalogDTO> updateCatalogsBatch(@Valid @RequestBody List<CatalogDTO> catalogDTOs) {
        log.debug("REST request to update Catalogs in batch: {}", catalogDTOs);
        return Flux.fromIterable(catalogDTOs)
                .flatMap(dto -> {
                    if (dto.publicId() == null) {
                        return Mono.error(new IllegalArgumentException("Public ID must be provided for batch update"));
                    }
                    return catalogService.updateByPublicId(dto.publicId(), dto);
                });
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> deleteCatalog(@PathVariable UUID id) {
        log.debug("REST request to delete Catalog with ID: {}", id);
        return catalogService.deleteByPublicId(id);
    }
    
    @Operation(
        summary = "Delete multiple catalog items in a single request",
        description = "Deletes multiple catalog items identified by their UUIDs in a single batch operation"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "Catalog items deleted successfully"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid input data",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "One or more catalog items not found",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> deleteCatalogsBatch(@RequestParam List<UUID> ids) {
        log.debug("REST request to delete Catalogs in batch: {}", ids);
        return Flux.fromIterable(ids)
                .flatMap(catalogService::deleteByPublicId)
                .then();
    }
    
    @GetMapping("/search/title/{title}")
    Flux<CatalogDTO> getCatalogsByTitle(@PathVariable String title) {
        log.debug("REST request to get Catalogs by title: {}", title);
        return catalogService.findByTitle(title);
    }
    
    @GetMapping("/search/industry/{industryId}")
    Flux<CatalogDTO> getCatalogsByIndustryId(@PathVariable UUID industryId) {
        log.debug("REST request to get Catalogs by industry ID: {}", industryId);
        return catalogService.findByIndustryId(industryId);
    }
    
    @Operation(
        summary = "Find catalog items by filter criteria with sorting",
        description = "Returns catalog items that match the specified filter criteria with sorting capabilities. " +
                "All filter parameters are optional. " +
                "Sorting can be applied using the sort and direction parameters. " +
                "Multiple sort fields can be specified by separating them with commas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved filtered catalog items",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CatalogDTO.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping("/filter")
    Flux<CatalogDTO> filterCatalogs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) UUID industryId,
            @RequestParam(required = false) String industryName,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) ComplianceStatus complianceStatus,
            @RequestParam(required = false) AvailabilityStatus availabilityStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to filter Catalogs with criteria and sorting: sort={}, direction={}", sort, direction);
        
        CatalogFilter filter = CatalogFilter.builder()
                .title(title)
                .description(description)
                .industryId(industryId)
                .industryName(industryName)
                .categories(categories)
                .tags(tags)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .merchantId(merchantId)
                .minRating(minRating)
                .maxRating(maxRating)
                .complianceStatus(complianceStatus)
                .availabilityStatus(availabilityStatus)
                .build();
        
        PageRequest pageRequest;
        if (sort != null && !sort.isEmpty()) {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
            
            // Handle multiple sort fields separated by commas
            String[] sortFields = sort.split(",");
            if (sortFields.length > 1) {
                Sort multiSort = Sort.by(sortDirection, sortFields[0].trim());
                for (int i = 1; i < sortFields.length; i++) {
                    multiSort = multiSort.and(Sort.by(sortDirection, sortFields[i].trim()));
                }
                pageRequest = PageRequest.of(page, size, multiSort);
            } else {
                pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort.trim()));
            }
        } else {
            pageRequest = PageRequest.of(page, size);
        }
        
        return catalogService.findByFilter(filter, pageRequest);
    }
    
    @Operation(
        summary = "Find catalog items by filter criteria with pagination metadata and sorting",
        description = "Returns a PageResponse containing catalog items that match the specified filter criteria " +
                "along with pagination metadata and sorting capabilities. " +
                "All filter parameters are optional. " +
                "Sorting can be applied using the sort and direction parameters. " +
                "Multiple sort fields can be specified by separating them with commas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successfully retrieved filtered catalog items with pagination metadata",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))
        )
    })
    @GetMapping("/filter/paged")
    Mono<PageResponse<CatalogDTO>> filterCatalogsPaged(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) UUID industryId,
            @RequestParam(required = false) String industryName,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) UUID merchantId,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) ComplianceStatus complianceStatus,
            @RequestParam(required = false) AvailabilityStatus availabilityStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        log.debug("REST request to filter Catalogs with criteria, pagination metadata, and sorting: sort={}, direction={}", 
                sort, direction);
        
        CatalogFilter filter = CatalogFilter.builder()
                .title(title)
                .description(description)
                .industryId(industryId)
                .industryName(industryName)
                .categories(categories)
                .tags(tags)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .merchantId(merchantId)
                .minRating(minRating)
                .maxRating(maxRating)
                .complianceStatus(complianceStatus)
                .availabilityStatus(availabilityStatus)
                .build();
        
        PageRequest pageRequest;
        if (sort != null && !sort.isEmpty()) {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
            
            // Handle multiple sort fields separated by commas
            String[] sortFields = sort.split(",");
            if (sortFields.length > 1) {
                Sort multiSort = Sort.by(sortDirection, sortFields[0].trim());
                for (int i = 1; i < sortFields.length; i++) {
                    multiSort = multiSort.and(Sort.by(sortDirection, sortFields[i].trim()));
                }
                pageRequest = PageRequest.of(page, size, multiSort);
            } else {
                pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sort.trim()));
            }
        } else {
            pageRequest = PageRequest.of(page, size);
        }
        
        return catalogService.findByFilterPaged(filter, pageRequest);
    }
}