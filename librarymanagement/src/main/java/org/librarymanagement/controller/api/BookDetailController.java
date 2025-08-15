package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.BookDetailResponse;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookAuthor;
import org.librarymanagement.entity.Publisher;
import org.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(ApiEndpoints.USER_BOOK)
public class BookDetailController {

    private final BookService bookService;

    @Autowired
    public BookDetailController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BookDetailResponse> getBook(@PathVariable String slug) {

        Book book = bookService.findBookBySlug(slug);

        Set<BookAuthor> bookAuthors = book.getBookAuthors();

        List<String> authorName = new ArrayList<>();

        for(BookAuthor bookAuthor : bookAuthors) {
            authorName.add(bookAuthor.getAuthor().getName());
        }

        Publisher publisher = book.getPublisher();

        if(publisher == null) {
            return ResponseEntity.notFound().build();
        }

        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                book.getId(),
                book.getImage(),
                book.getTitle(),
                book.getTotalCurrent(),
                book.getTotalQuantity(),
                book.getDescription(),
                book.getPublishedDay(),
                authorName,
                publisher.getName()
        );
        return ResponseEntity.ok(bookDetailResponse);
    }
}

