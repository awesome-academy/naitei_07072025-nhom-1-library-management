package org.librarymanagement.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.librarymanagement.dto.response.BookDto;
import org.librarymanagement.dto.response.BookFlatDto;
import org.librarymanagement.repository.BookRepository;
import org.librarymanagement.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<BookDto> findAllBooksWithFilter(Pageable pageable) {
        Page<BookFlatDto> rawBooks = bookRepository.findAllBooksFlat(pageable);

        // Step 1: Group theo sách (title + publisher) và gom tác giả
        Map<String, Set<String>> authorsMap = rawBooks.getContent().stream()
                .collect(Collectors.groupingBy(
                        dto -> dto.bookTitle() + "|" + dto.bookPublisher(), // key duy nhất cho 1 sách
                        LinkedHashMap::new,
                        Collectors.mapping(BookFlatDto::bookAuthor, Collectors.toSet())
                ));

        // Step 2: Map sang BookDto
        List<BookDto> dtos = rawBooks.getContent().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                dto -> dto.bookTitle() + "|" + dto.bookPublisher(),
                                dto -> new BookDto(
                                        dto.bookImage(),
                                        dto.bookTitle(),
                                        dto.bookDescription(),
                                        authorsMap.get(dto.bookTitle() + "|" + dto.bookPublisher()), // Set tác giả
                                        dto.bookPublisher()
                                ),
                                (existing, newDto) -> existing, // nếu trùng key, giữ existing
                                LinkedHashMap::new
                        ),
                        m -> new ArrayList<>(m.values())
                ));

        // Step 3: Trả về Page<BookDto>
        return new PageImpl<>(dtos, pageable, rawBooks.getTotalElements());
    }
}
