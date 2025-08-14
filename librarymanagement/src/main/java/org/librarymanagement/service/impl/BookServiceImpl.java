package org.librarymanagement.service.impl;

import org.librarymanagement.dto.response.BookDto;
import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookDto> findAllBooksWithFilter() {
        List<Object[]> temp = bookRepository.findAllBooksWithFilter();
        List<BookDto> bookDtos = new ArrayList<>();

        for (Object[] obj : temp) {
            String bookImage = (String) obj[0];
            String bookName = (String) obj[1];
            String bookDescription = (String) obj[2];
            String bookAuthor = (String) obj[3];
            String bookPublisher = (String) obj[4];

            Set<String> authors = new HashSet<>(Arrays.asList(bookAuthor.split(", ")));

            bookDtos.add(new BookDto(bookImage,bookName,bookDescription,authors,bookPublisher));
        }
    return bookDtos;
    }
}
