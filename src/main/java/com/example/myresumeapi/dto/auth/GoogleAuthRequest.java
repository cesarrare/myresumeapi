package com.example.myresumeapi.dto.auth;

import lombok.Data;

@Data
public class GoogleAuthRequest {

    /** Google ID token from the client (GIS / Sign in with Google). */
    private String idToken;
}
