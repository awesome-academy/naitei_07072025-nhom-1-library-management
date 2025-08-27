package org.librarymanagement.dto.request;

public record BookFilterRequest (
        String author,
        String publisher,
        String genre
) {
    @Override
    public String author() {
        return (author == null || author.isBlank()) ? null : author;
    }

    @Override
    public String publisher() {
        return (publisher == null || publisher.isBlank()) ? null : publisher;
    }

    @Override
    public String genre() {
        return (genre == null || genre.isBlank()) ? null : genre;
    }
}
