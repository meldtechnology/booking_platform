package com.jade.platform.mapper;

import com.jade.platform.domain.model.Catalog;
import com.jade.platform.dto.CatalogDTO;
import com.jade.platform.dto.CreateCatalogDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CatalogMapper {

    CatalogDTO toDto(Catalog catalog);
    
    /**
     * Maps a CreateCatalogDto to a CatalogDTO.
     * This is useful for converting between DTO types.
     */
    CatalogDTO dtoFromCreateDto(CreateCatalogDto createCatalogDto);
    
    /**
     * Maps a CreateCatalogDto to a new Catalog entity.
     * This method is used for creating new catalog items.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publicId", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    Catalog toEntityFromCreateDto(CreateCatalogDto createCatalogDto);
    
    /**
     * Creates a new Catalog entity from CreateCatalogDto with system-generated fields.
     */
    default Catalog toNewEntity(CreateCatalogDto createCatalogDto) {
        if (createCatalogDto == null) {
            return null;
        }
        
        return Catalog.builder()
                .publicId(UUID.randomUUID())
                .title(createCatalogDto.title())
                .description(createCatalogDto.description())
                .industryId(createCatalogDto.industryId())
                .industryName(createCatalogDto.industryName())
                .categories(createCatalogDto.categories())
                .tags(createCatalogDto.tags())
                .price(createCatalogDto.price())
                .merchantId(createCatalogDto.merchantId())
                .rating(createCatalogDto.rating())
                .complianceStatus(createCatalogDto.complianceStatus())
                .availabilityStatus(createCatalogDto.availabilityStatus())
                .createdOn(Instant.now())
                .updatedOn(Instant.now())
                .build();
    }
    
    default CatalogDTO toDtoWithoutId(Catalog catalog) {
        if (catalog == null) {
            return null;
        }
        return CatalogDTO.builder()
                .publicId(catalog.publicId())
                .title(catalog.title())
                .description(catalog.description())
                .industryId(catalog.industryId())
                .industryName(catalog.industryName())
                .categories(catalog.categories())
                .tags(catalog.tags())
                .price(catalog.price())
                .merchantId(catalog.merchantId())
                .rating(catalog.rating())
                .complianceStatus(catalog.complianceStatus())
                .availabilityStatus(catalog.availabilityStatus())
                .createdOn(catalog.createdOn())
                .updatedOn(catalog.updatedOn())
                .build();
    }

    @Mapping(target = "id", ignore = true)
    Catalog toEntity(CatalogDTO catalogDTO);
    
    /**
     * Since Catalog is a record (immutable), we need to implement this method manually
     * in the implementation class. MapStruct will generate the implementation.
     */
    default Catalog updateEntityFromDto(CatalogDTO catalogDTO, Catalog catalog) {
        if (catalogDTO == null) {
            return catalog;
        }
        
        return new Catalog(
            catalog.id(),  // Keep the original ID
            catalog.publicId(),  // Keep the original publicId
            catalogDTO.title() != null ? catalogDTO.title() : catalog.title(),
            catalogDTO.description() != null ? catalogDTO.description() : catalog.description(),
            catalogDTO.industryId() != null ? catalogDTO.industryId() : catalog.industryId(),
            catalogDTO.industryName() != null ? catalogDTO.industryName() : catalog.industryName(),
            catalogDTO.categories() != null ? catalogDTO.categories() : catalog.categories(),
            catalogDTO.tags() != null ? catalogDTO.tags() : catalog.tags(),
            catalogDTO.price() != null ? catalogDTO.price() : catalog.price(),
            catalogDTO.merchantId() != null ? catalogDTO.merchantId() : catalog.merchantId(),
            catalogDTO.rating(),
            catalogDTO.complianceStatus() != null ? catalogDTO.complianceStatus() : catalog.complianceStatus(),
            catalogDTO.availabilityStatus() != null ? catalogDTO.availabilityStatus() : catalog.availabilityStatus(),
            catalog.createdOn(),  // Keep the original creation timestamp
            catalogDTO.updatedOn() != null ? catalogDTO.updatedOn() : catalog.updatedOn()
        );
    }
}