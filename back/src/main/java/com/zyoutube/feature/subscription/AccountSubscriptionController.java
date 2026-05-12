package com.zyoutube.feature.subscription;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.subscription.model.vo.AccountSubscriptionResponse;
import com.zyoutube.feature.subscription.service.AccountSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountSubscriptionController {
    private final AccountSubscriptionService accountSubscriptionService;

    @PutMapping("/{username}/subscription")
    public ApiResponse<AccountSubscriptionResponse> subscribe(@PathVariable String username) {
        return ApiResponse.success(accountSubscriptionService.subscribe(username));
    }

    @DeleteMapping("/{username}/subscription")
    public ApiResponse<AccountSubscriptionResponse> unsubscribe(@PathVariable String username) {
        return ApiResponse.success(accountSubscriptionService.unsubscribe(username));
    }
}
