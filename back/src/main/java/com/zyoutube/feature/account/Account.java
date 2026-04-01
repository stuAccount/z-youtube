package com.zyoutube.feature.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Entity
@Table(name="accounts")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter @Setter
public class Account {
    @Id @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length=50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name="password_hash", nullable = false)
    private String passwordHash;



}
