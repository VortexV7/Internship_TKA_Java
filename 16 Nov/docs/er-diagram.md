# ER Diagram — Recruitment & Placement Management System

Database: `recruitment_db` (MySQL). This diagram matches `sql/database_schema.sql` exactly — same 17 tables, same keys, same relationships.

> How to view: paste the Mermaid block below into any Mermaid viewer (Mermaid Live Editor, GitHub, VS Code with a Mermaid extension, or Notion). A standalone visual version is also available as [`er-diagram.html`](er-diagram.html).

```mermaid
erDiagram
    roles {
        int role_id PK
        string role_code UK
        string role_name
        tinyint is_active
        datetime created_at
    }
    users {
        int user_id PK
        string username UK
        string password_hash
        string full_name
        string email UK
        int role_id FK
        string status
        datetime last_login_at
        datetime created_at
    }
    candidates {
        int candidate_id PK
        string candidate_code UK
        string full_name
        string email UK
        string mobile UK
        date date_of_birth
        string gender
        string qualification
        string college
        string university
        int passing_year
        decimal percentage
        decimal cgpa
        int backlog_count
        string primary_technology
        string current_location
        string preferred_location
        string profile_status
        string placement_status
        datetime created_at
        datetime updated_at
    }
    skills {
        int skill_id PK
        string skill_name UK
        string category
        tinyint is_active
    }
    candidate_skills {
        int candidate_skill_id PK
        int candidate_id FK
        int skill_id FK
        string proficiency_level
        decimal years_experience
        tinyint is_primary
    }
    companies {
        int company_id PK
        string company_code UK
        string company_name
        string industry
        string website
        string location
        string company_type
        string hr_name
        string hr_email
        string hr_mobile
        string status
        datetime created_at
    }
    jobs {
        int job_id PK
        string job_code UK
        int company_id FK
        string job_title
        string employment_type
        string location
        int openings
        decimal min_package_lpa
        decimal max_package_lpa
        decimal min_percentage
        int min_passing_year
        int max_passing_year
        int max_backlogs
        decimal experience_min_years
        string qualification_rule
        date posted_date
        date application_deadline
        string status
        int created_by FK
        datetime created_at
    }
    job_skills {
        int job_skill_id PK
        int job_id FK
        int skill_id FK
        string minimum_level
        tinyint mandatory_flag
    }
    applications {
        int application_id PK
        string application_code UK
        int candidate_id FK
        int job_id FK
        datetime applied_at
        string eligibility_status
        string eligibility_reason
        string application_status
        string source
        int assigned_recruiter_id FK
        datetime updated_at
    }
    application_history {
        bigint history_id PK
        int application_id FK
        string old_status
        string new_status
        string remarks
        int changed_by FK
        datetime changed_at
    }
    interview_rounds {
        int interview_id PK
        int application_id FK
        int round_number
        string round_type
        date scheduled_date
        time scheduled_time
        string mode
        string interviewer_name
        string meeting_or_location
        string status
        datetime created_at
    }
    interview_feedback {
        int feedback_id PK
        int interview_id FK_UK
        decimal technical_rating
        decimal communication_rating
        decimal problem_solving_rating
        decimal overall_rating
        string recommendation
        string remarks
        int submitted_by FK
        datetime submitted_at
    }
    offers {
        int offer_id PK
        string offer_code UK
        int application_id FK_UK
        string designation
        decimal offered_package_lpa
        date offer_date
        date expected_joining_date
        string offer_status
        string remarks
        datetime created_at
    }
    placements {
        int placement_id PK
        int candidate_id FK
        int company_id FK
        int job_id FK
        int offer_id FK_UK
        string designation
        decimal final_package_lpa
        date expected_joining_date
        date actual_joining_date
        string joining_status
        string placement_status
        datetime created_at
    }
    activity_logs {
        bigint log_id PK
        int user_id FK
        string module_name
        string action_name
        string record_id
        string description
        datetime logged_at
    }
    qualifications {
        int qualification_id PK
        string qualification_code UK
        string qualification_name
        tinyint is_active
    }
    locations {
        int location_id PK
        string location_code UK
        string city
        string state
        tinyint is_active
    }

    roles ||--o{ users : "has"
    users ||--o{ jobs : "posts"
    users ||--o{ applications : "assigned as recruiter"
    users ||--o{ application_history : "changes status"
    users ||--o{ interview_feedback : "submits"
    users ||--o{ activity_logs : "performs"

    candidates ||--o{ candidate_skills : "has"
    skills ||--o{ candidate_skills : "listed in"
    candidates ||--o{ applications : "applies through"
    candidates ||--o{ placements : "placed through"

    companies ||--o{ jobs : "posts"
    companies ||--o{ placements : "hires through"

    jobs ||--o{ job_skills : "requires"
    skills ||--o{ job_skills : "required by"
    jobs ||--o{ applications : "receives"
    jobs ||--o{ placements : "filled by"

    applications ||--o{ application_history : "tracked in"
    applications ||--o{ interview_rounds : "interviewed in"
    applications ||--o| offers : "gets at most one"
    interview_rounds ||--o| interview_feedback : "gets one"
    offers ||--o| placements : "leads to at most one"
```

