package org.librarymanagement.service;

import org.librarymanagement.dto.response.BookDetailResponse;
import org.springframework.data.domain.Page;
import org.librarymanagement.dto.response.BookListDto;
import org.springframework.data.domain.Pageable;
import org.librarymanagement.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import org.librarymanagement.dto.response.BookResponseDto;
import java.util.List;

public interface BookService {
    Page<BookListDto> findAllBooksWithFilter(String author, String publisher, String genre, Pageable pageable);
    BookDetailResponse createBookDetailResponseBySlug(String slug);
    Book findBookBySlug(String slug);
    void importBooksFromExcel(MultipartFile file) throws IOException;
    List<BookResponseDto> searchBooks(String keyword);
}
