package com.zyoutube.feature.account;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.account.model.dto.ChangePasswordRequest;
import com.zyoutube.feature.account.model.dto.RegisterAccountRequest;
import com.zyoutube.feature.account.model.vo.SelfProfileResponse;
import com.zyoutube.feature.account.model.vo.PublicProfileResponse;
import com.zyoutube.feature.account.model.dto.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ApiResponse<SelfProfileResponse> register(@Valid @RequestBody RegisterAccountRequest req) {
        return ApiResponse.success(accountService.register(req));
    }


    @GetMapping("/profile")
    public ApiResponse<SelfProfileResponse> getSelfProfile() {
        return ApiResponse.success(accountService.getSelfProfile());
    }

    @GetMapping("/profile/{username}")
    public ApiResponse<PublicProfileResponse> getPublicProfile(@PathVariable String username) {
        return ApiResponse.success(accountService.getPublicProfile(username));
    }


    @PatchMapping("/profile")
    public ApiResponse<SelfProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest req) {
        return ApiResponse.success(accountService.updateProfile(req));
    }

    @PatchMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        accountService.changePassword(req);
        return ApiResponse.success(null);
    }

    @DeleteMapping
    public ApiResponse<Void> withdraw() {
        accountService.withdraw();
        return ApiResponse.success(null);
    }



}