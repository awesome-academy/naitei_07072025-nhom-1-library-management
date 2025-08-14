package org.librarymanagement.dto.response;

public record ResponseObject(
        String message,
        Integer status,
        Object data
) { }
