package com.comestics.cosmetics_store.service.impl;

import com.comestics.cosmetics_store.entity.Role;
import com.comestics.cosmetics_store.entity.User;
import com.comestics.cosmetics_store.repository.RoleRepository;
import com.comestics.cosmetics_store.repository.UserRepository;
import com.comestics.cosmetics_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public User registerCustomer(User user, String rawPassword) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(Collections.singleton(userRole));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal().equals("anonymousUser")) {
            return null;
        }
        String email = auth.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}
