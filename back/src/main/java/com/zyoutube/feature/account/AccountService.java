package com.zyoutube.feature.account;

import com.zyoutube.feature.account.model.dto.ChangePasswordRequest;
import com.zyoutube.feature.account.model.vo.PublicProfileResponse;
import com.zyoutube.feature.account.model.vo.SelfProfileResponse;
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
    public SelfProfileResponse register(RegisterAccountRequest req) {
        if(accountRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (accountRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        Account account = new Account();
        account.renameUsername(req.getUsername());
        account.changeEmail(req.getEmail());
        account.updatePassword(passwordEncoder.encode(req.getPassword()));
        entityManager.persist(account);
        return new SelfProfileResponse(account.getId(), account.getUsername(), account.getEmail(),
                account.getNickname(), account.getBio(), account.getAvatarUrl());
    }

    public void withdraw(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

        if (account.isDeleted()) {
            throw new IllegalStateException("Account already withdrawn");
        }
        account.softDelete();
    }

    public SelfProfileResponse getSelfProfile(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));
        return new SelfProfileResponse(account.getId(), account.getUsername(), account.getEmail(),
                account.getNickname(), account.getBio(), account.getAvatarUrl());
    }

    public PublicProfileResponse getPublicProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));
        return new PublicProfileResponse(account.getId(), account.getUsername(),
                account.getNickname(), account.getBio(), account.getAvatarUrl());   
    }

    @Transactional
    public SelfProfileResponse updateProfile(Long id, @Valid UpdateProfileRequest req) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

        if(accountRepository.existsByEmailAndIdNot(req.getEmail(), id)) {
            throw new IllegalArgumentException("Email already been used");
        }

        if (req.getEmail() != null) {
            account.changeEmail(req.getEmail());
        }
        if (req.getNickname() != null) {
            account.setNickname(req.getNickname());
        }
        if (req.getAvatarUrl() != null) {
            account.setAvatarUrl(req.getAvatarUrl());
        }
        if (req.getBio() != null) {
            account.setBio(req.getBio());
        }
        return new SelfProfileResponse(account.getId(), account.getUsername(), account.getEmail(),
                account.getNickname(), account.getBio(), account.getAvatarUrl());
    }

    @Transactional
    public void changePassword(Long id, @Valid ChangePasswordRequest req) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

        if (!passwordEncoder.matches(req.getOldPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Old Password is incorrect");
        }

        if (!passwordEncoder.matches(req.getNewPassword(), req.getConfirmPassword())) {
            throw new IllegalArgumentException("The new passwords do not match");
        }

        account.updatePassword(passwordEncoder.encode(req.getNewPassword()));

    }
}
