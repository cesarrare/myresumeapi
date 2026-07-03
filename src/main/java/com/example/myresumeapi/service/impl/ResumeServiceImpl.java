package com.example.myresumeapi.service.impl;

import com.example.myresumeapi.dto.FeaturedProjectResponse;
import com.example.myresumeapi.dto.PersonalInfoResponse;
import com.example.myresumeapi.dto.ProfessionalHistoryResponse;
import com.example.myresumeapi.dto.ResumeResponse;
import com.example.myresumeapi.dto.ResumeSaveRequest;
import com.example.myresumeapi.dto.ResumeSummaryResponse;
import com.example.myresumeapi.dto.ResumeUpdateRequest;
import com.example.myresumeapi.dto.TechnicalSkillResponse;
import com.example.myresumeapi.entity.*;
import com.example.myresumeapi.repository.*;
import com.example.myresumeapi.service.ResumeService;
import com.example.myresumeapi.util.PhotoImageValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl
        implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final CoreCompetencyRepository coreCompetencyRepository;
    private final TechnicalSkillRepository technicalSkillRepository;
    private final ProfessionalHistoryRepository professionalHistoryRepository;
    private final AchievementRepository achievementRepository;
    private final FeaturedProjectRepository featuredProjectRepository;
    private final ProjectTechnologyRepository projectTechnologyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ResumeResponse getResumeById(Long resumeId) {

        Resume resume = resumeRepository
                .findById(resumeId)
                .orElseThrow();

        ResumeResponse response = new ResumeResponse();
        response.setResumeId(resume.getId());
        response.setUserId(resume.getUser().getId());
        response.setResumeName(resume.getResumeName());
        response.setTemplateName(resume.getTemplateName());

        response.setPersonalInfo(
                mapPersonalInfo(resumeId)
        );

        response.setCoreCompetencies(
                mapCoreCompetencies(resumeId)
        );

        response.setTechnicalSkills(
                mapTechnicalSkills(resumeId)
        );

        response.setProfessionalHistory(
                mapProfessionalHistory(resumeId)
        );

        response.setFeaturedProjects(
                mapFeaturedProjects(resumeId)
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumeSummaryResponse> getResumesByUserId(Long userId) {
        return resumeRepository.findByUser_IdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::mapResumeSummary)
                .toList();
    }

    @Override
    @Transactional
    public ResumeResponse saveResume(ResumeSaveRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        LocalDateTime now = LocalDateTime.now();

        Resume resume = resumeRepository.save(
                Resume.builder()
                        .user(user)
                        .resumeName(request.getResumeName())
                        .templateName(request.getTemplateName())
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );

        savePersonalInfo(resume, request.getPersonalInfo());
        saveCoreCompetencies(resume, request.getCoreCompetencies());
        saveTechnicalSkills(resume, request.getTechnicalSkills());
        saveProfessionalHistory(resume, request.getProfessionalHistory());
        saveFeaturedProjects(resume, request.getFeaturedProjects());

        return getResumeById(resume.getId());
    }

    @Override
    @Transactional
    public ResumeResponse updateResume(ResumeUpdateRequest request) {
        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Resume not found"
                ));

        if (!resume.getUser().getId().equals(request.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Resume does not belong to user"
            );
        }

        resume.setResumeName(request.getResumeName());
        resume.setTemplateName(request.getTemplateName());
        resume.setUpdatedAt(LocalDateTime.now());
        resumeRepository.save(resume);

        clearResumeChildren(resume.getId());

        upsertPersonalInfo(resume, request.getPersonalInfo());
        saveCoreCompetencies(resume, request.getCoreCompetencies());
        saveTechnicalSkills(resume, request.getTechnicalSkills());
        saveProfessionalHistory(resume, request.getProfessionalHistory());
        saveFeaturedProjects(resume, request.getFeaturedProjects());

        return getResumeById(resume.getId());
    }

    private void clearResumeChildren(Long resumeId) {
        for (ProfessionalHistory history :
                professionalHistoryRepository.findByResume_IdOrderByDisplayOrderAsc(resumeId)) {
            achievementRepository.deleteAll(
                    achievementRepository.findByProfessionalHistory_IdOrderByDisplayOrderAsc(
                            history.getId()
                    )
            );
        }
        professionalHistoryRepository.deleteAll(
                professionalHistoryRepository.findByResume_IdOrderByDisplayOrderAsc(resumeId)
        );

        for (FeaturedProject project :
                featuredProjectRepository.findByResume_IdOrderByDisplayOrderAsc(resumeId)) {
            projectTechnologyRepository.deleteAll(
                    projectTechnologyRepository.findByProject_IdOrderByDisplayOrderAsc(
                            project.getId()
                    )
            );
        }
        featuredProjectRepository.deleteAll(
                featuredProjectRepository.findByResume_IdOrderByDisplayOrderAsc(resumeId)
        );

        coreCompetencyRepository.deleteAll(
                coreCompetencyRepository.findByResume_IdOrderByDisplayOrderAsc(resumeId)
        );

        technicalSkillRepository.deleteAll(
                technicalSkillRepository.findByResume_IdOrderByDisplayOrderAsc(resumeId)
        );

        personalInfoRepository.flush();
        coreCompetencyRepository.flush();
        technicalSkillRepository.flush();
        professionalHistoryRepository.flush();
        featuredProjectRepository.flush();
    }

    private void savePersonalInfo(Resume resume, PersonalInfoResponse dto) {
        if (dto == null) {
            return;
        }

        personalInfoRepository.save(
                applyPersonalInfoFields(
                        PersonalInfo.builder().resume(resume).build(),
                        dto
                )
        );
    }

    private void upsertPersonalInfo(Resume resume, PersonalInfoResponse dto) {
        if (dto == null) {
            personalInfoRepository.findByResume_Id(resume.getId())
                    .ifPresent(personalInfoRepository::delete);
            return;
        }

        PersonalInfo personalInfo = personalInfoRepository.findByResume_Id(resume.getId())
                .orElseGet(() -> PersonalInfo.builder().resume(resume).build());

        personalInfoRepository.save(applyPersonalInfoFields(personalInfo, dto));
    }

    private PersonalInfo applyPersonalInfoFields(PersonalInfo personalInfo, PersonalInfoResponse dto) {
        personalInfo.setFullName(dto.getName());
        personalInfo.setTitle(dto.getTitle());
        personalInfo.setEmail(dto.getEmail());
        personalInfo.setPhone(dto.getPhone());
        personalInfo.setAddress(dto.getAddress());
        personalInfo.setLinkedinUrl(dto.getLinkedin());
        personalInfo.setGithubUrl(dto.getGithub());
        personalInfo.setSummary(dto.getSummary());
        applyPhotoFields(personalInfo, dto);
        return personalInfo;
    }

    private void applyPhotoFields(PersonalInfo personalInfo, PersonalInfoResponse dto) {
        if (dto.getPhotoImage() != null) {
            if (dto.getPhotoImage().isBlank()) {
                personalInfo.setPhotoData(null);
                personalInfo.setPhotoMimeType(null);
                return;
            }

            PhotoImageValidator.ParsedPhotoImage parsed = PhotoImageValidator.parse(
                    dto.getPhotoImage(),
                    dto.getPhotoMimeType()
            );
            personalInfo.setPhotoData(parsed.base64Data());
            personalInfo.setPhotoMimeType(parsed.mimeType());
            personalInfo.setPhotoUrl(null);
            return;
        }

        if (dto.getPhoto() == null) {
            return;
        }

        String photo = dto.getPhoto().trim();
        if (photo.isEmpty()) {
            personalInfo.setPhotoUrl(null);
            personalInfo.setPhotoData(null);
            personalInfo.setPhotoMimeType(null);
            return;
        }

        if (photo.startsWith("data:")) {
            PhotoImageValidator.ParsedPhotoImage parsed = PhotoImageValidator.parse(photo, null);
            personalInfo.setPhotoData(parsed.base64Data());
            personalInfo.setPhotoMimeType(parsed.mimeType());
            personalInfo.setPhotoUrl(null);
            return;
        }

        personalInfo.setPhotoUrl(photo);
        personalInfo.setPhotoData(null);
        personalInfo.setPhotoMimeType(null);
    }

    private String resolvePhotoForResponse(PersonalInfo info) {
        String dataUri = PhotoImageValidator.toDataUri(info.getPhotoData(), info.getPhotoMimeType());
        if (dataUri != null) {
            return dataUri;
        }

        return info.getPhotoUrl();
    }

    private void saveCoreCompetencies(Resume resume, List<String> competencies) {
        if (competencies == null) {
            return;
        }

        for (int i = 0; i < competencies.size(); i++) {
            coreCompetencyRepository.save(
                    CoreCompetency.builder()
                            .resume(resume)
                            .competencyName(competencies.get(i))
                            .displayOrder(i)
                            .build()
            );
        }
    }

    private void saveTechnicalSkills(
        Resume resume,
        List<TechnicalSkillResponse> technicalSkills
    ) {
        if (technicalSkills == null) {
            return;
        }

        int displayOrder = 0;

        for (TechnicalSkillResponse skill : technicalSkills) {

            technicalSkillRepository.save(
                    TechnicalSkill.builder()
                            .resume(resume)
                            .category(skill.getCategory())
                            .skillName(skill.getSkillName())
                            .yearsOfExperience(skill.getYearsOfExperience())
                            .displayOrder(displayOrder++)
                            .build()
            );
        }
    }

    private void saveProfessionalHistory(
            Resume resume,
            List<ProfessionalHistoryResponse> historyItems
    ) {
        if (historyItems == null) {
            return;
        }

        for (int i = 0; i < historyItems.size(); i++) {
            ProfessionalHistoryResponse item = historyItems.get(i);

            ProfessionalHistory history = professionalHistoryRepository.save(
                    ProfessionalHistory.builder()
                            .resume(resume)
                            .company(item.getCompany())
                            .role(item.getRole())
                            .location(item.getLocation())
                            .displayPeriod(item.getPeriod())
                            .displayOrder(i)
                            .build()
            );

            saveAchievements(history, item.getAchievements());
        }
    }

    private void saveAchievements(
            ProfessionalHistory history,
            List<String> achievements
    ) {
        if (achievements == null) {
            return;
        }

        for (int i = 0; i < achievements.size(); i++) {
            achievementRepository.save(
                    Achievement.builder()
                            .professionalHistory(history)
                            .description(achievements.get(i))
                            .displayOrder(i)
                            .build()
            );
        }
    }

    private void saveFeaturedProjects(
            Resume resume,
            List<FeaturedProjectResponse> projects
    ) {
        if (projects == null) {
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            FeaturedProjectResponse item = projects.get(i);

            FeaturedProject project = featuredProjectRepository.save(
                    FeaturedProject.builder()
                            .resume(resume)
                            .projectName(item.getName())
                            .description(item.getDescription())
                            .displayOrder(i)
                            .build()
            );

            saveProjectTechnologies(project, item.getTechnologies());
        }
    }

    private void saveProjectTechnologies(
            FeaturedProject project,
            List<String> technologies
    ) {
        if (technologies == null) {
            return;
        }

        for (int i = 0; i < technologies.size(); i++) {
            projectTechnologyRepository.save(
                    ProjectTechnology.builder()
                            .project(project)
                            .technologyName(technologies.get(i))
                            .displayOrder(i)
                            .build()
            );
        }
    }

    private ResumeSummaryResponse mapResumeSummary(Resume resume) {
        ResumeSummaryResponse summary = new ResumeSummaryResponse();
        summary.setResumeId(resume.getId());
        summary.setUserId(resume.getUser().getId());
        summary.setResumeName(resume.getResumeName());
        return summary;
    }

    private PersonalInfoResponse mapPersonalInfo(Long resumeId) {
        return personalInfoRepository.findByResume_Id(resumeId)
                .map(info -> {
                    PersonalInfoResponse dto = new PersonalInfoResponse();
                    dto.setName(info.getFullName());
                    dto.setTitle(info.getTitle());
                    dto.setEmail(info.getEmail());
                    dto.setPhone(info.getPhone());
                    dto.setAddress(info.getAddress());
                    dto.setLinkedin(info.getLinkedinUrl());
                    dto.setGithub(info.getGithubUrl());
                    dto.setPhoto(resolvePhotoForResponse(info));
                    dto.setSummary(info.getSummary());
                    return dto;
                })
                .orElse(null);
    }

    private List<String> mapCoreCompetencies(Long resumeId) {
        return coreCompetencyRepository
                .findByResume_IdOrderByDisplayOrderAsc(resumeId)
                .stream()
                .map(CoreCompetency::getCompetencyName)
                .toList();
    }

    private List<TechnicalSkillResponse> mapTechnicalSkills(Long resumeId) {

        return technicalSkillRepository
                .findByResume_IdOrderByDisplayOrderAsc(resumeId)
                .stream()
                .map(skill -> TechnicalSkillResponse.builder()
                        .category(skill.getCategory())
                        .skillName(skill.getSkillName())
                        .yearsOfExperience(skill.getYearsOfExperience())
                        .build())
                .toList();
    }

    private List<ProfessionalHistoryResponse> mapProfessionalHistory(Long resumeId) {
        return professionalHistoryRepository
                .findByResume_IdOrderByDisplayOrderAsc(resumeId)
                .stream()
                .map(this::mapProfessionalHistoryEntry)
                .toList();
    }

    private ProfessionalHistoryResponse mapProfessionalHistoryEntry(
            ProfessionalHistory history
    ) {
        ProfessionalHistoryResponse dto = new ProfessionalHistoryResponse();
        dto.setCompany(history.getCompany());
        dto.setRole(history.getRole());
        dto.setPeriod(history.getDisplayPeriod());
        dto.setLocation(history.getLocation());
        dto.setAchievements(
                achievementRepository
                        .findByProfessionalHistory_IdOrderByDisplayOrderAsc(history.getId())
                        .stream()
                        .map(Achievement::getDescription)
                        .toList()
        );
        return dto;
    }

    private List<FeaturedProjectResponse> mapFeaturedProjects(Long resumeId) {
        return featuredProjectRepository
                .findByResume_IdOrderByDisplayOrderAsc(resumeId)
                .stream()
                .map(this::mapFeaturedProject)
                .toList();
    }

    private FeaturedProjectResponse mapFeaturedProject(FeaturedProject project) {
        FeaturedProjectResponse dto = new FeaturedProjectResponse();
        dto.setName(project.getProjectName());
        dto.setDescription(project.getDescription());
        dto.setTechnologies(
                projectTechnologyRepository
                        .findByProject_IdOrderByDisplayOrderAsc(project.getId())
                        .stream()
                        .map(ProjectTechnology::getTechnologyName)
                        .toList()
        );
        return dto;
    }
}
