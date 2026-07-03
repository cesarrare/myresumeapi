package com.example.myresumeapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "personal_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    private String title;
    private String email;
    private String phone;
    private String address;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "photo_data", columnDefinition = "TEXT")
    private String photoData;

    @Column(name = "photo_mime_type")
    private String photoMimeType;

    private String summary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;
}