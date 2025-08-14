package org.librarymanagement.service;

import org.librarymanagement.dto.response.BookDto;

import java.util.List;

public interface BookService {
    List<BookDto> findAllBooksWithFilter();
}
