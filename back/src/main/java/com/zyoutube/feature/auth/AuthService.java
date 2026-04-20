package com.zyoutube.feature.auth;

import com.zyoutube.feature.account.AccountRepository;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.auth.model.dto.LoginRequest;
import com.zyoutube.feature.auth.model.vo.LoginResponse;
import com.zyoutube.feature.auth.security.AccountUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * TODO Sprint 7: authentication application service.
 */
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final AccountRepository accountRepository;

    public AuthService(AuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository,
            AccountRepository accountRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.accountRepository = accountRepository;
    }

    public LoginResponse login(LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                    req.getLoginId(), 
                    req.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

        Account account = accountRepository.findById(principal.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated account not found"));

        return new LoginResponse(
            account.getId(),
            account.getUsername(),
            account.getEmail(),
            account.getNickname(),
            account.getBio(),
            account.getAvatarUrl()
        );
    }

    public LoginResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        AccountUserDetails principal = (AccountUserDetails) authentication.getPrincipal();

        Account account = accountRepository.findById(principal.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated account not found"));

        return new LoginResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getNickname(),
                account.getBio(),
                account.getAvatarUrl()
        );

    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        SecurityContextHolder.clearContext();
    }
}
