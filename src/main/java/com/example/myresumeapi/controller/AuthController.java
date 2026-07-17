package com.example.myresumeapi.controller;

import com.example.myresumeapi.dto.auth.GoogleAuthRequest;
import com.example.myresumeapi.dto.auth.LoginRequest;
import com.example.myresumeapi.dto.auth.LoginResponse;
import com.example.myresumeapi.dto.auth.RegisterRequest;
import com.example.myresumeapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/google")
    public LoginResponse googleAuth(@RequestBody GoogleAuthRequest request) {
        return authService.googleAuth(request);
    }
}
