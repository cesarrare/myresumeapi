package com.example.myresumeapi.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
public class GoogleTokenService {

    private final String clientId;
    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenService(@Value("${app.google.client-id:}") String clientId) {
        this.clientId = clientId == null ? "" : clientId.trim();
        this.verifier = this.clientId.isEmpty()
                ? null
                : new GoogleIdTokenVerifier.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance()
                )
                        .setAudience(Collections.singletonList(this.clientId))
                        .build();
    }

    public GoogleIdToken.Payload verify(String idToken) {
        if (clientId.isEmpty() || verifier == null) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Google auth is not configured (set GOOGLE_CLIENT_ID)"
            );
        }

        if (idToken == null || idToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Google idToken is required"
            );
        }

        try {
            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw unauthorized();
            }

            GoogleIdToken.Payload payload = token.getPayload();
            if (Boolean.FALSE.equals(payload.getEmailVerified())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Google email is not verified"
                );
            }

            return payload;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            // Audience mismatch / expired token / bad signature usually land here.
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid Google idToken: " + ex.getMessage(),
                    ex
            );
        }
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid Google idToken"
        );
    }
}
