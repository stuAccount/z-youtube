package com.zyoutube.feature.account;

import com.zyoutube.feature.account.model.dto.AccountResponse;
import com.zyoutube.feature.account.model.dto.CreateAccountRequest;
import com.zyoutube.feature.account.model.dto.UpdateAccountRequest;
import com.zyoutube.feature.account.model.entity.Account;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
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

    public AccountResponse deleteById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));
        accountRepository.delete(account);
        return new AccountResponse(account.getId(), account.getUsername(), account.getEmail());
    }

    public AccountResponse getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));
        return new AccountResponse(account.getId(), account.getUsername(), account.getEmail());
    }

    public AccountResponse update(Long id, @Valid UpdateAccountRequest req) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));
        account.setUsername(req.getUsername());
        account.setEmail(req.getEmail());
        return new AccountResponse(account.getId(), account.getUsername(), account.getEmail());
    }
}
