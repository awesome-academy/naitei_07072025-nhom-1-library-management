package org.librarymanagement.service;

import org.springframework.data.domain.Page;
import org.librarymanagement.dto.response.BookDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookDto> findAllBooksWithFilter(Pageable pageable);
}
