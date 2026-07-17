package com.example.myresumeapi.service;

import com.example.myresumeapi.dto.auth.GoogleAuthRequest;
import com.example.myresumeapi.dto.auth.LoginRequest;
import com.example.myresumeapi.dto.auth.LoginResponse;
import com.example.myresumeapi.dto.auth.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);

    LoginResponse googleAuth(GoogleAuthRequest request);
}
