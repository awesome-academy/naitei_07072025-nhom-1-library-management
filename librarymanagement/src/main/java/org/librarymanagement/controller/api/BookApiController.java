package org.librarymanagement.controller.api;

import org.librarymanagement.dto.response.BookDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.librarymanagement.constant.ApiEndpoints;

import java.util.List;
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

    @GetMapping("/library")
    public ResponseEntity<ResponseObject> findAllBooks() {
        List<BookDto> listBooks = bookService.findAllBooksWithFilter();
        String successMessage = messageSource.getMessage("book.query.success", null, Locale.getDefault());
        return ResponseEntity.ok( new ResponseObject(
                successMessage,
                HttpStatus.OK.value(),
                listBooks
        ));
    }
}
