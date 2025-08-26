package org.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"user", "borrowRequestItems"})
@Table(name = "borrow_requests")
@Data
public class BorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "day_confirmed")
    private LocalDateTime dayConfirmed;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "lastReminderSent_at")
    private LocalDateTime lastReminderSentAt;

    @OneToMany(mappedBy = "borrowRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BorrowRequestItem> borrowRequestItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
