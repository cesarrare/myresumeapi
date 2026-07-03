package com.example.myresumeapi.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Set;
import java.util.regex.Pattern;

public final class PhotoImageValidator {

    private static final int MAX_DECODED_BYTES = 2 * 1024 * 1024;
    private static final Pattern DATA_URI_PATTERN =
            Pattern.compile("^data:(image/[a-zA-Z0-9.+-]+);base64,(.+)$", Pattern.DOTALL);

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private PhotoImageValidator() {
    }

    public record ParsedPhotoImage(String base64Data, String mimeType) {
    }

    public static ParsedPhotoImage parse(String photoImage, String photoMimeType) {
        if (photoImage == null || photoImage.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "photoImage must not be blank when provided"
            );
        }

        String trimmed = photoImage.trim();
        String mimeType = normalizeMimeType(photoMimeType);
        String base64Payload = trimmed;

        var matcher = DATA_URI_PATTERN.matcher(trimmed);
        if (matcher.matches()) {
            mimeType = normalizeMimeType(matcher.group(1));
            base64Payload = matcher.group(2).trim();
        }

        if (mimeType == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "photoMimeType is required when photoImage is provided"
            );
        }

        validateBase64Payload(base64Payload, mimeType);

        return new ParsedPhotoImage(base64Payload, mimeType);
    }

    public static String normalizeMimeType(String mimeType) {
        if (mimeType == null || mimeType.isBlank()) {
            return null;
        }

        String normalized = mimeType.trim().toLowerCase();
        if ("image/jpg".equals(normalized)) {
            return "image/jpeg";
        }

        return normalized;
    }

    private static void validateBase64Payload(String base64Payload, String mimeType) {
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported photoMimeType"
            );
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Payload);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "photoImage must be valid base64 data"
            );
        }

        if (decoded.length == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "photoImage must not be empty"
            );
        }

        if (decoded.length > MAX_DECODED_BYTES) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "photoImage exceeds maximum allowed size"
            );
        }
    }

    public static String toDataUri(String base64Data, String mimeType) {
        if (base64Data == null || base64Data.isBlank() || mimeType == null || mimeType.isBlank()) {
            return null;
        }

        return "data:" + normalizeMimeType(mimeType) + ";base64," + base64Data.trim();
    }
}
