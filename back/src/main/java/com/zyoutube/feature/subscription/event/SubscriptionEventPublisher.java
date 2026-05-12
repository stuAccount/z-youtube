package com.zyoutube.feature.subscription.event;

public interface SubscriptionEventPublisher {
    void publishSubscriptionCreated(SubscriptionCreatedEvent event);
}
