package com.zyoutube.feature.account;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.account.model.dto.RegisterAccountRequest;
import com.zyoutube.feature.account.model.vo.AccountResponse;
import com.zyoutube.feature.account.model.dto.UpdateAccountRequest;
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
    public ApiResponse<AccountResponse> create(@Valid @RequestBody RegisterAccountRequest req) {
        return ApiResponse.ok(accountService.create(req));
    }

    @GetMapping("{id}")
    public ApiResponse<AccountResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(accountService.getById(id));
    }

    @PatchMapping("{id}/profile")
    public


    @DeleteMapping("{id}")
    public ApiResponse<AccountResponse> deleteById(@PathVariable Long id) {
        return ApiResponse.ok(accountService.deleteById(id));
    }

    @PutMapping("{id}")
    public ApiResponse<AccountResponse> update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateAccountRequest req) {
        return ApiResponse.ok(accountService.update(id, req));
    }


}