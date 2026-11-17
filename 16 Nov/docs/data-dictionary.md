# Data Dictionary — Recruitment & Placement Management System

Database: `recruitment_db` (MySQL 8.0+, InnoDB, `utf8mb4`).
Source of truth for the structure: `sql/database_schema.sql`.
Sample data: `sql/sample_data.sql`.

**Conventions used in every table**

| Convention | Meaning |
|---|---|
| Surrogate PK | Every table has a numeric auto-increment primary key (`INT`, or `BIGINT` for high-volume history/log tables). |
| Business code | Human-readable unique code where the business needs one (`CAN-2026-0001`, `COM-001`, `JOB-2026-001`, `APP-2026-0001`, `OFF-2026-001`). |
| Soft-delete only | Records are never hard-deleted. A row is retired by setting `status` / `profile_status` / `is_active`. History and log tables are append-only. |
| `created_at` | Set once when the row is created (`DEFAULT CURRENT_TIMESTAMP`). |
| `updated_at` | Re-set automatically on every update (`ON UPDATE CURRENT_TIMESTAMP`). |

---

## 1. `roles` — user roles

One row per role that a system user can have (Admin, Recruiter, Placement Officer).

| Column | Type | Constraint | Description |
|---|---|---|---|
| `role_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `role_code` | VARCHAR(30) | UNIQUE, NOT NULL | Short code, e.g. `ADMIN`, `RECRUITER`, `PLACEMENT_OFFICER`. |
| `role_name` | VARCHAR(80) | NOT NULL | Display name, e.g. `Administrator`. |
| `is_active` | TINYINT(1) | NOT NULL, DEFAULT 1 | 1 = role can be assigned, 0 = retired (soft-delete). |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the role was created. |

## 2. `users` — system users

People who log in to the system (admin, recruiters, placement officer, viewers).

| Column | Type | Constraint | Description |
|---|---|---|---|
| `user_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `username` | VARCHAR(80) | UNIQUE, NOT NULL | Login name. |
| `password_hash` | VARCHAR(255) | NOT NULL | Bcrypt hash of the password. Never plain text. |
| `full_name` | VARCHAR(120) | NOT NULL | Person's full name. |
| `email` | VARCHAR(150) | UNIQUE, NULL | Email address (unique when given). |
| `role_id` | INT | FK → `roles.role_id`, NOT NULL | The user's role. |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE', CHECK IN (ACTIVE, INACTIVE, LOCKED) | Account state. LOCKED is set after repeated failed logins. |
| `last_login_at` | DATETIME | NULL | Last successful login, NULL if never logged in. |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the account was created. |

## 3. `candidates` — job-seeking candidates

The core master table. One row per candidate.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `candidate_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `candidate_code` | VARCHAR(30) | UNIQUE, NOT NULL | Business code, e.g. `CAN-2026-0001`. |
| `full_name` | VARCHAR(150) | NOT NULL | Candidate's full name. |
| `email` | VARCHAR(150) | UNIQUE, NOT NULL | Email address; also used to contact the candidate. |
| `mobile` | VARCHAR(20) | UNIQUE, NOT NULL | 10-digit mobile number. |
| `date_of_birth` | DATE | NULL | Date of birth. |
| `gender` | VARCHAR(20) | NULL | Gender, as given by the candidate. |
| `qualification` | VARCHAR(80) | NOT NULL | Highest qualification, e.g. `BTech`, `BE`, `MCA` (see `qualifications`). |
| `college` | VARCHAR(180) | NULL | College / institute name. |
| `university` | VARCHAR(180) | NULL | Affiliating university, e.g. `RTMNU`. |
| `passing_year` | SMALLINT | NOT NULL | Year of passing (e.g. 2026). Used by eligibility checks. |
| `percentage` | DECIMAL(5,2) | NULL, CHECK 0–100 | Aggregate percentage. NULL if not provided. |
| `cgpa` | DECIMAL(4,2) | NULL | CGPA, when the college reports CGPA instead of percentage. |
| `backlog_count` | INT | NOT NULL, DEFAULT 0, CHECK ≥ 0 | Number of live backlogs. Used by eligibility checks. |
| `primary_technology` | VARCHAR(80) | NULL | Main technology, e.g. `Java`, `Python`, `React`. Drives Report 1. |
| `current_location` | VARCHAR(100) | NULL | City where the candidate currently lives. |
| `preferred_location` | VARCHAR(100) | NULL | City the candidate prefers to work in. |
| `profile_status` | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE', CHECK IN (ACTIVE, INACTIVE, ON_HOLD, BLACKLISTED) | Profile state. Only ACTIVE profiles can apply (see § Business Rules). |
| `placement_status` | VARCHAR(20) | NOT NULL, DEFAULT 'NOT_PLACED', CHECK IN (NOT_PLACED, PLACED) | Set to PLACED when a placement with `joining_status = 'JOINED'` exists. |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the profile was created. |
| `updated_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP, ON UPDATE | Last change to the profile. |

