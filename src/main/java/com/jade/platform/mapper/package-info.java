/**
 * This package contains mappers for converting between domain entities and DTOs.
 * 
 * <p>The mappers in this package use MapStruct to generate the mapping code at compile time,
 * which is more efficient than reflection-based mapping libraries.</p>
 * 
 * <p>Key features of the mappers:</p>
 * <ul>
 *   <li>Spring component model for dependency injection</li>
 *   <li>Bidirectional mapping between entities and DTOs</li>
 *   <li>Support for updating existing entities from DTOs</li>
 *   <li>Proper handling of collections with defensive copying</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>
 * {@code
 * @Autowired
 * private CatalogMapper catalogMapper;
 * 
 * // Entity to DTO
 * CatalogDTO dto = catalogMapper.toDto(catalogEntity);
 * 
 * // DTO to Entity
 * Catalog entity = catalogMapper.toEntity(catalogDTO);
 * 
 * // Update existing entity
 * Catalog updatedEntity = catalogMapper.updateEntityFromDto(catalogDTO, existingEntity);
 * }
 * </pre>
 */
package com.jade.platform.mapper;