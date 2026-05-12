package com.zyoutube.feature.subscription.service.impl;

import com.zyoutube.feature.account.AccountFinder;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.subscription.AccountSubscriptionRepository;
import com.zyoutube.feature.subscription.event.SubscriptionCreatedEvent;
import com.zyoutube.feature.subscription.event.SubscriptionEventPublisher;
import com.zyoutube.feature.subscription.model.entity.AccountSubscription;
import com.zyoutube.feature.subscription.model.vo.AccountSubscriptionResponse;
import java.time.LocalDateTime;

import com.zyoutube.feature.subscription.service.AccountSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountSubscriptionServiceImpl implements AccountSubscriptionService {
    private final AccountSubscriptionRepository accountSubscriptionRepository;
    private final AccountFinder accountFinder;
    private final SubscriptionEventPublisher subscriptionEventPublisher;

    @Override
    @Transactional
    public AccountSubscriptionResponse subscribe(String targetUsername) {
        Account subscriber = accountFinder.getCurrentAccount();
        Account target = accountFinder.findActiveAccountByUsername(targetUsername);
        validateNotSelfSubscription(subscriber, target);

        boolean alreadySubscribed = accountSubscriptionRepository.existsBySubscriber_IdAndSubscribedTo_Id(
                subscriber.getId(),
                target.getId()
        );
        if (!alreadySubscribed) {
            AccountSubscription subscription = new AccountSubscription();
            subscription.assignSubscriber(subscriber);
            subscription.assignSubscribedTo(target);
            accountSubscriptionRepository.save(subscription);
            subscriptionEventPublisher.publishSubscriptionCreated(
                    new SubscriptionCreatedEvent(subscriber.getId(), target.getId(), LocalDateTime.now())
            );
        }

        return buildResponse(target);
    }

    @Override
    @Transactional
    public AccountSubscriptionResponse unsubscribe(String targetUsername) {
        Account subscriber = accountFinder.getCurrentAccount();
        Account target = accountFinder.findActiveAccountByUsername(targetUsername);
        validateNotSelfSubscription(subscriber, target);

        accountSubscriptionRepository.findBySubscriber_IdAndSubscribedTo_Id(subscriber.getId(), target.getId())
                .ifPresent(accountSubscriptionRepository::delete);

        return buildResponse(target, false);
    }

    @Override
    @Transactional(readOnly = true)
    public long countSubscribers(Long accountId) {
        return accountSubscriptionRepository.countBySubscribedTo_Id(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countSubscriptions(Long accountId) {
        return accountSubscriptionRepository.countBySubscriber_Id(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSubscribed(Long subscriberId, Long targetAccountId) {
        return accountSubscriptionRepository.existsBySubscriber_IdAndSubscribedTo_Id(subscriberId, targetAccountId);
    }

    private void validateNotSelfSubscription(Account subscriber, Account target) {
        if (subscriber.getId().equals(target.getId())) {
            throw new IllegalArgumentException("You cannot subscribe to yourself");
        }
    }

    private AccountSubscriptionResponse buildResponse(Account target) {
        return buildResponse(target, true);
    }

    private AccountSubscriptionResponse buildResponse(Account target, boolean subscribed) {
        return new AccountSubscriptionResponse(
                target.getId(),
                target.getUsername(),
                subscribed,
                countSubscribers(target.getId())
        );
    }
}