Indexes: `primary_technology`, `passing_year`, `placement_status` (eligibility searches and reports filter on these).

## 4. `skills` — master list of skills

One row per skill / technology. Shared by candidates (`candidate_skills`) and jobs (`job_skills`), so a skill name is stored once.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `skill_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `skill_name` | VARCHAR(100) | UNIQUE, NOT NULL | Skill name, e.g. `Java`, `React`, `Testing`. |
| `category` | VARCHAR(60) | NULL | Group, e.g. `Programming`, `Frontend`, `QA`, `Soft Skills`. |
| `is_active` | TINYINT(1) | NOT NULL, DEFAULT 1 | 1 = available for use, 0 = retired. |

## 5. `candidate_skills` — candidate ↔ skill (many-to-many)

Links a candidate to each skill they have, with how good they are at it.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `candidate_skill_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `candidate_id` | INT | FK → `candidates.candidate_id`, NOT NULL | The candidate. |
| `skill_id` | INT | FK → `skills.skill_id`, NOT NULL | The skill. |
| `proficiency_level` | VARCHAR(20) | NULL | e.g. `Beginner`, `Intermediate`, `Advanced`. |
| `years_experience` | DECIMAL(4,1) | NOT NULL, DEFAULT 0 | Years of experience in this skill. |
| `is_primary` | TINYINT(1) | NOT NULL, DEFAULT 0 | 1 = this is the candidate's main skill. |

UNIQUE (`candidate_id`, `skill_id`) — a candidate cannot list the same skill twice.

## 6. `companies` — recruiting companies

One row per company that hires through the placement cell.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `company_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `company_code` | VARCHAR(30) | UNIQUE, NOT NULL | Business code, e.g. `COM-001`. |
| `company_name` | VARCHAR(180) | NOT NULL | Company name, e.g. `TCS`. |
| `industry` | VARCHAR(100) | NULL | e.g. `IT Services`, `Consulting`. |
| `website` | VARCHAR(200) | NULL | Company website URL. |
| `location` | VARCHAR(120) | NULL | Head office / hiring location city. |
| `company_type` | VARCHAR(30) | NULL | e.g. `MNC`, `Startup`, `Product`, `Service`. |
| `hr_name` | VARCHAR(120) | NULL | Hiring contact person's name. |
| `hr_email` | VARCHAR(150) | NULL | Hiring contact email. |
| `hr_mobile` | VARCHAR(20) | NULL | Hiring contact mobile. |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE / INACTIVE — inactive companies get no new jobs. |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the company was added. |

Indexes: `company_name`, `status`.

## 7. `jobs` — job openings

One row per opening posted by a company. Carries the eligibility rules that applications are checked against.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `job_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `job_code` | VARCHAR(40) | UNIQUE, NOT NULL | Business code, e.g. `JOB-2026-001`. |
| `company_id` | INT | FK → `companies.company_id`, NOT NULL | The hiring company. |
| `job_title` | VARCHAR(150) | NOT NULL | e.g. `Java Developer`. |
| `employment_type` | VARCHAR(30) | NOT NULL, DEFAULT 'FULL_TIME' | `FULL_TIME` or `INTERNSHIP`. |
| `location` | VARCHAR(120) | NOT NULL | Job location city. |
| `openings` | INT | NOT NULL, CHECK > 0 | Number of vacancies. |
| `min_package_lpa` | DECIMAL(6,2) | NULL | Minimum package in LPA. |
| `max_package_lpa` | DECIMAL(6,2) | NULL | Maximum package in LPA. CHECK: min ≤ max. |
| `min_percentage` | DECIMAL(5,2) | NULL | Minimum percentage a candidate needs. |
| `min_passing_year` | SMALLINT | NULL | Earliest passing year accepted. |
| `max_passing_year` | SMALLINT | NULL | Latest passing year accepted. |
| `max_backlogs` | INT | NOT NULL, DEFAULT 0 | Maximum backlogs allowed. |
| `experience_min_years` | DECIMAL(4,1) | NOT NULL, DEFAULT 0 | Minimum experience in years. |
| `qualification_rule` | VARCHAR(255) | NULL | Accepted qualifications, e.g. `BTech/BE/MCA`. |
| `posted_date` | DATE | NOT NULL | Date the job was posted. |
| `application_deadline` | DATE | NOT NULL | Last date to apply. CHECK: deadline ≥ posted date. |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'OPEN', CHECK IN (DRAFT, OPEN, CLOSED, CANCELLED) | Only OPEN jobs accept applications. |
| `created_by` | INT | FK → `users.user_id`, NULL | User who posted the job. |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the job was created. |

