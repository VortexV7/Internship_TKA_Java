# Test Cases — Recruitment & Placement Management System

Database: `recruitment_db` (MySQL). Load the schema and sample data first:

```bash
mysql -u root -p < sql/database_schema.sql
mysql -u root -p recruitment_db < sql/sample_data.sql
```

**How to run a test:** execute the SQL in *Input / Precondition* (or the equivalent app action) and compare the outcome with *Expected Result*. A test passes only when the database state matches the expected result exactly — including the `application_history` and `candidates.placement_status` side effects.

**Sample-data references used below:** candidate `CAN-2026-0006` (Priya Nair, 58.00%, 0 backlogs, 2026), `CAN-2026-0008` (Kavya Reddy, 59.00%, 2 backlogs), `CAN-2026-0029` (Kabir Anand, profile INACTIVE), job `JOB-2026-004` (min 65.00%, max 0 backlogs), job `JOB-2026-011` (CLOSED), job `JOB-2026-012` (DRAFT), application `APP-2026-0001` (APPLIED), application `APP-2026-0038` (JOINED).

| ID | Module | Test Case | Input / Precondition | Expected Result | Type |
|---|---|---|---|---|---|
| TC-01 | Eligibility | Inactive candidate cannot apply | Candidate `CAN-2026-0029` (`profile_status = 'INACTIVE'`) applies for `JOB-2026-002` (OPEN) | Application is rejected at rule 1; `eligibility_status = 'NOT_ELIGIBLE'`, reason mentions inactive profile | Negative |
| TC-02 | Eligibility | Closed job rejects applications | Active candidate applies for `JOB-2026-011` (`status = 'CLOSED'`) | Application is rejected at rule 2; `eligibility_status = 'NOT_ELIGIBLE'`, reason mentions job closed | Negative |
| TC-03 | Eligibility | Expired deadline rejects applications | Active candidate applies for a job whose `application_deadline` is yesterday (set `JOB-2026-001` deadline to a past date in a test copy) | Application is rejected at rule 3; `eligibility_status = 'NOT_ELIGIBLE'`, reason mentions deadline passed | Negative |
| TC-04 | Eligibility | Duplicate application is caught by eligibility | Candidate `CAN-2026-0001` applies again for `JOB-2026-002` (already has `APP-2026-0038` for it) | Application is rejected at rule 4 (duplicate); no second row is created — UNIQUE (`candidate_id`, `job_id`) also blocks it | Negative |
| TC-05 | Eligibility | Percentage below minimum is rejected | Candidate `CAN-2026-0006` (58.00%) applies for `JOB-2026-004` (min 65.00%) | `eligibility_status = 'NOT_ELIGIBLE'`, `eligibility_reason = 'Percentage 58.00 below required 65.00'` (same as sample `APP-2026-0030`) | Negative |
| TC-06 | Eligibility | Passing year below minimum is rejected | Candidate with `passing_year = 2024` applies for `JOB-2026-003` (`min_passing_year = 2025`) | `eligibility_status = 'NOT_ELIGIBLE'`, reason mentions passing year below minimum | Negative |
| TC-07 | Eligibility | Passing year above maximum is rejected | Candidate with `passing_year = 2026` applies for `JOB-2026-007` (`max_passing_year = 2025`) | `eligibility_status = 'NOT_ELIGIBLE'`, reason mentions passing year above maximum | Negative |
| TC-08 | Eligibility | Backlogs above maximum are rejected | Candidate `CAN-2026-0008` (2 backlogs) applies for `JOB-2026-006` (`max_backlogs = 1`) | `eligibility_status = 'NOT_ELIGIBLE'`, `eligibility_reason = 'Backlog count 2 exceeds allowed maximum 1'` (same as sample `APP-2026-0032`) | Negative |
| TC-09 | Eligibility | Candidate passing every rule is eligible | Candidate `CAN-2026-0001` (87.50%, 0 backlogs, 2025) applies for `JOB-2026-002` (min 60.00%, 2024–2026, max 1 backlog) on a fresh test copy | `eligibility_status = 'ELIGIBLE'`, `eligibility_reason` is NULL, `application_status = 'APPLIED'` | Positive |
| TC-10 | Eligibility | Draft job accepts no applications | Active candidate applies for `JOB-2026-012` (`status = 'DRAFT'`) | Application is rejected; `eligibility_status = 'NOT_ELIGIBLE'`, reason mentions job not open | Negative |
| TC-11 | Candidates | Duplicate email is rejected | Insert a candidate with `email = 'aarav.sharma01@gmail.com'` (already used by `CAN-2026-0001`) | INSERT fails with a duplicate-key error on `uk_candidates_email`; no row is added | Negative |
| TC-12 | Candidates | Duplicate mobile is rejected | Insert a candidate with `mobile = '9823014567'` (already used by `CAN-2026-0001`) | INSERT fails with a duplicate-key error on `uk_candidates_mobile`; no row is added | Negative |
| TC-13 | Candidates | Percentage above 100 is rejected | Insert a candidate with `percentage = 105.00` | INSERT fails on `chk_candidates_percentage` (0–100) | Negative |
| TC-14 | Candidates | Negative percentage is rejected | Insert a candidate with `percentage = -5.00` | INSERT fails on `chk_candidates_percentage` (0–100) | Negative |
| TC-15 | Candidates | Negative backlog count is rejected | Insert a candidate with `backlog_count = -1` | INSERT fails on `chk_candidates_backlog` (≥ 0) | Negative |
| TC-16 | Candidates | Boundary percentages 0 and 100 are accepted | Insert candidates with `percentage = 0.00` and `percentage = 100.00` | Both INSERTs succeed; both rows are stored | Positive |
| TC-17 | Applications | Same candidate + job cannot be applied twice | Insert a second application with `candidate_id = 1, job_id = 2` (already `APP-2026-0038`) | INSERT fails on UNIQUE (`candidate_id`, `job_id`) | Negative |
| TC-18 | Status Workflow | Invalid jump APPLIED → JOINED is rejected | Try to set `APP-2026-0001` (`APPLIED`) directly to `JOINED` | The application layer rejects the transition; status stays `APPLIED` and no history row is written for the jump | Negative |
| TC-19 | Status Workflow | Valid forward transitions work and are audited | Move `APP-2026-0009` `ELIGIBILITY_VERIFIED → SHORTLISTED` | Status becomes `SHORTLISTED`; a new `application_history` row appears with `old_status = 'ELIGIBILITY_VERIFIED'`, `new_status = 'SHORTLISTED'` | Positive |
| TC-20 | Status Workflow | Rejection is terminal | Set `APP-2026-0014` (`SHORTLISTED`) to `REJECTED`, then try to move it to `INTERVIEW_SCHEDULED` | First change succeeds; the second is rejected and the status stays `REJECTED` | Positive |
| TC-21 | Status Workflow | JOINED is reachable only from OFFERED | Move `APP-2026-0036` (`OFFERED`) to `JOINED` | Transition succeeds; application becomes `JOINED` with a matching history row | Positive |
| TC-22 | Status Workflow | Candidate can withdraw an application | Set `APP-2026-0002` (`APPLIED`) to `WITHDRAWN` | Status becomes `WITHDRAWN`; history row records `old_status = 'APPLIED'` | Positive |
| TC-23 | Interviews | Duplicate round number is rejected | Insert a second round with `application_id = 23, round_number = 1` (round 1 already exists) | INSERT fails on UNIQUE (`application_id`, `round_number`) | Negative |
| TC-24 | Interviews | One feedback per interview only | Insert a second `interview_feedback` row for `interview_id = 5` (feedback already exists) | INSERT fails on UNIQUE (`interview_id`) | Negative |
| TC-25 | Interviews | Feedback needs a completed interview | Try to submit feedback for `interview_id = 1` (`status = 'SCHEDULED'`) | The application layer blocks it; no feedback row is created until the round is `COMPLETED` | Negative |
| TC-26 | Offers | Offer without a selected application is rejected | Try to create an offer for `APP-2026-0001` (`APPLIED`, never interviewed) | Offer creation is rejected; no row appears in `offers` | Negative |
| TC-27 | Offers | Second offer for the same application is rejected | Insert a second offer with `application_id = 28` (already has `OFF-2026-001`) | INSERT fails on UNIQUE (`application_id`) on `offers` | Negative |
| TC-28 | Placements | Placement without an offer is rejected | Try to insert a placement with an `offer_id` that does not exist | INSERT fails on foreign key `fk_place_offer` | Negative |
| TC-29 | Placements | Placement needs an accepted offer | Try to create a placement from `OFF-2026-001` (`offer_status = 'OFFERED'`, not accepted) | Placement creation is rejected; no row appears in `placements` | Negative |
| TC-30 | Transactions | Rollback on offer failure leaves no orphan | In one transaction: INSERT an offer for `APP-2026-0029` and UPDATE it to `OFFERED`; force the UPDATE to fail (e.g. invalid status value) | Transaction rolls back: no offer row exists and the application is still `SELECTED` | Negative |
| TC-31 | Transactions | Rollback on joining failure keeps candidate unplaced | In one transaction: INSERT a placement for candidate 12 (offer `OFF-2026-003`) and UPDATE `candidates.placement_status` to `PLACED`; force the placement INSERT to fail (e.g. duplicate `offer_id`) | Transaction rolls back: no placement row exists and candidate 12 is still `NOT_PLACED` | Negative |
| TC-32 | Transactions | Status change and history are written together | Move `APP-2026-0010` to `SHORTLISTED` via the application's status-change function | The status update and the `application_history` row commit together; neither exists without the other | Positive |
| TC-33 | Reports | Report 5 (company-wise placements) is correct | Run Report 5 from `sql/reports.sql` on the sample data | Returns 4 rows: Infosys 2, Wipro 1, Capgemini 1, Tech Mahindra 1 — Infosys first, total 5 joined placements | Positive |
| TC-34 | Reports | Report 3 (applications by status) adds up | Run Report 3 on the sample data | The 10 status counts add up to 45, matching `SELECT COUNT(*) FROM applications` | Positive |
| TC-35 | Reports | Report 1 (candidates by technology) adds up | Run Report 1 on the sample data | The technology counts add up to 30, matching `SELECT COUNT(*) FROM candidates` | Positive |
| TC-36 | Security | Passwords are stored as hashes, never plain text | `SELECT username, password_hash FROM users;` and attempt login with the stored value as the password | `password_hash` values all start with the bcrypt prefix `$2a$10$`; logging in with the hash itself as the password fails | Positive |
| TC-37 | Soft Delete | Deactivating a candidate keeps the row | Set candidate 29 `profile_status = 'INACTIVE'` instead of deleting the row | The row still exists (`SELECT` finds it); the candidate can no longer apply (see TC-01); no `DELETE` was used | Positive |
| TC-38 | Jobs | Deadline before posted date is rejected | Insert a job with `posted_date = '2026-02-10'` and `application_deadline = '2026-02-01'` | INSERT fails on `chk_jobs_deadline` (deadline ≥ posted date) | Negative |
| TC-39 | Jobs | Zero openings are rejected | Insert a job with `openings = 0` | INSERT fails on `chk_jobs_openings` (openings > 0) | Negative |
| TC-40 | Jobs | Minimum package above maximum is rejected | Insert a job with `min_package_lpa = 8.00` and `max_package_lpa = 5.00` | INSERT fails on `chk_jobs_package` (min ≤ max) | Negative |

**Coverage summary:** 40 test cases — Eligibility 10 (TC-01–TC-10), Candidates 6 (TC-11–TC-16), Applications 1 (TC-17), Status Workflow 5 (TC-18–TC-22), Interviews 3 (TC-23–TC-25), Offers 2 (TC-26–TC-27), Placements 2 (TC-28–TC-29), Transactions 3 (TC-30–TC-32), Reports 3 (TC-33–TC-35), Security 1 (TC-36), Soft Delete 1 (TC-37), Jobs 3 (TC-38–TC-40). Positive: 13, Negative: 27.
