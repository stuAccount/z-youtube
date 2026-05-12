package com.zyoutube.feature.subscription.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountSubscriptionResponse {
    private Long targetAccountId;
    private String targetUsername;
    private boolean subscribed;
    private long subscriberCount;
}
