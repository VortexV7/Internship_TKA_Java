# Recruitment & Placement Management System

A Java + MySQL console application that manages the complete recruitment and
placement lifecycle of candidates in a training institute / placement cell —
from registration and eligibility checking, through interviews and offers,
to joining and final placement.

> **HTML/CSS pages in `prototype/` are UI prototypes only.** They show how a
> future web version could look and are **not connected to the Java backend**.

## Project overview (from the abstract)

The system keeps centralised information about candidates, companies, job
openings, applications, eligibility, interview rounds, feedback, offers,
joining status and final placements, replacing spreadsheets and manual
tracking with a structured, database-driven process.

**Workflow**

```
Candidate Registration -> Eligibility Verification -> Job Application
-> Shortlisting -> Interview Scheduling -> Interview Feedback
-> Selection -> Offer -> Joining -> Placement
```

**Eligibility Engine** — before an application is created, the system checks,
in order: candidate is active, job is open, deadline is valid, candidate has
not already applied, percentage criteria, passing-year criteria, backlog
criteria. The result is `ELIGIBLE` or `NOT ELIGIBLE` with a clear reason.

**Business rules** — duplicate email / mobile blocked, duplicate application
blocked, percentage 0–100, backlog count never negative, only valid status
transitions allowed (e.g. `APPLIED -> JOINED` is blocked), soft-delete only
(deactivate / close, never hard-delete history), transactions for multi-table
operations, audit history for every status change.

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Database | MySQL 8 |
| Database access | JDBC — `Connection`, `PreparedStatement`, `ResultSet` only (no string-concatenated SQL) |
| Concepts used | OOP, Collections (`List<Candidate>`, `List<Job>`, `List<Application>`, `Map<String, Integer>`), Exception Handling, File Handling (`activity_log.txt`) |
| Build | Maven (`pom.xml`) or plain `javac` (see below) |
| UI prototype | Static HTML + CSS (`prototype/`) — not connected to the backend |

**Not used** (as specified): Servlet, JSP, Spring / Spring Boot, Hibernate.

## Folder structure

```
recruitment-placement-system/
├── pom.xml                          Maven build (Java 17, mysql-connector-j 8.0.33)
├── compile.sh / compile.bat         Compile with javac (no Maven needed)
├── run.sh / run.bat                 Run the console application
├── activity_log.txt                 File log - e.g. "Candidate Created: CAN-2026-0001"
├── README.md                        This file
├── TEST_CASES.md                    30+ test cases
├── src/main/java/com/recruitment/
│   ├── model/        Candidate, Company, Job, Application, Interview,
│   │                 InterviewFeedback, Offer, Placement, Role, User, Skill,
│   │                 ApplicationHistory, ActivityLog, EligibilityResult
│   ├── dao/          CandidateDAO, CompanyDAO, JobDAO, ApplicationDAO,
│   │                 InterviewDAO, OfferDAO, PlacementDAO, ReportDAO
│   ├── service/      CandidateService, CompanyService, JobService,
│   │                 EligibilityService, ApplicationService, InterviewService,
│   │                 OfferService, PlacementService, ReportService
│   ├── util/         DBConnection, ActivityLogger, ValidationUtil
│   ├── exception/    CandidateNotFoundException, JobNotFoundException,
│   │                 DuplicateApplicationException, CandidateNotEligibleException,
│   │                 ValidationException
│   └── app/          RecruitmentApp (console menu)
├── src/main/resources/db.properties Database settings (edit username/password)
├── sql/
│   ├── database_schema.sql          CREATE DATABASE + all 17 tables, constraints, indexes
│   ├── sample_data.sql              30 candidates, 8 companies, 12 jobs, 45 applications, ...
│   └── reports.sql                  All 10 required reporting queries
├── docs/
│   ├── data-dictionary.md           Table / column dictionary
│   ├── er-diagram.md                ER diagram (Mermaid)
│   └── er-diagram.html              Visual ER diagram (standalone page)
└── prototype/                       Static HTML/CSS UI prototype (see prototype/README.md)
    ├── index.html  login.html  candidates.html  companies.html  jobs.html
    ├── applications.html  interviews.html  offers.html  placements.html  reports.html
    ├── css/style.css                Deep Ink Navy / Teal / Warm Amber theme, Sora + Inter
    └── js/app.js                    Sidebar, filters, demo toast only - no backend calls
```

