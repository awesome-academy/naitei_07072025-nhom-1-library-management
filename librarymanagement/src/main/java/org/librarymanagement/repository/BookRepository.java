package org.librarymanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.librarymanagement.dto.response.BookFlatDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.librarymanagement.dto.response.BookSearchFlatDto;
import org.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {

        @Query("""
    SELECT new org.librarymanagement.dto.response.BookFlatDto(
        b.image,
        b.title,
        b.description,
        a.name,
        p.name
    )
    FROM Book b
    JOIN b.bookAuthors ba
    JOIN ba.author a
    JOIN b.publisher p
""")
        Page<BookFlatDto> findAllBooksFlat(Pageable pageable);
    // Cập nhật EntityGraph để tải eagerly 'bookAuthors' VÀ 'author' bên trong 'bookAuthors'
    @EntityGraph(attributePaths = {"bookAuthors.author", "publisher"})
    Optional<Book> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            SELECT new org.librarymanagement.dto.response.BookSearchFlatDto(
                b.id,
                b.image,
                b.title,
                b.description,
                b.publishedDay,
                p.name,
                a.name,
                g.name
            )
            FROM Book b
            LEFT JOIN b.publisher p
            LEFT JOIN b.bookAuthors ba LEFT JOIN ba.author a
            LEFT JOIN b.bookGenres bg LEFT JOIN bg.genre g
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(g.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<BookSearchFlatDto> searchBooks(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT new org.librarymanagement.dto.response.BookSearchFlatDto(
                b.id,
                b.image,
                b.title,
                b.description,
                b.publishedDay,
                p.name,
                a.name,
                g.name
            )
            FROM Book b
            LEFT JOIN b.publisher p
            LEFT JOIN b.bookAuthors ba LEFT JOIN ba.author a
            LEFT JOIN b.bookGenres bg LEFT JOIN bg.genre g
            WHERE b.id IN :bookIds
    """)
    List<BookSearchFlatDto> findAllBookDataByIds(@Param("bookIds") List<Integer> bookIds);
}
