package com.zyoutube.feature.auth.security;

import com.zyoutube.feature.account.model.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
public class AccountUserDetails implements UserDetails {
	private final Long accountId;
	private final String username;
	private final String passwordHash;
	private final Collection<? extends GrantedAuthority> authorities;
	private final boolean enabled;

	public static AccountUserDetails from(Account account) {
		return new AccountUserDetails(
				account.getId(),
				account.getUsername(),
				account.getPasswordHash(),
				List.of(new SimpleGrantedAuthority("ROLE_USER")),
				!account.isDeleted()
		);
	}

	@Override
	public String getPassword() {
		return this.passwordHash;
	}
}