package com.example.myresumeapi.service.impl;

import com.example.myresumeapi.dto.auth.LoginRequest;
import com.example.myresumeapi.dto.auth.LoginResponse;
import com.example.myresumeapi.dto.auth.RegisterRequest;
import com.example.myresumeapi.entity.User;
import com.example.myresumeapi.repository.UserRepository;
import com.example.myresumeapi.security.JwtService;
import com.example.myresumeapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        validateCredentials(request.getEmail(), request.getPassword());

        User user = userRepository.findByEmail(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> unauthorized());

        if (!isPasswordValid(user, request.getPassword())) {
            throw unauthorized();
        }

        return buildLoginResponse(user);
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        validateCredentials(request.getEmail(), request.getPassword());

        String email = normalizeEmail(request.getEmail());

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .createdAt(now)
                .updatedAt(now)
                .build();

        user = userRepository.save(user);

        return buildLoginResponse(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        String token = jwtService.generateToken(user.getEmail());

        return LoginResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .build();
    }

    private void validateCredentials(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email is required"
            );
        }

        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password is required"
            );
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private boolean isPasswordValid(User user, String rawPassword) {
        String storedHash = user.getPasswordHash();

        if (passwordEncoder.matches(rawPassword, storedHash)) {
            return true;
        }

        if (!looksLikeBcryptHash(storedHash) && storedHash.equals(rawPassword)) {
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }

        return false;
    }

    private boolean looksLikeBcryptHash(String value) {
        return value != null
                && (value.startsWith("$2a$")
                || value.startsWith("$2b$")
                || value.startsWith("$2y$"));
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }
}
