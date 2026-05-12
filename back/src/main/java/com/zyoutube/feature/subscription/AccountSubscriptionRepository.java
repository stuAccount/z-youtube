package com.zyoutube.feature.subscription;

import com.zyoutube.feature.subscription.model.entity.AccountSubscription;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSubscriptionRepository extends JpaRepository<AccountSubscription, Long> {
    boolean existsBySubscriber_IdAndSubscribedTo_Id(Long subscriberId, Long subscribedToId);

    Optional<AccountSubscription> findBySubscriber_IdAndSubscribedTo_Id(Long subscriberId, Long subscribedToId);

    long countBySubscriber_Id(Long subscriberId);

    long countBySubscribedTo_Id(Long subscribedToId);
}
