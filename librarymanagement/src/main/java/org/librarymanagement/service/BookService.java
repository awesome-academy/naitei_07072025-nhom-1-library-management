package org.librarymanagement.service;

import org.librarymanagement.entity.Book;

public interface BookService {
    public Book findBookBySlug(String slug);
}
