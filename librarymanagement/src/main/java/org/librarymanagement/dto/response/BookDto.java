package org.librarymanagement.dto.response;

import java.util.Set;

public record BookDto(
        String bookImage,
        String bookTitle,
        String bookDescription,
        Set<String> bookAuthor,
        String bookPublisher
) {

}