Indexes: `company_id`, `status`, `application_deadline`, `location`.

## 8. `job_skills` — job ↔ required skill (many-to-many)

The skills a job requires, and how strictly.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `job_skill_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `job_id` | INT | FK → `jobs.job_id`, NOT NULL | The job. |
| `skill_id` | INT | FK → `skills.skill_id`, NOT NULL | The required skill. |
| `minimum_level` | VARCHAR(20) | NULL | Minimum level expected, e.g. `Intermediate`. |
| `mandatory_flag` | TINYINT(1) | NOT NULL, DEFAULT 1 | 1 = must-have, 0 = nice-to-have. |

UNIQUE (`job_id`, `skill_id`) — a job cannot list the same skill twice.

## 9. `applications` — candidate applies for a job

One row per (candidate, job) pair. This is the pipeline table: every status change moves the application one step forward, and each change is written to `application_history` in the same transaction.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `application_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `application_code` | VARCHAR(40) | UNIQUE, NOT NULL | Business code, e.g. `APP-2026-0001`. |
| `candidate_id` | INT | FK → `candidates.candidate_id`, NOT NULL | The applicant. |
| `job_id` | INT | FK → `jobs.job_id`, NOT NULL | The job applied for. |
| `applied_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the application was submitted. |
| `eligibility_status` | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING', CHECK IN (PENDING, ELIGIBLE, NOT_ELIGIBLE) | Result of the eligibility check. |
| `eligibility_reason` | VARCHAR(500) | NULL | Human-readable reason, required when NOT_ELIGIBLE (e.g. `Percentage 58.00 below required 65.00`). |
| `application_status` | VARCHAR(30) | NOT NULL, DEFAULT 'APPLIED', CHECK IN (APPLIED, ELIGIBILITY_VERIFIED, SHORTLISTED, INTERVIEW_SCHEDULED, INTERVIEWED, SELECTED, REJECTED, OFFERED, JOINED, WITHDRAWN) | Current pipeline stage. |
| `source` | VARCHAR(50) | NULL | Where the application came from: `College Drive`, `Website`, `Referral`, `Walk-in`. |
| `assigned_recruiter_id` | INT | FK → `users.user_id`, NULL | Recruiter handling this application. |
| `updated_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP, ON UPDATE | Last status change. |

UNIQUE (`candidate_id`, `job_id`) — a candidate cannot apply twice for the same job. Indexes: `application_status`, `assigned_recruiter_id`.

## 10. `application_history` — status-change audit trail

