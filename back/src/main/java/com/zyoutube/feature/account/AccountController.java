package com.zyoutube.feature.account;

import com.zyoutube.common.api.ApiResponse;
import com.zyoutube.feature.account.model.dto.CreateAccountRequest;
import com.zyoutube.feature.account.model.dto.AccountResponse;
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

    @PostMapping
    public ApiResponse<AccountResponse> create(@Valid @RequestBody CreateAccountRequest req) {
        return ApiResponse.ok(accountService.create(req));
    }

    @DeleteMapping("{id}")
    public ApiResponse<AccountResponse> deleteById(@PathVariable Long id) {
        return ApiResponse.ok(accountService.deleteById(id));
    }

    @GetMapping("{id}")
    public ApiResponse<AccountResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(accountService.getById(id));
    }

    @PutMapping("{id}")
    public ApiResponse<AccountResponse> update(@PathVariable Long id,
                                  @Valid @RequestBody UpdateAccountRequest req) {
        return ApiResponse.ok(accountService.update(id, req));
    }


}