package org.librarymanagement.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.librarymanagement.entity.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {

    // Cập nhật EntityGraph để tải eagerly 'bookAuthors' VÀ 'author' bên trong 'bookAuthors'
    @EntityGraph(attributePaths = {"bookAuthors.author", "publisher"})
    Book findBySlug(String slug);
}
