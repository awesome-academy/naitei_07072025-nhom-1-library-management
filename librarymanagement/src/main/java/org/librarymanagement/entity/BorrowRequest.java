package org.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "borrow_requests")
@Data
public class BorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "day_start", nullable = false)
    private LocalDate dayStart;

    @Column(name = "day_end", nullable = false)
    private LocalDate dayEnd;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "day_return")
    private LocalDateTime dayReturn;

    @Column(name = "day_close")
    private LocalDateTime dayClose;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "borrowRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BorrowRequestItem> borrowRequestItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
