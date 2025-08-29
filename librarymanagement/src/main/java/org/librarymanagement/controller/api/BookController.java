package org.librarymanagement.controller.api;

import org.librarymanagement.dto.response.PageResponse;
import org.librarymanagement.service.CurrentUserService;
import org.springframework.data.domain.Page;
import org.librarymanagement.dto.response.BookListDto;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BookResponseDto;
import org.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping(ApiEndpoints.USER_BOOK)
public class BookController {

    private final BookService  bookService;
    private final CurrentUserService currentUserService;

    private final MessageSource messageSource;

    @Autowired
    public BookController(BookService bookService, CurrentUserService currentUserService,
                          MessageSource messageSource) {
        this.bookService = bookService;
        this.currentUserService = currentUserService;
        this.messageSource = messageSource;
    }

    @GetMapping("/")
    public ResponseEntity<PageResponse<BookListDto>> findAllBooks(Pageable pageable, Locale locale) {
        Page<BookListDto> listBooks = bookService.findAllBooksWithFilter(null, null, null, pageable);

        String successMessage = messageSource.getMessage("book.query.success", null, locale);
        return ResponseEntity.ok(new PageResponse<>(
                successMessage,
                HttpStatus.OK.value(),
                listBooks.getContent(),
                listBooks.getNumber(),
                listBooks.getSize(),
                listBooks.getTotalElements(),
                listBooks.getTotalPages()
        ));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponseDto>> searchBooks(@RequestParam String keyword, Pageable pageable, Locale locale) {
        Page<BookResponseDto> books = bookService.searchBooks(keyword, pageable);

        String successMessage = messageSource.getMessage("book.query.success", null, locale);

        return ResponseEntity.ok(new PageResponse<>(
                successMessage,
                HttpStatus.OK.value(),
                books.getContent(),
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages()
        ));
    }
}
