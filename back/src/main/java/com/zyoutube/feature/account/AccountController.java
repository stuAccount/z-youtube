package com.zyoutube.feature.account;

import com.zyoutube.feature.account.dto.CreateAccountRequest;
import com.zyoutube.feature.account.dto.AccountResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        return accountService.create(req);
    }

    @GetMapping("{id}")
    public AccountResponse getById(@PathVariable Long id) {
        return accountService.getById(id);
    }

}