package org.librarymanagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record BookVersionResponse(
        String status,
        String title,
        String publisherName,
        LinkResponse reviewLink
) {}
