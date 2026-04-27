package com.zyoutube.feature.account;

import com.zyoutube.feature.account.model.dto.ChangePasswordRequest;
import com.zyoutube.feature.account.model.vo.PublicProfileResponse;
import com.zyoutube.feature.account.model.vo.SelfProfileResponse;
import com.zyoutube.feature.account.model.dto.RegisterAccountRequest;
import com.zyoutube.feature.account.model.dto.UpdateProfileRequest;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.auth.context.CurrentUserProvider;
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
    private final CurrentUserProvider currentUserProvider;

    public AccountService(AccountRepository accountRepository, EntityManager entityManager, PasswordEncoder passwordEncoder,
            CurrentUserProvider currentUserProvider) {
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
        this.currentUserProvider = currentUserProvider;
    }

    private Account getCurrentAccount() {
        return accountRepository.findById(currentUserProvider.getCurrentAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));
    }

    private SelfProfileResponse toSelfProfileResponse(Account account) {
        return new SelfProfileResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getNickname(),
                account.getBio(),
                account.getAvatarUrl()
        );
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

    
    @Transactional
    public void withdraw() {
        Account account = getCurrentAccount();

        if (account.isDeleted()) {
            throw new IllegalStateException("Account already withdrawn");
        }
        account.softDelete();
    }

    public SelfProfileResponse getSelfProfile() {
        return toSelfProfileResponse(getCurrentAccount());
    }

    public PublicProfileResponse getPublicProfile(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account not found!"));

        if (account.isDeleted()) {
            throw new IllegalArgumentException("Account not found!");
        }

        return new PublicProfileResponse(account.getId(), account.getUsername(),
                account.getNickname(), account.getBio(), account.getAvatarUrl());   
    }

    @Transactional
    public SelfProfileResponse updateProfile(@Valid UpdateProfileRequest req) {
        Long id = currentUserProvider.getCurrentAccountId();
        Account account = getCurrentAccount();

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
        return toSelfProfileResponse(account);
    }

    @Transactional
    public void changePassword(@Valid ChangePasswordRequest req) {
        Account account = getCurrentAccount();

        if (!passwordEncoder.matches(req.getOldPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Old Password is incorrect");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("The new passwords do not match");
        }

        account.updatePassword(passwordEncoder.encode(req.getNewPassword()));

    }
}
