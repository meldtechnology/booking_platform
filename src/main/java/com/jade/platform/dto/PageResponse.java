package com.jade.platform.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public PageResponse {
        // Defensive copy for immutability
        content = List.copyOf(content);
        
        // Calculate total pages
        if (size > 0) {
            int calculatedTotalPages = (int) Math.ceil((double) totalElements / size);
            if (calculatedTotalPages != totalPages) {
                totalPages = calculatedTotalPages;
            }
        }
    }
    
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new PageResponse<>(content, page, size, totalElements, totalPages);
    }
}