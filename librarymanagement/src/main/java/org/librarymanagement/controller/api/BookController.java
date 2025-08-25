package org.librarymanagement.controller.api;

import org.librarymanagement.dto.response.PageResponse;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.service.BorrowService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Map;
import java.util.List;

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
    public ResponseEntity<ResponseObject> searchBooks(@RequestParam String keyword, Locale locale) {
        List<BookResponseDto> books = bookService.searchBooks(keyword);

        String successMessage = messageSource.getMessage("book.query.success", null, locale);
        ResponseObject responseObject = new ResponseObject(successMessage, HttpStatus.OK.value(), books);

        return ResponseEntity.ok(responseObject);
    }
}
