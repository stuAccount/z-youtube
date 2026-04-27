package com.zyoutube.feature.video.model.entity;

import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.video.model.type.VideoStatus;
import com.zyoutube.feature.video.model.type.VideoVisibility;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "videos",
        indexes = {
                @Index(name = "idx_videos_author_id", columnList = "author_id"),
            @Index(name = "idx_videos_status_visibility_created_at", columnList = "status, visibility, created_at")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Access(AccessType.FIELD)
public class Video {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 5000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false, foreignKey = @ForeignKey(name = "fk_videos_author"))
    private Account author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VideoStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private VideoVisibility visibility;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void assignAuthor(Account author) {
        this.author = author;
    }

    public void changeStatus(VideoStatus status) {
        this.status = status;
    }

    public void changeVisibility(VideoVisibility visibility) {
        this.visibility = visibility;
    }

    /**
     * 发布视频
     * 
     * @throws IllegalStateException 如果视频已归档，则抛出异常
     */
    public void publish() {
        if (this.status == VideoStatus.ARCHIVED) {
            throw new IllegalStateException("Archived video can not be published");
        }
        this.status = VideoStatus.PUBLISHED;
    }

    /**
     * 将已发布的视频取消发布，状态变更为草稿
     * 
     * @throws IllegalStateException 当视频状态不是已发布时抛出异常
     */
    public void unpublish() {
        if (this.status != VideoStatus.PUBLISHED) {
            throw new IllegalStateException("Only published video can be unpublished");
        }
        this.status = VideoStatus.DRAFT;
    }

    /**
     * 将视频状态设置为已归档
     */
    public void archive() {
        this.status = VideoStatus.ARCHIVED;
    }

    public boolean isOwnedBy(Long accountId) {
        return accountId != null
                && this.author != null
                && accountId.equals(this.author.getId());
    }

    public VideoVisibility getVisibilityOrDefault() {
        return this.visibility != null ? this.visibility : VideoVisibility.PRIVATE;
    }

    /**
     * 在实体持久化到数据库之前自动执行的回调方法
     * 设置创建时间和更新时间为当前时间
     * 如果视频状态为空，则默认设置为草稿状态
     * 如果视频可见性为空，则默认设置为私有状态
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = VideoStatus.DRAFT;
        }
        if (this.visibility == null) {
            this.visibility = VideoVisibility.PRIVATE;
        }
    }

    /**
     * 在实体更新前自动设置更新时间
     * <p>
     * 使用 JPA @PreUpdate 生命周期回调，在实体数据持久化到数据库之前自动更新 updatedAt 字段
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
