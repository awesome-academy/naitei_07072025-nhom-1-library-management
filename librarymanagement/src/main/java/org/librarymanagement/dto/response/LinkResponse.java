package org.librarymanagement.dto.response;

public record LinkResponse(
        String href,
        String rel,
        String title
) {}
