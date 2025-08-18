/**
 * This package contains Data Transfer Objects (DTOs) for the booking platform.
 * 
 * <p>DTOs are used to transfer data between the service layer and the client.
 * They are designed to be serialized and deserialized easily, and include
 * validation constraints to ensure data integrity.</p>
 * 
 * <p>The DTOs in this package follow these principles:</p>
 * <ul>
 *   <li>Implemented as Java records for immutability</li>
 *   <li>Include Jakarta Validation constraints for input validation</li>
 *   <li>Provide defensive copying for collections to ensure immutability</li>
 *   <li>Separate from domain entities to decouple the API from the domain model</li>
 * </ul>
 */
package com.jade.platform.dto;