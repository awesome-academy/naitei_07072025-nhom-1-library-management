package org.librarymanagement.dto.response;

import java.time.LocalDate;
import java.util.List;

public record BookDetailResponse(
        Integer id,
        String image,
        String title,
        Integer totalCurrent,
        Integer totalQuantity,
        String description,
        LocalDate publishedDay,
        List<String> authorName,
        String publisherName
) {}
