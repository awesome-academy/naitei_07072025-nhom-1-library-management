package org.librarymanagement.dto.response;

import java.util.List;

public record PageResponse<T>(
        String message,
        int status,
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}
