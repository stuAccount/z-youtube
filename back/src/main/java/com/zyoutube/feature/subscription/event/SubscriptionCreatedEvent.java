package com.zyoutube.feature.subscription.event;

import java.time.LocalDateTime;

public record SubscriptionCreatedEvent(
        Long subscriberId,
        Long subscribedToId,
        LocalDateTime occurredAt
) {
}
