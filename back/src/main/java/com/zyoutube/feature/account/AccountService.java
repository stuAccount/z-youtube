package com.zyoutube.feature.account;

import com.zyoutube.feature.account.model.vo.AccountResponse;
import com.zyoutube.feature.account.model.dto.RegisterAccountRequest;
import com.zyoutube.feature.account.model.dto.UpdateProfileRequest;
import com.zyoutube.feature.account.model.entity.Account;
import jakarta.persistence.EntityManager;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository,
                          EntityManager entityManager,
                          PasswordEncoder passwordEncoder){
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AccountResponse register(RegisterAccountRequest req) {
        if(accountRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        Account account = new Account();
        account.renameUsername(req.getUsername());
        account.changeEmail(req.getEmail());
        account.updatePassword(passwordEncoder.encode(req.getPassword()));
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
    public AccountResponse updateProfile(Long id, @Valid UpdateProfileRequest req) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

        if(accountRepository.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new IllegalArgumentException("email already been used");
        }

        account.renameUsername(req.getUsername());
        account.changeEmail(req.getEmail());
        return new AccountResponse(account.getId(), account.getUsername(), account.getEmail());
    }
}
