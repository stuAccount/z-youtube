package com.zyoutube.feature.account;

import com.zyoutube.feature.account.model.vo.AccountResponse;
import com.zyoutube.feature.account.model.dto.RegisterAccountRequest;
import com.zyoutube.feature.account.model.dto.UpdateAccountRequest;
import com.zyoutube.feature.account.model.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    public AccountService(AccountRepository accountRepository, EntityManager entityManager){
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public AccountResponse create(RegisterAccountRequest req) {
        if(accountRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        Account account = new Account();
        account.setUsername(req.getUsername());
        account.setEmail(req.getEmail());
        account.setPasswordHash("hashed_" + req.getPassword());
        entityManager.persist(account);
        return new AccountResponse(account.getId(), account.getUsername(), account.getEmail());
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

    @Transactional
    public AccountResponse update(Long id, @Valid UpdateAccountRequest req) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

        if(accountRepository.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new IllegalArgumentException("email already been used");
        }

        account.setUsername(req.getUsername());
        account.setEmail(req.getEmail());
        return new AccountResponse(account.getId(), account.getUsername(), account.getEmail());
    }
}
