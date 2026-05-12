package com.zyoutube.feature.subscription.event;

import org.springframework.stereotype.Component;

@Component
public class NoOpSubscriptionEventPublisher implements SubscriptionEventPublisher {
    @Override
    public void publishSubscriptionCreated(SubscriptionCreatedEvent event) {
        // Reserved for async fan-out such as Redis MQ integration.
    }
}
