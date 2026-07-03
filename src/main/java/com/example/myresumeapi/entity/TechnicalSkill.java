package com.example.myresumeapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "technical_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicalSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
}