Append-only. One row per status change; never UPDATE or DELETE rows here.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `history_id` | BIGINT | PK, AUTO_INCREMENT | Surrogate primary key (BIGINT: this table grows fastest). |
| `application_id` | INT | FK → `applications.application_id`, NOT NULL | The application that changed. |
| `old_status` | VARCHAR(30) | NULL | Previous status; NULL when the application was created. |
| `new_status` | VARCHAR(30) | NOT NULL | Status after the change. |
| `remarks` | VARCHAR(500) | NULL | Why the status changed. |
| `changed_by` | INT | FK → `users.user_id`, NULL | User who made the change. |
| `changed_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the change happened. |

Indexes: `application_id`, `changed_at`.

## 11. `interview_rounds` — interview rounds of an application

One row per round (Round 1, Round 2, …) of an application.

| Column | Type | Constraint | Description |
|---|---|---|---|
| `interview_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `application_id` | INT | FK → `applications.application_id`, NOT NULL | The application being interviewed. |
| `round_number` | INT | NOT NULL | 1, 2, 3, … within the application. |
| `round_type` | VARCHAR(50) | NOT NULL, CHECK IN (APTITUDE, TECHNICAL, MANAGERIAL, HR) | Kind of round. |
| `scheduled_date` | DATE | NOT NULL | Date of the interview. |
| `scheduled_time` | TIME | NULL | Time of the interview. |
| `mode` | VARCHAR(20) | NOT NULL, CHECK IN (ONLINE, OFFLINE, TELEPHONIC) | How the interview is held. |
| `interviewer_name` | VARCHAR(120) | NULL | Who conducts it. |
| `meeting_or_location` | VARCHAR(255) | NULL | Meeting link (online) or venue (offline). |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'SCHEDULED', CHECK IN (SCHEDULED, COMPLETED, RESCHEDULED, CANCELLED, NO_SHOW) | Round state. |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the round was scheduled. |

UNIQUE (`application_id`, `round_number`) — no duplicate round numbers. Indexes: `scheduled_date`, `status`.

## 12. `interview_feedback` — feedback for one interview

Exactly one feedback row per interview (enforced by the UNIQUE key on `interview_id`).

| Column | Type | Constraint | Description |
|---|---|---|---|
| `feedback_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `interview_id` | INT | UNIQUE, FK → `interview_rounds.interview_id`, NOT NULL | The interview this feedback is for. One feedback per interview. |
| `technical_rating` | DECIMAL(3,1) | NULL, CHECK 0–5 | Technical score out of 5. |
| `communication_rating` | DECIMAL(3,1) | NULL, CHECK 0–5 | Communication score out of 5. |
| `problem_solving_rating` | DECIMAL(3,1) | NULL, CHECK 0–5 | Problem-solving score out of 5. |
| `overall_rating` | DECIMAL(3,1) | NULL, CHECK 0–5 | Overall score out of 5. |
| `recommendation` | VARCHAR(30) | NOT NULL, CHECK IN (NEXT_ROUND, SELECTED, REJECTED, ON_HOLD) | Interviewer's verdict. |
| `remarks` | TEXT | NULL | Free-text comments. |
| `submitted_by` | INT | FK → `users.user_id`, NULL | User who submitted the feedback. |
| `submitted_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the feedback was submitted. |

Index: `recommendation`.

## 13. `offers` — job offers

One offer per application at most (UNIQUE on `application_id`).

