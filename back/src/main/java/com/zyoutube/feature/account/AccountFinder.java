package com.zyoutube.feature.account;

import com.zyoutube.common.exception.NotFoundException;
import com.zyoutube.feature.account.model.entity.Account;
import com.zyoutube.feature.auth.context.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountFinder {
    private final AccountRepository accountRepository;
    private final CurrentUserProvider currentUserProvider;

    /**
     * 根据账户ID查找账户信息
     * 
     * @param accountId 账户ID
     * @return 找到的账户信息
     * @throws NotFoundException 当账户不存在时抛出
     */
    public Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
    }

    public Account findActiveAccount(Long accountId) {
        Account account = findAccount(accountId);
        if (account.isDeleted()) {
            throw new NotFoundException("Account not found");
        }
        return account;
    }

    public Account findActiveAccountByUsername(String username) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (account.isDeleted()) {
            throw new NotFoundException("Account not found");
        }
        return account;
    }

    /**
     * 获取当前登录用户的账户信息
     * 
     * @return 当前用户的账户对象
     * @throws NotFoundException 如果当前用户ID对应的账户不存在
     */
    public Account getCurrentAccount() {
        return findActiveAccount(currentUserProvider.getCurrentAccountId());
    }
}
