package org.librarymanagement.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.librarymanagement.dto.response.BookFlatDto;
import org.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository  extends CrudRepository<Book,Integer> {

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
}
