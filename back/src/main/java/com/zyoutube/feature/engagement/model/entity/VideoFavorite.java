package com.zyoutube.feature.engagement.model.entity;

import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.video.model.entity.Video;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "video_favorites",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_video_favorites_video_id_account_id", columnNames = {"video_id", "account_id"})
        },
        indexes = {
                @Index(name = "idx_video_favorites_video_id", columnList = "video_id"),
                @Index(name = "idx_video_favorites_account_id_created_at", columnList = "account_id, created_at")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Access(AccessType.FIELD)
public class VideoFavorite {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false, foreignKey = @ForeignKey(name = "fk_video_favorites_video"))
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_video_favorites_account"))
    private Account account;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    public void assignVideo(Video video) {
        this.video = video;
    }

    public void assignAccount(Account account) {
        this.account = account;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
