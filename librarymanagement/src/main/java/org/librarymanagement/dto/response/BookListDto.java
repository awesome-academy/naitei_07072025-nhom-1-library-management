package org.librarymanagement.dto.response;

import java.util.Set;

public record BookListDto(
        Integer id,
        String bookImage,
        String bookTitle,
        String bookDescription,
        Set<String> bookAuthors,
        String bookPublisher,
        Set<String> bookGenre,
        Integer totalCurrent
) { }
