package com.jade.platform.mapper;

import com.jade.platform.domain.model.Catalog;
import com.jade.platform.dto.CatalogDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CatalogMapperImpl implements CatalogMapper {

    @Override
    public CatalogDTO toDto(Catalog catalog) {
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

    @Override
    public Catalog toEntity(CatalogDTO catalogDTO) {
        if (catalogDTO == null) {
            return null;
        }

        return Catalog.builder()
                .title(catalogDTO.title())
                .description(catalogDTO.description())
                .industryId(catalogDTO.industryId())
                .industryName(catalogDTO.industryName())
                .categories(catalogDTO.categories())
                .tags(catalogDTO.tags())
                .price(catalogDTO.price())
                .merchantId(catalogDTO.merchantId())
                .rating(catalogDTO.rating())
                .complianceStatus(catalogDTO.complianceStatus())
                .availabilityStatus(catalogDTO.availabilityStatus())
                .createdOn(catalogDTO.createdOn())
                .updatedOn(catalogDTO.updatedOn())
                .build();
    }

    @Override
    public Catalog updateEntityFromDto(CatalogDTO catalogDTO, Catalog catalog) {
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