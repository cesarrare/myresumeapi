package com.example.myresumeapi.dto;

import lombok.Data;

@Data
public class PersonalInfoResponse {

    private String name;
    private String title;
    private String email;
    private String phone;
    private String address;
    private String linkedin;
    private String github;
    /** External photo URL or data URI returned by the API when an image is stored. */
    private String photo;
    /** Optional base64 image payload (no data URI prefix required). */
    private String photoImage;
    /** Optional MIME type for photoImage, e.g. image/png. */
    private String photoMimeType;
    private String summary;
}