**Architecture** (as specified):

```
Main Application (RecruitmentApp)
        |
     Service  (business logic, eligibility, workflow, transactions)
        |
       DAO    (database operations, PreparedStatement)
        |
      JDBC
        |
      MySQL (recruitment_db)
```

Model = data, DAO = database operations, Service = business logic,
Main = console menu.

## Setup — 3 steps to run

### Step 1 — Create the database and load sample data

```bash
mysql -u root -p < sql/database_schema.sql
mysql -u root -p recruitment_db < sql/sample_data.sql
```

This creates database `recruitment_db` with all 17 tables and loads:
3 roles, 5 users, 10 skills, 30 candidates, 8 companies, 12 jobs,
45 applications, 22 interview rounds, 16 feedback records, 9 offers,
6 placements (5 joined). User passwords are stored as hashes only —
the sample file contains placeholder hashes; set real hashes before use.

Verify with:

```sql
USE recruitment_db;
SHOW TABLES;                                  -- expect 17 tables
SELECT COUNT(*) FROM candidates;               -- 30
SELECT COUNT(*) FROM applications;             -- 45
SELECT COUNT(*) FROM placements WHERE joining_status = 'JOINED';  -- 5
```

### Step 2 — Configure the database connection and get the driver

1. Edit `src/main/resources/db.properties` — set `db.username` and
   `db.password` to match your MySQL installation. The defaults are
   `jdbc:mysql://localhost:3306/recruitment_db`, user `root`.
2. Get the MySQL JDBC driver (`mysql-connector-j-8.0.33.jar`) and put it
   in `lib/`:
   - **With Maven:** `mvn compile` downloads it automatically.
   - **Without Maven:** download it from
     https://dev.mysql.com/downloads/connector/j/
     (Platform Independent, ZIP archive) and copy the `.jar` into `lib/`.

### Step 3 — Compile and run

