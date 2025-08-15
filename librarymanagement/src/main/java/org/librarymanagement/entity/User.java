package org.librarymanagement.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer status;

    @Column(name = "activated_status", nullable = false)
    private boolean activatedStatus = false;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phone;

    @Column(nullable = false)
    private Integer role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private String avatar;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews;

    @Column(name = "reset_token")
    private String resetToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BorrowRequest> borrowRequests;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserInteraction> userInteractions;

    private String verificationToken;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        this.status = 1;
    }
}
