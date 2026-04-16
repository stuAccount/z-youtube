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
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("register")
    public ApiResponse<SelfProfileResponse> register(@Valid @RequestBody RegisterAccountRequest req) {
        return ApiResponse.success(accountService.register(req));
    }

    
    @GetMapping("{id}")
    public ApiResponse<SelfProfileResponse> getSelfProfile(@PathVariable Long id) {
        return ApiResponse.success(accountService.getSelfProfile(id));
    }

    @GetMapping("profile/{username}")
    public ApiResponse<PublicProfileResponse> getPublicProfile(@PathVariable String username) {
        return ApiResponse.success(accountService.getPublicProfile(username));
    }   


    @PatchMapping("{id}/profile")
    public ApiResponse<SelfProfileResponse> updateProfile(@PathVariable Long id,
                                                          @Valid @RequestBody UpdateProfileRequest req) {
        return ApiResponse.success(accountService.updateProfile(id, req));
    }

    @PatchMapping("{id}/password")
    public ApiResponse<Void> changePassword(@PathVariable Long id,
                                                           @Valid @RequestBody ChangePasswordRequest req) {
        accountService.changePassword(id, req);
        return ApiResponse.success(null);
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> withdraw(@PathVariable Long id) {
        accountService.withdraw(id);
        return ApiResponse.success(null);
    }



}