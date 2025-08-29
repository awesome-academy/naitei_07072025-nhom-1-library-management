package org.librarymanagement.dto.response;

import java.time.LocalDate;

public record PublisherBookResponse(
        String title,
        LocalDate publishedDay
) {}
