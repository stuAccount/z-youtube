package com.zyoutube.feature.subscription.service;

import com.zyoutube.feature.subscription.model.vo.AccountSubscriptionResponse;

public interface AccountSubscriptionService {
    AccountSubscriptionResponse subscribe(String targetUsername);

    AccountSubscriptionResponse unsubscribe(String targetUsername);

    long countSubscribers(Long accountId);

    long countSubscriptions(Long accountId);

    boolean isSubscribed(Long subscriberId, Long targetAccountId);
}
