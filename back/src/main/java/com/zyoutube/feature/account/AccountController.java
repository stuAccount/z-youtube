package com.zyoutube.feature.account;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.account.model.dto.ChangePasswordRequest;
import com.zyoutube.feature.account.model.dto.RegisterAccountRequest;
import com.zyoutube.feature.account.model.vo.AccountResponse;
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
    public ApiResponse<AccountResponse> register(@Valid @RequestBody RegisterAccountRequest req) {
        return ApiResponse.ok(accountService.register(req));
    }

    @GetMapping("{id}")
    public ApiResponse<AccountResponse> getProfile(@PathVariable Long id) {
        return ApiResponse.ok(accountService.getById(id));
    }

    @PatchMapping("{id}/profile")
    public ApiResponse<AccountResponse> updateProfile(@PathVariable Long id,
                                               @Valid @RequestBody UpdateProfileRequest req) {
        return ApiResponse.ok(accountService.updateProfile(id, req));
    }

    @PatchMapping("{id}/password")
    public ApiResponse<AccountResponse> changePassword(@PathVariable Long id,
                                                       @Valid @RequestBody ChangePasswordRequest req) {
        return ApiResponse.ok(accountService.changePassword(id, req));
    }

    @DeleteMapping("{id}")
    public ApiResponse<AccountResponse> withdraw(@PathVariable Long id) {
        return ApiResponse.ok(accountService.deleteById(id));
    }



}