| Column | Type | Constraint | Description |
|---|---|---|---|
| `offer_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `offer_code` | VARCHAR(40) | UNIQUE, NOT NULL | Business code, e.g. `OFF-2026-001`. |
| `application_id` | INT | UNIQUE, FK → `applications.application_id`, NOT NULL | The application this offer belongs to. |
| `designation` | VARCHAR(120) | NOT NULL | Offered designation, e.g. `Java Developer`. |
| `offered_package_lpa` | DECIMAL(6,2) | NOT NULL | Offered package in LPA. |
| `offer_date` | DATE | NOT NULL | Date the offer was made. |
| `expected_joining_date` | DATE | NULL | Date the candidate is expected to join. |
| `offer_status` | VARCHAR(20) | NOT NULL, DEFAULT 'OFFERED', CHECK IN (OFFERED, ACCEPTED, REJECTED, EXPIRED, WITHDRAWN) | Offer state. |
| `remarks` | VARCHAR(500) | NULL | Notes about the offer. |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the offer was created. |

Index: `offer_status`.

## 14. `placements` — final placement records

Created only after a candidate joins (or is about to join on an accepted offer). One placement per offer (UNIQUE on `offer_id`).

| Column | Type | Constraint | Description |
|---|---|---|---|
| `placement_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `candidate_id` | INT | FK → `candidates.candidate_id`, NOT NULL | The placed candidate. |
| `company_id` | INT | FK → `companies.company_id`, NOT NULL | The hiring company. |
| `job_id` | INT | FK → `jobs.job_id`, NOT NULL | The job. |
| `offer_id` | INT | UNIQUE, FK → `offers.offer_id`, NOT NULL | The accepted offer behind this placement. |
| `designation` | VARCHAR(120) | NOT NULL | Final designation. |
| `final_package_lpa` | DECIMAL(6,2) | NOT NULL | Final package in LPA (matches the offer). |
| `expected_joining_date` | DATE | NULL | Expected joining date. |
| `actual_joining_date` | DATE | NULL | Actual joining date; set once the candidate joins. |
| `joining_status` | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING', CHECK IN (PENDING, JOINED, NOT_JOINED, DEFERRED, NO_SHOW) | Whether the candidate actually joined. |
| `placement_status` | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE / INACTIVE (soft-delete). |
| `created_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When the placement was recorded. |

Indexes: `candidate_id`, `company_id`, `joining_status`.

## 15. `activity_logs` — who did what

Append-only audit log for the whole system (logins, creates, updates, status changes).

| Column | Type | Constraint | Description |
|---|---|---|---|
| `log_id` | BIGINT | PK, AUTO_INCREMENT | Surrogate primary key (BIGINT: this table grows fastest). |
| `user_id` | INT | FK → `users.user_id`, NULL | Who did it; NULL for system actions. |
| `module_name` | VARCHAR(60) | NOT NULL | Module, e.g. `Candidates`, `Jobs`, `Applications`, `Offers`. |
| `action_name` | VARCHAR(80) | NOT NULL | Action, e.g. `CREATE`, `UPDATE`, `LOGIN`, `STATUS_CHANGE`. |
| `record_id` | VARCHAR(60) | NULL | Business code / id of the record touched. |
| `description` | VARCHAR(500) | NULL | Human-readable description. |
| `logged_at` | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | When it happened. |

Indexes: `user_id`, `module_name`, `logged_at`.

## 16. `qualifications` — master list of qualifications

| Column | Type | Constraint | Description |
|---|---|---|---|
| `qualification_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `qualification_code` | VARCHAR(30) | UNIQUE, NOT NULL | e.g. `BTECH`, `BE`, `MCA`, `BSC`, `MTECH`. |
| `qualification_name` | VARCHAR(80) | NOT NULL | e.g. `Bachelor of Technology`. |
| `is_active` | TINYINT(1) | NOT NULL, DEFAULT 1 | 1 = available, 0 = retired. |

## 17. `locations` — master list of cities

| Column | Type | Constraint | Description |
|---|---|---|---|
| `location_id` | INT | PK, AUTO_INCREMENT | Surrogate primary key. |
| `location_code` | VARCHAR(30) | UNIQUE, NOT NULL | e.g. `NAG`, `PUN`, `MUM`, `BLR`, `HYD`, `CHE`. |
| `city` | VARCHAR(100) | NOT NULL | City name. |
| `state` | VARCHAR(100) | NULL | State name. |
| `is_active` | TINYINT(1) | NOT NULL, DEFAULT 1 | 1 = available, 0 = retired. |

---

## Status values (all CHECK-constrained)

### `users.status`
| Value | Meaning |
|---|---|
| ACTIVE | Account can log in. |
| INACTIVE | Account disabled (soft-delete). |
| LOCKED | Locked after failed login attempts; admin must unlock. |

### `candidates.profile_status`
| Value | Meaning |
|---|---|
| ACTIVE | Profile live; candidate can apply. |
| INACTIVE | Profile deactivated (soft-delete). |
| ON_HOLD | Temporarily paused; cannot apply until re-activated. |
| BLACKLISTED | Blocked for misconduct / fake details; cannot apply. |

### `candidates.placement_status`
| Value | Meaning |
|---|---|
| NOT_PLACED | Default; no joined placement yet. |
| PLACED | Candidate has a placement with `joining_status = 'JOINED'`. |

### `jobs.status`
| Value | Meaning |
|---|---|
| DRAFT | Being prepared; not visible to candidates. |
| OPEN | Accepting applications. |
| CLOSED | Applications stopped (deadline passed or positions filled). |
| CANCELLED | Job withdrawn by the company. |

### `applications.eligibility_status`
| Value | Meaning |
|---|---|
| PENDING | Not yet checked. |
| ELIGIBLE | Passed all eligibility rules. |
| NOT_ELIGIBLE | Failed at least one rule; reason in `eligibility_reason`. |

