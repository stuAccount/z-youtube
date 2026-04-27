package com.zyoutube.feature.auth.context;

import com.zyoutube.common.exception.UnauthorizedException;
import com.zyoutube.feature.auth.security.AccountUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    public Long getCurrentAccountId() {
        Long accountId = getCurrentAccountIdOrNull();
        if (accountId == null) {
            throw new UnauthorizedException("No authenticated user");
        }
        return accountId;
    }

    public Long getCurrentAccountIdOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        if (!(authentication.getPrincipal() instanceof AccountUserDetails principal)) {
            throw new UnauthorizedException("Invalid authenticated principal");
        }

        return principal.getAccountId();
    }
}
