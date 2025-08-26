package org.librarymanagement.service.impl;

import org.apache.poi.ss.usermodel.*;
import org.librarymanagement.constant.*;
import org.librarymanagement.service.*;

import org.librarymanagement.entity.*;
import org.librarymanagement.dto.response.*;
import org.librarymanagement.repository.*;
import org.librarymanagement.dto.response.BookDetailResponse;
import org.librarymanagement.dto.response.ReviewResponse;
import org.librarymanagement.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.librarymanagement.dto.response.BookListDto;
import org.librarymanagement.dto.response.BookRawDto;
import org.librarymanagement.entity.Book;
import org.librarymanagement.entity.BookAuthor;
import org.librarymanagement.entity.Publisher;
import org.librarymanagement.entity.Review;
import org.librarymanagement.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final SlugService slugService;
    private final BookVersionService bookVersionService;
    private final GenreService genreService;
    private final PublisherService publisherService;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService,
                           SlugService slugService, BookVersionService bookVersionService,
                           GenreService genreService, PublisherService publisherService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.slugService = slugService;
        this.bookVersionService = bookVersionService;
        this.genreService = genreService;
        this.publisherService = publisherService;
    }

    public Page<BookListDto> findAllBooksWithFilter(String author, String publisher, String genre, Pageable pageable) {
        Page<BookRawDto> rawBooks = bookRepository.findAllBooksRaw(author, publisher, genre, pageable);

        // Step 1: Group theo sách (title + publisher)
        // Gom authors
        Map<Integer, Set<String>> authorsMap = rawBooks.getContent().stream()
                .filter(dto -> dto.bookAuthor() != null)
                .collect(Collectors.groupingBy(
                        BookRawDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookRawDto::bookAuthor, Collectors.toSet())
                ));

        // Gom genres
        Map<Integer, Set<String>> genresMap = rawBooks.getContent().stream()
                .filter(dto -> dto.bookGenre() != null)
                .collect(Collectors.groupingBy(
                        BookRawDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookRawDto::bookGenre, Collectors.toSet())
                ));

        // Step 2: Map sang BookDto
        List<BookListDto> dtos = rawBooks.getContent().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                BookRawDto::id,
                                dto -> new BookListDto(
                                        dto.id(),
                                        dto.bookImage(),
                                        dto.bookTitle(),
                                        dto.bookDescription(),
                                        authorsMap.getOrDefault(dto.id(), Set.of()), // gom nhiều tác giả
                                        dto.bookPublisher(),
                                        genresMap.getOrDefault(dto.id(), Set.of()),
                                        dto.totalCurrent()
                                ),
                                (existing, newDto) -> existing,
                                LinkedHashMap::new
                        ),
                        m -> new ArrayList<>(m.values())
                ));

        // Step 3: Trả về Page<BookDto>
        return new PageImpl<>(dtos, pageable, rawBooks.getTotalElements());
    }

    @Override
    public Book findBookBySlug(String slug) {
        return bookRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sách với slug: " + slug));
    }
    @Override
    public BookDetailResponse createBookDetailResponseBySlug(String slug) {
        Book book = findBookBySlug(slug);

        Set<BookAuthor> bookAuthors = book.getBookAuthors();

        List<String> authorNames = new ArrayList<>();

        authorNames = bookAuthors.stream()
                .map(bookAuthor -> bookAuthor.getAuthor().getName())
                .collect(Collectors.toList());

        Publisher publisher = book.getPublisher();

        String publisherName = (publisher != null) ? publisher.getName() : null;

        Set<Review> reviews = book.getReviews();

        Set<ReviewResponse> reviewResponses = convertReviewsToDtos(reviews);

        BookDetailResponse bookDetailResponse = new BookDetailResponse(
                book.getId(),
                book.getImage(),
                book.getTitle(),
                book.getTotalCurrent(),
                book.getTotalQuantity(),
                book.getDescription(),
                book.getPublishedDay(),
                authorNames,
                publisherName,
                reviewResponses
        );

        return bookDetailResponse;
    }

    private Set<ReviewResponse> convertReviewsToDtos(Set<Review> reviews) {
        if (reviews == null) {
            return Collections.emptySet();
        }

        Set<ReviewResponse> reviewResponses = new HashSet<>();

        reviewResponses = reviews.stream()
                .map(review -> new ReviewResponse(
                        review.getId(),
                        review.getComment(),
                        review.getStar(),
                        review.getCreatedAt(),
                        new UserResponse(
                                review.getUser().getId(),
                                review.getUser().getName(),
                                review.getUser().getUsername()
                        )
                ))
                .collect(Collectors.toSet());

        return reviewResponses;
    }

    public void importBooksFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // Bỏ dòng header
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Book book = new Book();
                book.setTitle(getCellValueAsString(row.getCell(0)));
                book.setDescription(getCellValueAsString(row.getCell(4)));

                // --- Publisher ---
                String publisherName = getCellValueAsString(row.getCell(5));
                Publisher publisher = publisherService.findOrCreatePublisher(publisherName);
                book.setPublisher(publisher);

                // --- Published Day ---
                String publishedDayStr = getCellValueAsString(row.getCell(6));
                if (publishedDayStr != null && !publishedDayStr.isEmpty()) {
                    book.setPublishedDay(LocalDate.parse(publishedDayStr));
                } else {
                    book.setPublishedDay(LocalDate.now());
                }

                // --- Quantity ---
                Integer totalQuantity = parseInteger(getCellValueAsString(row.getCell(7)));
                book.setTotalQuantity(totalQuantity);
                book.setTotalCurrent(totalQuantity);

                // --- Image ---
                book.setImage(getCellValueAsString(row.getCell(8)));

                // --- Slug ---
                String slug = slugService.generateUniqueSlug(book.getTitle());
                book.setSlug(slug);

                // --- Author (status=1) ---
                String authorName = getCellValueAsString(row.getCell(1));
                if (authorName != null && !authorName.isEmpty()) {
                    Author author = authorService.findOrCreateAuthor(authorName);
                    BookAuthor bookAuthor = new BookAuthor();
                    bookAuthor.setBook(book);
                    bookAuthor.setAuthor(author);
                    bookAuthor.setStatus(AuthorConstants.AUTHOR);
                    book.getBookAuthors().add(bookAuthor);
                }

                // --- Coauthor (status=2, có thể nhiều, cách nhau dấu ,) ---
                String coAuthors = getCellValueAsString(row.getCell(2));
                if (coAuthors != null && !coAuthors.isEmpty()) {
                    String[] coAuthorArr = coAuthors.split(",");
                    for (String co : coAuthorArr) {
                        String coName = co.trim();
                        if (coName.isEmpty()) continue;
                        Author coAuthor = authorService.findOrCreateAuthor(coName);
                        BookAuthor bookAuthor = new BookAuthor();
                        bookAuthor.setBook(book);
                        bookAuthor.setAuthor(coAuthor);
                        bookAuthor.setStatus(AuthorConstants.COAUTHOR);
                        book.getBookAuthors().add(bookAuthor);
                    }
                }

                // --- Genres (có thể nhiều, cách nhau dấu ,) ---
                String genres = getCellValueAsString(row.getCell(3));
                if (genres != null && !genres.isEmpty()) {
                    String[] genreArr = genres.split(",");
                    for (String g : genreArr) {
                        String gName = g.trim();
                        if (gName.isEmpty()) continue;
                        Genre genre = genreService.findOrCreateGenre(gName);
                        BookGenre bookGenre = new BookGenre();
                        bookGenre.setBook(book);
                        bookGenre.setGenre(genre);
                        book.getBookGenres().add(bookGenre);
                    }
                }

                bookRepository.save(book);

                // Tạo book_versions
                bookVersionService.createBookVersions(book, book.getTotalQuantity(), BookVersionConstants.AVAILABLE);
            }
        }
    }
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK, _NONE, ERROR -> null;
        };
    }

    private Integer parseInteger(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /*Tìm kiếm sách - 91162*/
    @Override
    @Transactional(readOnly = true)
    public List<BookResponseDto> searchBooks(String keyword) {
        List<BookSearchFlatDto> flat = bookRepository.searchBooks(keyword);

        if(flat.isEmpty()){
            throw new NotFoundException("Không tìm thấy sách với từ khóa " + keyword);
        }

        Map<Integer, Set<String>> authorsMap = flat.stream()
                .filter(dto -> dto.bookAuthor() != null)
                .collect(Collectors.groupingBy(
                        BookSearchFlatDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookSearchFlatDto::bookAuthor, Collectors.toSet())
                ));

        Map<Integer, Set<String>> genresMap = flat.stream()
                .filter(dto -> dto.bookGenre() != null)
                .collect(Collectors.groupingBy(
                        BookSearchFlatDto::id,
                        LinkedHashMap::new,
                        Collectors.mapping(BookSearchFlatDto::bookGenre, Collectors.toSet())
                ));

        List<BookResponseDto> results = flat.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                BookSearchFlatDto::id,
                                dto -> new BookResponseDto(
                                        dto.id(),
                                        dto.image(),
                                        dto.title(),
                                        dto.description(),
                                        dto.publishedDay(),
                                        dto.publisherName(),
                                        authorsMap.getOrDefault(dto.id(), Set.of()),
                                        genresMap.getOrDefault(dto.id(), Set.of())
                                ),
                                (existing, newDto) -> existing,
                                LinkedHashMap::new
                        ),
                        m -> new ArrayList<>(m.values())
                ));
        return results;
    }
}
