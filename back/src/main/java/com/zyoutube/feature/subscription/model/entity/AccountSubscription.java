package com.zyoutube.feature.subscription.model.entity;

import com.zyoutube.feature.account.model.entity.Account;
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
        name = "account_subscriptions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_account_subscriptions_subscriber_id_subscribed_to_id",
                        columnNames = {"subscriber_id", "subscribed_to_id"}
                )
        },
        indexes = {
                @Index(name = "idx_account_subscriptions_subscriber_id", columnList = "subscriber_id"),
                @Index(name = "idx_account_subscriptions_subscribed_to_id", columnList = "subscribed_to_id"),
                @Index(name = "idx_account_subscriptions_subscribed_to_id_created_at", columnList = "subscribed_to_id, created_at")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Access(AccessType.FIELD)
public class AccountSubscription {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscriber_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_subscriptions_subscriber"))
    private Account subscriber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscribed_to_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_subscriptions_subscribed_to"))
    private Account subscribedTo;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    public void assignSubscriber(Account subscriber) {
        this.subscriber = subscriber;
    }

    public void assignSubscribedTo(Account subscribedTo) {
        this.subscribedTo = subscribedTo;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
