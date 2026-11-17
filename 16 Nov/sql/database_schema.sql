-- ============================================================================
-- Recruitment & Placement Management System — Database Schema
-- Database : MySQL 8.0+  |  Engine: InnoDB  |  Charset: utf8mb4
-- File     : sql/database_schema.sql
-- Project  : Internship project (CSE student)
--
-- How to run:
--   mysql -u root -p < sql/database_schema.sql
--   mysql -u root -p recruitment_db < sql/sample_data.sql
--
-- Design notes
--   * 3NF: every non-key attribute depends on the key, the whole key and
--     nothing but the key. Lookup data (skills, qualifications, locations)
--     lives in its own tables instead of being repeated as free text.
--   * Every table has a numeric surrogate primary key (INT / BIGINT) plus a
--     readable business code where the business needs one
--     (CAN-2026-0001, COM-001, JOB-2026-001, APP-2026-0001, OFF-2026-001).
--   * Soft-delete only: records are deactivated (status / is_active) and
--     never hard-deleted. History tables are append-only.
--
-- TRANSACTIONS REQUIRED (do these in one transaction, never in two steps):
--   1. Selection -> Offer:   INSERT offers + UPDATE applications to OFFERED.
--   2. Joining -> Placement: INSERT placements + UPDATE candidates
--      placement_status to PLACED + UPDATE offers to ACCEPTED (if needed).
--   3. Status change -> History: UPDATE applications.application_status +
--      INSERT application_history in the same transaction.
--   4. Interview completion -> Feedback: UPDATE interview_rounds to
--      COMPLETED + INSERT interview_feedback in the same transaction.
--   If any step fails, ROLLBACK the whole transaction.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS recruitment_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE recruitment_db;

SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- ----------------------------------------------------------------------------
-- 1. roles — user roles (ADMIN, RECRUITER, PLACEMENT_OFFICER)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    role_id     INT          NOT NULL AUTO_INCREMENT,
    role_code   VARCHAR(30)  NOT NULL,
    role_name   VARCHAR(80)  NOT NULL,
    is_active   TINYINT(1)   NOT NULL DEFAULT 1,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_roles_code (role_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 2. users — system users (admin / recruiters / placement officer / viewer)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    user_id       INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(80)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(120) NOT NULL,
    email         VARCHAR(150) NULL,
    role_id       INT          NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME     NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED')),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 3. candidates — job-seeking candidates (the core master table)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS candidates (
    candidate_id       INT           NOT NULL AUTO_INCREMENT,
    candidate_code     VARCHAR(30)   NOT NULL,
    full_name          VARCHAR(150)  NOT NULL,
    email              VARCHAR(150)  NOT NULL,
    mobile             VARCHAR(20)   NOT NULL,
    date_of_birth      DATE          NULL,
    gender             VARCHAR(20)   NULL,
    qualification      VARCHAR(80)   NOT NULL,
    college            VARCHAR(180)  NULL,
    university         VARCHAR(180)  NULL,
    passing_year       SMALLINT      NOT NULL,
    percentage         DECIMAL(5,2)  NULL,
    cgpa               DECIMAL(4,2)  NULL,
    backlog_count      INT           NOT NULL DEFAULT 0,
    primary_technology VARCHAR(80)   NULL,
    current_location   VARCHAR(100)  NULL,
    preferred_location VARCHAR(100)  NULL,
    profile_status     VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    placement_status   VARCHAR(20)   NOT NULL DEFAULT 'NOT_PLACED',
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (candidate_id),
    UNIQUE KEY uk_candidates_code (candidate_code),
    UNIQUE KEY uk_candidates_email (email),
    UNIQUE KEY uk_candidates_mobile (mobile),
    CONSTRAINT chk_candidates_percentage
        CHECK (percentage IS NULL OR (percentage >= 0 AND percentage <= 100)),
    CONSTRAINT chk_candidates_backlog CHECK (backlog_count >= 0),
    CONSTRAINT chk_candidates_profile_status
        CHECK (profile_status IN ('ACTIVE', 'INACTIVE', 'ON_HOLD', 'BLACKLISTED')),
    CONSTRAINT chk_candidates_placement_status
        CHECK (placement_status IN ('NOT_PLACED', 'PLACED')),
    INDEX idx_candidates_technology (primary_technology),
    INDEX idx_candidates_passing_year (passing_year),
    INDEX idx_candidates_placement_status (placement_status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 4. skills — master list of technologies / skills
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS skills (
    skill_id   INT          NOT NULL AUTO_INCREMENT,
    skill_name VARCHAR(100) NOT NULL,
    category   VARCHAR(60)  NULL,
    is_active  TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (skill_id),
    UNIQUE KEY uk_skills_name (skill_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 5. candidate_skills — many-to-many: candidate <-> skill
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS candidate_skills (
    candidate_skill_id INT           NOT NULL AUTO_INCREMENT,
    candidate_id       INT           NOT NULL,
    skill_id           INT           NOT NULL,
    proficiency_level  VARCHAR(20)   NULL,
    years_experience   DECIMAL(4,1)  NOT NULL DEFAULT 0,
    is_primary         TINYINT(1)    NOT NULL DEFAULT 0,
    PRIMARY KEY (candidate_skill_id),
    UNIQUE KEY uk_candidate_skill (candidate_id, skill_id),
    CONSTRAINT fk_cs_candidate FOREIGN KEY (candidate_id)
        REFERENCES candidates (candidate_id),
    CONSTRAINT fk_cs_skill FOREIGN KEY (skill_id)
        REFERENCES skills (skill_id),
    INDEX idx_cs_skill (skill_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 6. companies — recruiting companies
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS companies (
    company_id   INT          NOT NULL AUTO_INCREMENT,
    company_code VARCHAR(30)  NOT NULL,
    company_name VARCHAR(180) NOT NULL,
    industry     VARCHAR(100) NULL,
    website      VARCHAR(200) NULL,
    location     VARCHAR(120) NULL,
    company_type VARCHAR(30)  NULL,
    hr_name      VARCHAR(120) NULL,
    hr_email     VARCHAR(150) NULL,
    hr_mobile    VARCHAR(20)  NULL,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (company_id),
    UNIQUE KEY uk_companies_code (company_code),
    INDEX idx_companies_name (company_name),
    INDEX idx_companies_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 7. jobs — job openings posted by companies
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jobs (
    job_id              INT           NOT NULL AUTO_INCREMENT,
    job_code            VARCHAR(40)   NOT NULL,
    company_id          INT           NOT NULL,
    job_title           VARCHAR(150)  NOT NULL,
    employment_type     VARCHAR(30)   NOT NULL DEFAULT 'FULL_TIME',
    location            VARCHAR(120)  NOT NULL,
    openings            INT           NOT NULL,
    min_package_lpa     DECIMAL(6,2)  NULL,
    max_package_lpa     DECIMAL(6,2)  NULL,
    min_percentage      DECIMAL(5,2)  NULL,
    min_passing_year    SMALLINT      NULL,
    max_passing_year    SMALLINT      NULL,
    max_backlogs        INT           NOT NULL DEFAULT 0,
    experience_min_years DECIMAL(4,1) NOT NULL DEFAULT 0,
    qualification_rule  VARCHAR(255)  NULL,
    posted_date         DATE          NOT NULL,
    application_deadline DATE         NOT NULL,
    status              VARCHAR(20)   NOT NULL DEFAULT 'OPEN',
    created_by          INT           NULL,
    created_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (job_id),
    UNIQUE KEY uk_jobs_code (job_code),
    CONSTRAINT chk_jobs_openings CHECK (openings > 0),
    CONSTRAINT chk_jobs_package
        CHECK (min_package_lpa IS NULL OR max_package_lpa IS NULL
               OR min_package_lpa <= max_package_lpa),
    CONSTRAINT chk_jobs_deadline CHECK (application_deadline >= posted_date),
    CONSTRAINT chk_jobs_status
        CHECK (status IN ('DRAFT', 'OPEN', 'CLOSED', 'CANCELLED')),
    CONSTRAINT fk_jobs_company FOREIGN KEY (company_id)
        REFERENCES companies (company_id),
    CONSTRAINT fk_jobs_created_by FOREIGN KEY (created_by)
        REFERENCES users (user_id),
    INDEX idx_jobs_company (company_id),
    INDEX idx_jobs_status (status),
    INDEX idx_jobs_deadline (application_deadline),
    INDEX idx_jobs_location (location)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 8. job_skills — many-to-many: job <-> required skill
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS job_skills (
    job_skill_id   INT          NOT NULL AUTO_INCREMENT,
    job_id         INT          NOT NULL,
    skill_id       INT          NOT NULL,
    minimum_level  VARCHAR(20)  NULL,
    mandatory_flag TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (job_skill_id),
    UNIQUE KEY uk_job_skill (job_id, skill_id),
    CONSTRAINT fk_js_job FOREIGN KEY (job_id) REFERENCES jobs (job_id),
    CONSTRAINT fk_js_skill FOREIGN KEY (skill_id) REFERENCES skills (skill_id),
    INDEX idx_js_skill (skill_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 9. applications — candidate applies for a job (one row per candidate+job)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS applications (
    application_id       INT           NOT NULL AUTO_INCREMENT,
    application_code     VARCHAR(40)   NOT NULL,
    candidate_id         INT           NOT NULL,
    job_id               INT           NOT NULL,
    applied_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    eligibility_status   VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    eligibility_reason   VARCHAR(500)  NULL,
    application_status   VARCHAR(30)   NOT NULL DEFAULT 'APPLIED',
    source               VARCHAR(50)   NULL,
    assigned_recruiter_id INT          NULL,
    updated_at           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (application_id),
    UNIQUE KEY uk_applications_code (application_code),
    UNIQUE KEY uk_applications_candidate_job (candidate_id, job_id),
    CONSTRAINT chk_applications_eligibility
        CHECK (eligibility_status IN ('PENDING', 'ELIGIBLE', 'NOT_ELIGIBLE')),
    CONSTRAINT chk_applications_status
        CHECK (application_status IN (
            'APPLIED', 'ELIGIBILITY_VERIFIED', 'SHORTLISTED',
            'INTERVIEW_SCHEDULED', 'INTERVIEWED', 'SELECTED',
            'REJECTED', 'OFFERED', 'JOINED', 'WITHDRAWN')),
    CONSTRAINT fk_app_candidate FOREIGN KEY (candidate_id)
        REFERENCES candidates (candidate_id),
    CONSTRAINT fk_app_job FOREIGN KEY (job_id) REFERENCES jobs (job_id),
    CONSTRAINT fk_app_recruiter FOREIGN KEY (assigned_recruiter_id)
        REFERENCES users (user_id),
    INDEX idx_app_status (application_status),
    INDEX idx_app_recruiter (assigned_recruiter_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 10. application_history — append-only audit trail of status changes.
--     Never UPDATE or DELETE rows here.
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS application_history (
    history_id     BIGINT       NOT NULL AUTO_INCREMENT,
    application_id INT          NOT NULL,
    old_status     VARCHAR(30)  NULL,
    new_status     VARCHAR(30)  NOT NULL,
    remarks        VARCHAR(500) NULL,
    changed_by     INT          NULL,
    changed_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (history_id),
    CONSTRAINT fk_hist_application FOREIGN KEY (application_id)
        REFERENCES applications (application_id),
    CONSTRAINT fk_hist_changed_by FOREIGN KEY (changed_by)
        REFERENCES users (user_id),
    INDEX idx_hist_application (application_id),
    INDEX idx_hist_changed_at (changed_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 11. interview_rounds — one row per interview round of an application
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS interview_rounds (
    interview_id         INT          NOT NULL AUTO_INCREMENT,
    application_id       INT          NOT NULL,
    round_number         INT          NOT NULL,
    round_type           VARCHAR(50)  NOT NULL,
    scheduled_date       DATE         NOT NULL,
    scheduled_time       TIME         NULL,
    mode                 VARCHAR(20)  NOT NULL,
    interviewer_name     VARCHAR(120) NULL,
    meeting_or_location  VARCHAR(255) NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'SCHEDULED',
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (interview_id),
    UNIQUE KEY uk_interview_app_round (application_id, round_number),
    CONSTRAINT chk_interview_round_type
        CHECK (round_type IN ('APTITUDE', 'TECHNICAL', 'MANAGERIAL', 'HR')),
    CONSTRAINT chk_interview_mode
        CHECK (mode IN ('ONLINE', 'OFFLINE', 'TELEPHONIC')),
    CONSTRAINT chk_interview_status
        CHECK (status IN ('SCHEDULED', 'COMPLETED', 'RESCHEDULED',
                          'CANCELLED', 'NO_SHOW')),
    CONSTRAINT fk_interview_application FOREIGN KEY (application_id)
        REFERENCES applications (application_id),
    INDEX idx_interview_date (scheduled_date),
    INDEX idx_interview_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 12. interview_feedback — exactly one feedback row per interview
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS interview_feedback (
    feedback_id           INT           NOT NULL AUTO_INCREMENT,
    interview_id          INT           NOT NULL,
    technical_rating      DECIMAL(3,1)  NULL,
    communication_rating  DECIMAL(3,1)  NULL,
    problem_solving_rating DECIMAL(3,1) NULL,
    overall_rating        DECIMAL(3,1)  NULL,
    recommendation        VARCHAR(30)   NOT NULL,
    remarks               TEXT          NULL,
    submitted_by          INT           NULL,
    submitted_at          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (feedback_id),
    UNIQUE KEY uk_feedback_interview (interview_id),
    CONSTRAINT chk_feedback_tech
        CHECK (technical_rating IS NULL OR (technical_rating >= 0 AND technical_rating <= 5)),
    CONSTRAINT chk_feedback_comm
        CHECK (communication_rating IS NULL OR (communication_rating >= 0 AND communication_rating <= 5)),
    CONSTRAINT chk_feedback_ps
        CHECK (problem_solving_rating IS NULL OR (problem_solving_rating >= 0 AND problem_solving_rating <= 5)),
    CONSTRAINT chk_feedback_overall
        CHECK (overall_rating IS NULL OR (overall_rating >= 0 AND overall_rating <= 5)),
    CONSTRAINT chk_feedback_recommendation
        CHECK (recommendation IN ('NEXT_ROUND', 'SELECTED', 'REJECTED', 'ON_HOLD')),
    CONSTRAINT fk_feedback_interview FOREIGN KEY (interview_id)
        REFERENCES interview_rounds (interview_id),
    CONSTRAINT fk_feedback_submitted_by FOREIGN KEY (submitted_by)
        REFERENCES users (user_id),
    INDEX idx_feedback_recommendation (recommendation)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 13. offers — job offer for an application (at most one offer per application)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS offers (
    offer_id              INT           NOT NULL AUTO_INCREMENT,
    offer_code            VARCHAR(40)   NOT NULL,
    application_id        INT           NOT NULL,
    designation           VARCHAR(120)  NOT NULL,
    offered_package_lpa   DECIMAL(6,2)  NOT NULL,
    offer_date            DATE          NOT NULL,
    expected_joining_date DATE          NULL,
    offer_status          VARCHAR(20)   NOT NULL DEFAULT 'OFFERED',
    remarks               VARCHAR(500)  NULL,
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (offer_id),
    UNIQUE KEY uk_offers_code (offer_code),
    UNIQUE KEY uk_offers_application (application_id),
    CONSTRAINT chk_offers_status
        CHECK (offer_status IN ('OFFERED', 'ACCEPTED', 'REJECTED', 'EXPIRED', 'WITHDRAWN')),
    CONSTRAINT fk_offers_application FOREIGN KEY (application_id)
        REFERENCES applications (application_id),
    INDEX idx_offers_status (offer_status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 14. placements — final placement record (created only after joining)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS placements (
    placement_id          INT           NOT NULL AUTO_INCREMENT,
    candidate_id          INT           NOT NULL,
    company_id            INT           NOT NULL,
    job_id                INT           NOT NULL,
    offer_id              INT           NOT NULL,
    designation           VARCHAR(120)  NOT NULL,
    final_package_lpa     DECIMAL(6,2)  NOT NULL,
    expected_joining_date DATE          NULL,
    actual_joining_date   DATE          NULL,
    joining_status        VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    placement_status      VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (placement_id),
    UNIQUE KEY uk_placements_offer (offer_id),
    CONSTRAINT chk_placements_joining
        CHECK (joining_status IN ('PENDING', 'JOINED', 'NOT_JOINED', 'DEFERRED', 'NO_SHOW')),
    CONSTRAINT fk_place_candidate FOREIGN KEY (candidate_id)
        REFERENCES candidates (candidate_id),
    CONSTRAINT fk_place_company FOREIGN KEY (company_id)
        REFERENCES companies (company_id),
    CONSTRAINT fk_place_job FOREIGN KEY (job_id) REFERENCES jobs (job_id),
    CONSTRAINT fk_place_offer FOREIGN KEY (offer_id) REFERENCES offers (offer_id),
    INDEX idx_place_candidate (candidate_id),
    INDEX idx_place_company (company_id),
    INDEX idx_place_joining (joining_status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 15. activity_logs — append-only log of who did what, in which module
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS activity_logs (
    log_id       BIGINT       NOT NULL AUTO_INCREMENT,
    user_id      INT          NULL,
    module_name  VARCHAR(60)  NOT NULL,
    action_name  VARCHAR(80)  NOT NULL,
    record_id    VARCHAR(60)  NULL,
    description  VARCHAR(500) NULL,
    logged_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    INDEX idx_log_user (user_id),
    INDEX idx_log_module (module_name),
    INDEX idx_log_logged_at (logged_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 16. qualifications — master list (BTech, BE, MCA, BSc, MTech, ...)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS qualifications (
    qualification_id   INT          NOT NULL AUTO_INCREMENT,
    qualification_code VARCHAR(30)  NOT NULL,
    qualification_name VARCHAR(80)  NOT NULL,
    is_active          TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (qualification_id),
    UNIQUE KEY uk_qual_code (qualification_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- ----------------------------------------------------------------------------
-- 17. locations — master list of cities
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS locations (
    location_id   INT          NOT NULL AUTO_INCREMENT,
    location_code VARCHAR(30)  NOT NULL,
    city          VARCHAR(100) NOT NULL,
    state         VARCHAR(100) NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (location_id),
    UNIQUE KEY uk_loc_code (location_code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

-- End of schema. 17 tables created.
-- Next step: load sample data (sql/sample_data.sql).
