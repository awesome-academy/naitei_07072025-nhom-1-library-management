package org.librarymanagement.dto.response;

import java.time.LocalDateTime;

public record BorrowRequestItemDto (
        Integer id,
        String bookTitle,
        String bookAuthor,
        String publisher,
        LocalDateTime publishDate,
        Integer availableQuantity,

        LocalDateTime dayStart,
        LocalDateTime dayEnd,
        Integer quantity,
        Integer status
) {
}