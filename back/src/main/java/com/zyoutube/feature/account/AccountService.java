package com.zyoutube.feature.account;

import com.zyoutube.feature.account.model.dto.ChangePasswordRequest;
import com.zyoutube.feature.account.model.vo.PublicProfileResponse;
import com.zyoutube.feature.account.model.vo.SelfProfileResponse;
import com.zyoutube.feature.account.model.dto.RegisterAccountRequest;
import com.zyoutube.feature.account.model.dto.UpdateProfileRequest;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.auth.context.CurrentUserProvider;
import com.zyoutube.feature.subscription.service.AccountSubscriptionService;
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
    private final AccountFinder accountFinder;
    private final CurrentUserProvider currentUserProvider;
    private final AccountSubscriptionService accountSubscriptionService;

    public AccountService(AccountRepository accountRepository,
                          EntityManager entityManager,
                          PasswordEncoder passwordEncoder,
                          AccountFinder accountFinder,
                          CurrentUserProvider currentUserProvider,
                          AccountSubscriptionService accountSubscriptionService) {
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
        this.accountFinder = accountFinder;
        this.currentUserProvider = currentUserProvider;
        this.accountSubscriptionService = accountSubscriptionService;
    }

    private SelfProfileResponse toSelfProfileResponse(Account account) {
        return new SelfProfileResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getNickname(),
                account.getAvatarUrl(),
                account.getBio(),
                accountSubscriptionService.countSubscribers(account.getId()),
                accountSubscriptionService.countSubscriptions(account.getId())
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
        return toSelfProfileResponse(account);
    }

    
    @Transactional
    public void withdraw() {
        Account account = accountFinder.getCurrentAccount();

        if (account.isDeleted()) {
            throw new IllegalStateException("Account already withdrawn");
        }
        account.softDelete();
    }

    public SelfProfileResponse getSelfProfile() {
        return toSelfProfileResponse(accountFinder.getCurrentAccount());
    }

    public PublicProfileResponse getPublicProfile(String username) {
        Account account = accountFinder.findActiveAccountByUsername(username);
        Long currentAccountId = currentUserProvider.getCurrentAccountIdOrNull();
        boolean subscribedByCurrentUser = currentAccountId != null
                && !currentAccountId.equals(account.getId())
                && accountSubscriptionService.isSubscribed(currentAccountId, account.getId());

        return new PublicProfileResponse(
                account.getId(),
                account.getUsername(),
                account.getNickname(),
                account.getAvatarUrl(),
                account.getBio(),
                accountSubscriptionService.countSubscribers(account.getId()),
                accountSubscriptionService.countSubscriptions(account.getId()),
                subscribedByCurrentUser
        );
    }

    @Transactional
    public SelfProfileResponse updateProfile(@Valid UpdateProfileRequest req) {
        Account account = accountFinder.getCurrentAccount();
        Long id = account.getId();

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
        Account account = accountFinder.getCurrentAccount();

        if (!passwordEncoder.matches(req.getOldPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Old Password is incorrect");
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new IllegalArgumentException("The new passwords do not match");
        }

        account.updatePassword(passwordEncoder.encode(req.getNewPassword()));

    }
}
