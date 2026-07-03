package com.example.myresumeapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "project_technologies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTechnology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "technology_name")
    private String technologyName;

    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private FeaturedProject project;
}