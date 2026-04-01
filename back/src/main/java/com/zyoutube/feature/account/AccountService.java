package com.zyoutube.feature.account;

import com.zyoutube.feature.account.dto.AccountResponse;
import com.zyoutube.feature.account.dto.CreateAccountRequest;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }
    public AccountResponse create(CreateAccountRequest req) {
        if(accountRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        Account account = new Account();
        account.setUsername(req.getUsername());
        account.setEmail(req.getEmail());
        account.setPasswordHash("hashed_" + req.getPassword());
        Account saved = accountRepository.save(account);
        return new AccountResponse(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    public AccountResponse getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));
        return new AccountResponse(account.getId(), account.getUsername(), account.getEmail());
    }
}
