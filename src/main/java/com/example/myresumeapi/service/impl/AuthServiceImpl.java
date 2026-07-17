package com.example.myresumeapi.service.impl;

import com.example.myresumeapi.dto.auth.GoogleAuthRequest;
import com.example.myresumeapi.dto.auth.LoginRequest;
import com.example.myresumeapi.dto.auth.LoginResponse;
import com.example.myresumeapi.dto.auth.RegisterRequest;
import com.example.myresumeapi.entity.User;
import com.example.myresumeapi.repository.UserRepository;
import com.example.myresumeapi.security.GoogleTokenService;
import com.example.myresumeapi.security.JwtService;
import com.example.myresumeapi.service.AuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    public static final String AUTH_PROVIDER_LOCAL = "LOCAL";
    public static final String AUTH_PROVIDER_GOOGLE = "GOOGLE";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GoogleTokenService googleTokenService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        validateCredentials(request.getEmail(), request.getPassword());

        User user = userRepository.findByEmail(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> unauthorized());

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "This account uses Google sign-in"
            );
        }

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
                .authProvider(AUTH_PROVIDER_LOCAL)
                .createdAt(now)
                .updatedAt(now)
                .build();

        user = userRepository.save(user);

        return buildLoginResponse(user);
    }

    @Override
    @Transactional
    public LoginResponse googleAuth(GoogleAuthRequest request) {
        GoogleIdToken.Payload payload = googleTokenService.verify(request.getIdToken());

        String googleId = payload.getSubject();
        String email = normalizeEmail(payload.getEmail());
        String firstName = stringClaim(payload, "given_name");
        String lastName = stringClaim(payload, "family_name");
        String pictureUrl = stringClaim(payload, "picture");

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Google account email is required"
            );
        }

        Optional<User> byGoogleId = userRepository.findByGoogleId(googleId);
        if (byGoogleId.isPresent()) {
            User user = byGoogleId.get();
            syncGoogleProfile(user, firstName, lastName, pictureUrl);
            return buildLoginResponse(userRepository.save(user));
        }

        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            user.setGoogleId(googleId);
            if (user.getAuthProvider() == null || user.getAuthProvider().isBlank()) {
                user.setAuthProvider(AUTH_PROVIDER_GOOGLE);
            } else if (AUTH_PROVIDER_LOCAL.equalsIgnoreCase(user.getAuthProvider())) {
                // Keep LOCAL password login; just link Google identity.
                user.setAuthProvider(AUTH_PROVIDER_LOCAL);
            } else {
                user.setAuthProvider(AUTH_PROVIDER_GOOGLE);
            }
            syncGoogleProfile(user, firstName, lastName, pictureUrl);
            return buildLoginResponse(userRepository.save(user));
        }

        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .email(email)
                .passwordHash(null)
                .firstName(firstName)
                .lastName(lastName)
                .authProvider(AUTH_PROVIDER_GOOGLE)
                .googleId(googleId)
                .pictureUrl(pictureUrl)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return buildLoginResponse(userRepository.save(user));
    }

    private void syncGoogleProfile(
            User user,
            String firstName,
            String lastName,
            String pictureUrl
    ) {
        if (isBlank(user.getFirstName()) && !isBlank(firstName)) {
            user.setFirstName(firstName);
        }
        if (isBlank(user.getLastName()) && !isBlank(lastName)) {
            user.setLastName(lastName);
        }
        if (!isBlank(pictureUrl)) {
            user.setPictureUrl(pictureUrl);
        }
        user.setUpdatedAt(LocalDateTime.now());
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

        if (storedHash == null || storedHash.isBlank()) {
            return false;
        }

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

    private String stringClaim(GoogleIdToken.Payload payload, String key) {
        Object value = payload.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
        );
    }
}