## Relationship summary

The system is built around one pipeline: **candidate → application → interview → offer → placement**.

- **Users & roles** — A `role` (ADMIN / RECRUITER / PLACEMENT_OFFICER) has many `users`. A user posts `jobs`, is assigned as recruiter on `applications`, records status changes in `application_history`, submits `interview_feedback`, and generates `activity_logs`. All of these are one-to-many from `users`.
- **Candidates & skills** — A `candidate` has many `candidate_skills`, and a `skill` appears in many `candidate_skills`: a many-to-many link resolved by `candidate_skills`, with one row flagged `is_primary = 1` for the candidate's main skill.
- **Companies & jobs** — A `company` posts many `jobs`; each job belongs to one company. A `job` requires many `job_skills`, and a `skill` is required by many jobs — again a many-to-many link, resolved by `job_skills`, with `mandatory_flag` marking must-have skills.
- **Applications (the pipeline)** — A `candidate` applies to many `jobs` and a `job` receives many applications, but each (candidate, job) pair occurs at most once — enforced by UNIQUE (`candidate_id`, `job_id`) on `applications`. Every application is assigned to one recruiter (`users`).
- **History** — Every status change of an `application` writes one row in `application_history` (one-to-many, append-only). `changed_by` points back to `users`.
- **Interviews** — An `application` has many `interview_rounds` (round 1, 2, …), each with a unique (`application_id`, `round_number`). Each interview round has at most one `interview_feedback` row (one-to-one, enforced by UNIQUE on `interview_feedback.interview_id`).
- **Offers & placements** — An `application` gets at most one `offer` (one-to-one), and an `offer` leads to at most one `placement` (one-to-one). A `placement` also points back to its `candidate`, `company` and `job`, so company-wise and technology-wise placement reports need no extra joins through offers.
- **Lookups** — `qualifications` and `locations` are standalone master tables (no foreign keys point at them in this version; `candidates.qualification` and `candidates.current_location` store the display values). They exist so the application can offer fixed dropdowns instead of free text.
- **Audit** — `activity_logs` records who did what in which module; `user_id` is nullable so system-generated entries are possible.

### Cardinality cheat-sheet

| Relationship | Type | Enforced by |
|---|---|---|
| roles → users | 1 : N | FK `users.role_id` |
| users → jobs / applications / history / feedback / logs | 1 : N | FKs on each table |
| candidates ↔ skills | N : M | `candidate_skills` |
| companies → jobs | 1 : N | FK `jobs.company_id` |
| jobs ↔ skills | N : M | `job_skills` |
| candidates ↔ jobs | N : M | `applications` (UNIQUE pair) |
| applications → application_history | 1 : N | FK `application_history.application_id` |
| applications → interview_rounds | 1 : N | FK `interview_rounds.application_id` |
| interview_rounds → interview_feedback | 1 : 1 | UNIQUE `interview_feedback.interview_id` |
| applications → offers | 1 : 1 | UNIQUE `offers.application_id` |
| offers → placements | 1 : 1 | UNIQUE `placements.offer_id` |
| candidates / companies / jobs → placements | 1 : N | FKs on `placements` |