### `applications.application_status` (pipeline order)
| Value | Meaning |
|---|---|
| APPLIED | Submitted, awaiting eligibility check. |
| ELIGIBILITY_VERIFIED | Passed eligibility. |
| SHORTLISTED | Shortlisted for interview. |
| INTERVIEW_SCHEDULED | At least one round scheduled. |
| INTERVIEWED | Interview rounds completed. |
| SELECTED | Selected after interviews. |
| REJECTED | Rejected at any stage (terminal). |
| OFFERED | Offer released. |
| JOINED | Candidate joined the company (terminal, success). |
| WITHDRAWN | Candidate withdrew (terminal). |

Valid transitions go forward along this order; REJECTED and WITHDRAWN can be entered from any non-terminal stage; JOINED is only reachable from OFFERED. A jump such as APPLIED → JOINED is invalid (see `TEST_CASES.md`).

### `interview_rounds.status`
| Value | Meaning |
|---|---|
| SCHEDULED | Booked, not yet held. |
| COMPLETED | Held; feedback is due. |
| RESCHEDULED | Moved to a new date/time. |
| CANCELLED | Called off. |
| NO_SHOW | Candidate did not turn up. |

### `interview_feedback.recommendation`
| Value | Meaning |
|---|---|
| NEXT_ROUND | Proceed to the next round. |
| SELECTED | Recommend selection. |
| REJECTED | Do not proceed. |
| ON_HOLD | Wait and compare with other candidates. |

### `offers.offer_status`
| Value | Meaning |
|---|---|
| OFFERED | Released, awaiting response. |
| ACCEPTED | Candidate accepted. |
| REJECTED | Candidate declined. |
| EXPIRED | Offer validity lapsed. |
| WITHDRAWN | Offer pulled back. |

### `placements.joining_status`
| Value | Meaning |
|---|---|
| PENDING | Accepted offer, joining date in the future. |
| JOINED | Candidate joined on (or before) the expected date. |
| NOT_JOINED | Candidate did not join. |
| DEFERRED | Joining postponed to a later date. |
| NO_SHOW | Candidate never turned up. |

---

## Business rules

1. **Eligibility check order.** When a candidate applies, check in this order and stop at the first failure, recording the reason in `eligibility_reason`:
   1. Candidate `profile_status` must be `ACTIVE`.
   2. Job `status` must be `OPEN`.
   3. Today's date must be on or before `application_deadline`.
   4. No existing application for the same (candidate, job) pair.
   5. Candidate `percentage` ≥ job `min_percentage` (when both are set).
   6. Candidate `passing_year` ≥ job `min_passing_year` (when set).
   7. Candidate `passing_year` ≤ job `max_passing_year` (when set).
   8. Candidate `backlog_count` ≤ job `max_backlogs`.
   If all pass, `eligibility_status = 'ELIGIBLE'`; otherwise `NOT_ELIGIBLE`.
2. **One application per candidate per job.** Enforced by UNIQUE (`candidate_id`, `job_id`).
3. **One feedback per interview.** Enforced by UNIQUE (`interview_id`) on `interview_feedback`.
4. **One offer per application.** Enforced by UNIQUE (`application_id`) on `offers`.
5. **One placement per offer.** Enforced by UNIQUE (`offer_id`) on `placements`.
6. **Offer only after selection.** An offer may be created only when the application is `SELECTED` (or already `OFFERED` for a re-issue); never for an `APPLIED` / `REJECTED` application.
7. **Placement only after joining (or accepted offer).** A placement is created only from an `ACCEPTED` offer. `candidates.placement_status` becomes `PLACED` only when `joining_status = 'JOINED'`.
8. **Status changes are audited.** Every `application_status` change inserts a row in `application_history` in the same transaction.
9. **Interview completion and feedback are paired.** Marking a round `COMPLETED` and inserting its feedback happen in one transaction.
10. **Transactions.** Selection → offer, joining → placement, status change → history, and interview completion → feedback are each a single transaction; any failure rolls the whole step back.
11. **Soft-delete only.** Never `DELETE` a candidate, company, job or user row. Set `profile_status` / `status` / `is_active` instead. History and log rows are never edited or deleted.
12. **Passwords.** Store only bcrypt hashes in `users.password_hash`. Never plain text.
13. **Validation ranges.** `percentage` 0–100, `backlog_count` ≥ 0, `openings` > 0, `min_package_lpa` ≤ `max_package_lpa`, `application_deadline` ≥ `posted_date`, all ratings 0–5.
14. **Recruiter assignment.** Every application has one assigned recruiter (`assigned_recruiter_id`), used by the recruiter-performance report.
