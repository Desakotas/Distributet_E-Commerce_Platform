package com.example.ecommerce.auth.config;

import com.example.ecommerce.auth.entity.UserAccount;
import com.example.ecommerce.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seed("alice", "password123", List.of("CUSTOMER"));
        seed("admin", "admin123", List.of("ADMIN", "CUSTOMER"));
    }

    private void seed(String username, String rawPassword, List<String> roles) {
        if (!userRepository.existsByUsername(username)) {
            userRepository.save(new UserAccount(username, passwordEncoder.encode(rawPassword), roles));
        }
    }
}
