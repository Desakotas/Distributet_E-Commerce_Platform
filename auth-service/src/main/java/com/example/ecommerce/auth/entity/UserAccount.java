package com.example.ecommerce.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false, length = 120)
    private String passwordHash;

    @Column(nullable = false, length = 200)
    private String roles;
    private Instant createdAt;

    protected UserAccount() {
    }

    public UserAccount(String username, String passwordHash, List<String> roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = String.join(",", roles);
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<String> getRoles() {
        return Arrays.stream(roles.split(","))
                .filter(role -> !role.isBlank())
                .toList();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
