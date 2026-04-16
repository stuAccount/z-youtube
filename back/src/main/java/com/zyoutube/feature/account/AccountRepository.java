package com.zyoutube.feature.account;

import com.zyoutube.feature.account.model.entity.Account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    
    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Account> findByUsername(String username);
}
