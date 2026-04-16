package com.zyoutube.feature.account.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_accounts_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_accounts_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_accounts_deleted_at", columnList = "deleted_at")
        }
)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Setter
public class Account {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false, length = 50)
    private String username;

    @Setter(AccessLevel.NONE)
    @Column(nullable = false, length = 100)
    private String email;

    @Setter(AccessLevel.NONE)
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 50)
    private String nickname;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Column(name = "deleted_at")
    @Setter(AccessLevel.NONE)
    private LocalDateTime deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    public void changeEmail(String newEmail)  {
        this.email = newEmail;
    }

    public void updatePassword(String hashedPassword)  {
        this.passwordHash = hashedPassword;
    }

    public void renameUsername(String newUsername) {
        this.username = newUsername;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.nickname == null || this.nickname.isBlank()) {
            setNickname(this.username);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
