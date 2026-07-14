DROP TABLE IF EXISTS resume_versions CASCADE;
DROP TABLE IF EXISTS languages CASCADE;
DROP TABLE IF EXISTS certifications CASCADE;
DROP TABLE IF EXISTS educations CASCADE;
DROP TABLE IF EXISTS project_technologies CASCADE;
DROP TABLE IF EXISTS featured_projects CASCADE;
DROP TABLE IF EXISTS achievements CASCADE;
DROP TABLE IF EXISTS professional_history CASCADE;
DROP TABLE IF EXISTS technical_skills CASCADE;
DROP TABLE IF EXISTS core_competencies CASCADE;
DROP TABLE IF EXISTS personal_info CASCADE;
DROP TABLE IF EXISTS resumes CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
   id BIGSERIAL PRIMARY KEY,
   email VARCHAR(255) NOT NULL UNIQUE,
   password_hash VARCHAR(255) NOT NULL,
   first_name VARCHAR(100),
   last_name VARCHAR(100),
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE resumes (
     id BIGSERIAL PRIMARY KEY,
     user_id BIGINT NOT NULL,
     resume_name VARCHAR(255) NOT NULL,
     template_name VARCHAR(100),
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT fk_resume_user
         FOREIGN KEY (user_id)
             REFERENCES users(id)
             ON DELETE CASCADE
);

CREATE TABLE personal_info (
       id BIGSERIAL PRIMARY KEY,
       resume_id BIGINT NOT NULL UNIQUE,
       full_name VARCHAR(255) NOT NULL,
       title VARCHAR(255),
       email VARCHAR(255),
       phone VARCHAR(100),
       address VARCHAR(255),
       linkedin_url VARCHAR(500),
       github_url VARCHAR(500),
       photo_url VARCHAR(500),
       summary TEXT,
       CONSTRAINT fk_personal_info_resume
           FOREIGN KEY (resume_id)
               REFERENCES resumes(id)
               ON DELETE CASCADE
);

CREATE TABLE core_competencies (
       id BIGSERIAL PRIMARY KEY,
       resume_id BIGINT NOT NULL,
       competency_name VARCHAR(255) NOT NULL,
       display_order INTEGER DEFAULT 0,
       CONSTRAINT fk_competency_resume
           FOREIGN KEY (resume_id)
               REFERENCES resumes(id)
               ON DELETE CASCADE
);

CREATE TABLE technical_skills (
      id BIGSERIAL PRIMARY KEY,
      resume_id BIGINT NOT NULL,
      category VARCHAR(100) NOT NULL,
      skill_name VARCHAR(255) NOT NULL,
      display_order INTEGER DEFAULT 0,
      CONSTRAINT fk_skill_resume
          FOREIGN KEY (resume_id)
              REFERENCES resumes(id)
              ON DELETE CASCADE
);

CREATE TABLE professional_history (
          id BIGSERIAL PRIMARY KEY,
          resume_id BIGINT NOT NULL,
          company VARCHAR(255) NOT NULL,
          role VARCHAR(255) NOT NULL,
          location VARCHAR(255),
          start_date DATE,
          end_date DATE,
          current_position BOOLEAN DEFAULT FALSE,
          display_period VARCHAR(100),
          display_order INTEGER DEFAULT 0,
          CONSTRAINT fk_professional_history_resume
              FOREIGN KEY (resume_id)
                  REFERENCES resumes(id)
                  ON DELETE CASCADE
);

CREATE TABLE achievements (
      id BIGSERIAL PRIMARY KEY,
      professional_history_id BIGINT NOT NULL,
      description TEXT NOT NULL,
      display_order INTEGER DEFAULT 0,
      CONSTRAINT fk_achievement_professional_history
          FOREIGN KEY (professional_history_id)
              REFERENCES professional_history(id)
              ON DELETE CASCADE
);

CREATE TABLE featured_projects (
       id BIGSERIAL PRIMARY KEY,
       resume_id BIGINT NOT NULL,
       project_name VARCHAR(255) NOT NULL,
       description TEXT,
       display_order INTEGER DEFAULT 0,
       CONSTRAINT fk_project_resume
           FOREIGN KEY (resume_id)
               REFERENCES resumes(id)
               ON DELETE CASCADE
);

CREATE TABLE project_technologies (
      id BIGSERIAL PRIMARY KEY,
      project_id BIGINT NOT NULL,
      technology_name VARCHAR(255) NOT NULL,
      display_order INTEGER DEFAULT 0,
      CONSTRAINT fk_project_technology
          FOREIGN KEY (project_id)
              REFERENCES featured_projects(id)
              ON DELETE CASCADE
);

CREATE TABLE educations (
        id BIGSERIAL PRIMARY KEY,
        resume_id BIGINT NOT NULL,
        institution VARCHAR(255) NOT NULL,
        degree VARCHAR(255),
        field_of_study VARCHAR(255),
        start_date DATE,
        end_date DATE,
        description TEXT,
        display_order INTEGER DEFAULT 0,
        CONSTRAINT fk_education_resume
            FOREIGN KEY (resume_id)
                REFERENCES resumes(id)
                ON DELETE CASCADE
);

CREATE TABLE certifications (
        id BIGSERIAL PRIMARY KEY,
        resume_id BIGINT NOT NULL,
        certification_name VARCHAR(255) NOT NULL,
        issuing_organization VARCHAR(255),
        issue_date DATE,
        expiration_date DATE,
        credential_id VARCHAR(255),
        credential_url VARCHAR(500),
        display_order INTEGER DEFAULT 0,
        CONSTRAINT fk_certification_resume
            FOREIGN KEY (resume_id)
                REFERENCES resumes(id)
                ON DELETE CASCADE
);

CREATE TABLE languages (
       id BIGSERIAL PRIMARY KEY,
       resume_id BIGINT NOT NULL,
       language_name VARCHAR(100) NOT NULL,
       proficiency_level VARCHAR(100),
       display_order INTEGER DEFAULT 0,
       CONSTRAINT fk_language_resume
           FOREIGN KEY (resume_id)
               REFERENCES resumes(id)
               ON DELETE CASCADE
);

CREATE TABLE resume_versions (
     id BIGSERIAL PRIMARY KEY,
     resume_id BIGINT NOT NULL,
     version_number INTEGER NOT NULL,
     snapshot_json JSONB NOT NULL,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     CONSTRAINT fk_resume_version_resume
         FOREIGN KEY (resume_id)
             REFERENCES resumes(id)
             ON DELETE CASCADE
);

CREATE INDEX idx_resume_user ON resumes(user_id);
CREATE INDEX idx_personal_info_resume ON personal_info(resume_id);
CREATE INDEX idx_competency_resume ON core_competencies(resume_id);
CREATE INDEX idx_skill_resume ON technical_skills(resume_id);
CREATE INDEX idx_history_resume ON professional_history(resume_id);
CREATE INDEX idx_achievement_history ON achievements(professional_history_id);
CREATE INDEX idx_project_resume ON featured_projects(resume_id);
CREATE INDEX idx_project_technology ON project_technologies(project_id);
CREATE INDEX idx_education_resume ON educations(resume_id);
CREATE INDEX idx_certification_resume ON certifications(resume_id);
CREATE INDEX idx_language_resume ON languages(resume_id);
CREATE INDEX idx_resume_version_resume ON resume_versions(resume_id);
