package org.librarymanagement.repository;

import org.librarymanagement.dto.response.BookDto;
import org.librarymanagement.entity.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository  extends CrudRepository<Book,Integer> {

        @Query(value = "SELECT b.image , b.title , b.description, " +
                "GROUP_CONCAT(a.name) AS author, p.name AS publisher " +
                "FROM books b " +
                "JOIN book_authors ba ON ba.book_id = b.id " +
                "JOIN authors a ON ba.author_id = a.id " +
                "JOIN publishers p ON b.publisher_id = p.id " +
                "GROUP BY b.id,p.name",nativeQuery = true)
        List<Object[]> findAllBooksWithFilter();
}
