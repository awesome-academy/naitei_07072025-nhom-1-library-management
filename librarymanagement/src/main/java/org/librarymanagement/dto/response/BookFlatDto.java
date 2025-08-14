package org.librarymanagement.dto.response;

public record BookFlatDto(
        String bookImage,
        String bookTitle,
        String bookDescription,
        String bookAuthor,
        String bookPublisher
) {}