**With Maven:**

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.recruitment.app.RecruitmentApp
# or: java -cp target/classes:<path-to-connector-jar> com.recruitment.app.RecruitmentApp
```

**Without Maven (Windows / Linux):**

```bash
./compile.sh     # Windows: compile.bat
./run.sh         # Windows: run.bat
```

If MySQL is not running, the app still opens and shows a clear warning;
database actions report the error instead of crashing.

## Console menu

```
==================================
RECRUITMENT & PLACEMENT SYSTEM
==================================
1. Candidates
2. Companies
3. Jobs
4. Applications
5. Interviews
6. Offers
7. Placements
8. Reports
9. Exit
```

Each item opens a submenu, for example Candidates offers: Add, View All,
Search by ID, Search by Technology, Update, Deactivate. Reports offers all
10 reports from the database specification plus Placed / Unplaced lists.

## Workflow walkthrough (happy path)

1. **Candidates → Add Candidate** — e.g. `CAN-2026-0031`, BTech, 2026,
   82.50%, Java, 0 backlogs. Duplicate email/mobile is rejected.
2. **Jobs → View Open Jobs** — pick an open job, e.g. `JOB-2026-001`
   (Java Developer, TCS, min 60%, max 0 backlogs).
3. **Applications → Apply for a Job** — the Eligibility Engine runs first.
   If the candidate is eligible, the application is created as `APPLIED`
   and a history row is written. If not, the exact reason is shown
   (e.g. "Percentage 58.00 is below the required minimum 65.00.").
4. **Applications → Shortlist Application** — `APPLIED -> SHORTLISTED`.
   Trying `APPLIED -> JOINED` directly is blocked.
5. **Interviews → Schedule Interview** — round 1, TECHNICAL, ONLINE.
   Application moves to `INTERVIEW_SCHEDULED`.
6. **Interviews → Submit Interview Feedback** — ratings 0–5 and a
   recommendation (`NEXT_ROUND` / `SELECTED` / `REJECTED` / `ON_HOLD`).
   `SELECTED` moves the application to `SELECTED`.
7. **Offers → Create Offer** — only for a `SELECTED` application; this is
   one transaction: create offer → application `OFFERED` → history row.
8. **Offers → Update Offer Status** — set `ACCEPTED` when the candidate
   accepts.
9. **Placements → Confirm Joining** — one transaction: create placement →
   candidate `PLACED` → application `JOINED` → history row.

Every step is also written to `activity_log.txt`, e.g.:

```
Candidate Created: CAN-2026-0001
Job Created: JOB-2026-001
Candidate Shortlisted: APP-2026-0001
```

## Reports

All 10 reports from the database specification (see `sql/reports.sql`,
also available in the console under **8. Reports**):

1. Candidate count by technology
2. Open jobs by company
3. Applications by status
4. Recruitment funnel
5. Company-wise placements
6. Technology-wise placements
7. Recruiter performance
8. Pending interviews
9. Offer-to-joining conversion
10. Job aging

Plus: Placed Candidates and Unplaced Candidates lists.

## Database design — summary

- 17 tables, Third Normal Form, numeric surrogate primary keys plus
  readable business codes (`candidate_code`, `job_code`,
  `application_code`, `offer_code`).
- Key constraints: `UNIQUE(email)`, `UNIQUE(mobile)` on candidates;
  `UNIQUE(candidate_id, job_id)` on applications;
  `UNIQUE(application_id, round_number)` on interview rounds;
  `UNIQUE(interview_id)` on feedback; `UNIQUE(application_id)` on offers;
  `UNIQUE(offer_id)` on placements. CHECK constraints on percentage
  (0–100), backlog (≥ 0), openings (> 0), package range, ratings (0–5)
  and all controlled status values.
- Status values (full list in `docs/data-dictionary.md`):
  candidate `ACTIVE / INACTIVE / ON_HOLD / BLACKLISTED`, placement
  `NOT_PLACED / PLACED`, job `DRAFT / OPEN / CLOSED / CANCELLED`,
  eligibility `PENDING / ELIGIBLE / NOT_ELIGIBLE`, application
  `APPLIED / ELIGIBILITY_VERIFIED / SHORTLISTED / INTERVIEW_SCHEDULED /
  INTERVIEWED / SELECTED / REJECTED / OFFERED / JOINED / WITHDRAWN`,
  interview `SCHEDULED / COMPLETED / RESCHEDULED / CANCELLED / NO_SHOW`,
  feedback `NEXT_ROUND / SELECTED / REJECTED / ON_HOLD`, offer
  `OFFERED / ACCEPTED / REJECTED / EXPIRED / WITHDRAWN`, joining
  `PENDING / JOINED / NOT_JOINED / DEFERRED / NO_SHOW`.
- Transactions (all use `setAutoCommit(false)` / `commit()` / `rollback()`):
  selection → offer → status → history; joining → placement → candidate
  status → application → history; status change → history; interview
  completion → feedback → application.
- History is never hard-deleted: candidates are deactivated, companies set
  INACTIVE, jobs CLOSED / CANCELLED.

## UI prototype

Open `prototype/index.html` in a browser (no server needed). Design:
Deep Ink Navy `#0E2233`, Teal `#0FA3A3`, Warm Amber `#E8A317`, warm
off-white `#F7F5F0` background; Sora headings, Inter body text. Same demo
data as `sample_data.sql` (30 candidates, 8 companies, 12 jobs,
45 applications, 9 offers, 6 placements). See `prototype/README.md`.

## Completion checklist (from the specifications)

- [x] Database connectivity works (JDBC, `db.properties`)
- [x] Candidate, company and job modules work
- [x] Eligibility check works (7 rules, in order, with reasons)
- [x] Duplicate application is blocked (service + UNIQUE constraint)
- [x] Interview, offer and placement flow works
- [x] PreparedStatement is used everywhere (no concatenated SQL)
- [x] Custom exceptions are handled
- [x] Reports work (10 reports)
- [x] Console menu works (9 items + submenus)
- [x] 17 tables with correct PKs, FKs, UNIQUEs, CHECKs, indexes
- [x] At least 30 candidates and meaningful transaction data loaded
- [x] No plain-text passwords stored
- [x] Historical records retained (soft-delete only)
- [x] Transaction rollback demonstrated (offer / joining / feedback flows)

## Test cases

See `TEST_CASES.md` — 30+ cases covering eligibility (each rule),
duplicates, invalid status jumps, deadline / percentage / backlog
validation, interview / offer / placement flow and reports.
