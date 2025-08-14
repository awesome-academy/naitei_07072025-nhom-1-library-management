package org.librarymanagement.controller.api;

import org.librarymanagement.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.librarymanagement.dto.response.BookDto;
import org.librarymanagement.exception.NotFoundException;
import org.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.librarymanagement.constant.ApiEndpoints;
import java.util.Locale;

@RestController
@RequestMapping(ApiEndpoints.USER_BOOK)
public class BookApiController {
    private final BookService  bookService;
    private final MessageSource messageSource;

    @Autowired
    public BookApiController(BookService bookService, MessageSource messageSource) {
        this.bookService = bookService;
        this.messageSource = messageSource;
    }

    @GetMapping("/all")
    public ResponseEntity<PageResponse<BookDto>> findAllBooks(Pageable pageable,Locale locale) {
        Page<BookDto> listBooks = bookService.findAllBooksWithFilter(pageable);

        if(listBooks.getTotalElements() == 0) {
            throw new NotFoundException("");
        }

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
}
