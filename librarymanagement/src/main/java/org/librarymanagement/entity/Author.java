package org.librarymanagement.entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "authors")
@Data
@EqualsAndHashCode(exclude = {"bookAuthors"})
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    private String bio;

    @Column(name = "created_at",  nullable = false)
    private LocalDateTime  createdAt;

    private String slug;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookAuthor> bookAuthors;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
