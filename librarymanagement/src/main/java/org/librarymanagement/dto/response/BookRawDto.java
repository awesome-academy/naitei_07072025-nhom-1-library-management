package org.librarymanagement.dto.response;

public record BookRawDto(
        Integer id,
        String bookImage,
        String bookTitle,
        String bookDescription,
        String bookAuthor,
        String bookPublisher,
        String bookGenre,
        Integer totalCurrent
) {}
