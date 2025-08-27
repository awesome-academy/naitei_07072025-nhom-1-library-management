package org.librarymanagement.dto.response;

import java.time.LocalDateTime;

public record BorrowHistoryDto(
        String bookTitle,
        String bookImage,
        String bookSlug,
        LocalDateTime borrowedDate,
        LocalDateTime expectedReturnDate,
        LocalDateTime actualReturnDate,
        Integer status,
        String derivedStatus
) {}
