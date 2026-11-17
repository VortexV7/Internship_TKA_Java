-- ============================================================================
-- Recruitment & Placement Management System — Reports
-- File : sql/reports.sql
-- Run  : mysql -u root -p recruitment_db < sql/reports.sql
--        (or paste any single query into MySQL Workbench / the mysql client)
--
-- All 10 reports from the project specification, plus two bonus lists.
-- Each report has a comment header explaining what it answers.
-- ============================================================================

USE recruitment_db;

-- ----------------------------------------------------------------------------
-- Report 1 — Candidate count by technology
-- Answers: how many candidates do we have for each primary technology?
-- ----------------------------------------------------------------------------
SELECT
    primary_technology AS technology,
    COUNT(*) AS candidate_count
FROM candidates
GROUP BY primary_technology
ORDER BY candidate_count DESC, technology;

-- ----------------------------------------------------------------------------
-- Report 2 — Open jobs by company
-- Answers: which companies currently have open jobs, and how many?
-- ----------------------------------------------------------------------------
SELECT
    c.company_name,
    COUNT(j.job_id) AS open_jobs,
    SUM(j.openings) AS total_openings
FROM jobs j
JOIN companies c ON c.company_id = j.company_id
WHERE j.status = 'OPEN'
GROUP BY c.company_id, c.company_name
ORDER BY open_jobs DESC, c.company_name;

-- ----------------------------------------------------------------------------
-- Report 3 — Applications by status
-- Answers: where is every application in the pipeline right now?
-- ----------------------------------------------------------------------------
SELECT
    application_status,
    COUNT(*) AS application_count
FROM applications
GROUP BY application_status
ORDER BY application_count DESC;

-- ----------------------------------------------------------------------------
-- Report 4 — Recruitment funnel
-- Answers: how many applications reach each major stage of the funnel?
-- (An application counts for a stage if it has reached at least that stage.
--  Stage order: APPLIED -> ELIGIBILITY_VERIFIED -> SHORTLISTED ->
--  INTERVIEW_SCHEDULED -> INTERVIEWED -> SELECTED -> OFFERED -> JOINED.)
-- ----------------------------------------------------------------------------
SELECT
    'Applied' AS stage,
    COUNT(*) AS application_count
FROM applications
UNION ALL
SELECT 'Eligibility verified',
    COUNT(*) FROM applications
    WHERE application_status IN
        ('ELIGIBILITY_VERIFIED', 'SHORTLISTED', 'INTERVIEW_SCHEDULED',
         'INTERVIEWED', 'SELECTED', 'OFFERED', 'JOINED')
UNION ALL
SELECT 'Shortlisted',
    COUNT(*) FROM applications
    WHERE application_status IN
        ('SHORTLISTED', 'INTERVIEW_SCHEDULED', 'INTERVIEWED',
         'SELECTED', 'OFFERED', 'JOINED')
UNION ALL
SELECT 'Interview scheduled',
    COUNT(*) FROM applications
    WHERE application_status IN
        ('INTERVIEW_SCHEDULED', 'INTERVIEWED', 'SELECTED', 'OFFERED', 'JOINED')
UNION ALL
SELECT 'Interviewed',
    COUNT(*) FROM applications
    WHERE application_status IN
        ('INTERVIEWED', 'SELECTED', 'OFFERED', 'JOINED')
UNION ALL
SELECT 'Selected',
    COUNT(*) FROM applications
    WHERE application_status IN ('SELECTED', 'OFFERED', 'JOINED')
UNION ALL
SELECT 'Offered',
    COUNT(*) FROM applications
    WHERE application_status IN ('OFFERED', 'JOINED')
UNION ALL
SELECT 'Joined',
    COUNT(*) FROM applications
    WHERE application_status = 'JOINED';

-- ----------------------------------------------------------------------------
-- Report 5 — Company-wise placements
-- Answers: which companies have taken the most of our candidates?
-- (Exact query from the project specification.)
-- ----------------------------------------------------------------------------
SELECT
    c.company_name,
    COUNT(p.placement_id) AS placed_count
FROM placements p
JOIN companies c ON c.company_id = p.company_id
WHERE p.joining_status = 'JOINED'
GROUP BY c.company_name
ORDER BY placed_count DESC;

-- ----------------------------------------------------------------------------
-- Report 6 — Technology-wise placements
-- Answers: which technologies are actually getting candidates placed?
-- ----------------------------------------------------------------------------
SELECT
    c.primary_technology AS technology,
    COUNT(p.placement_id) AS placed_count
FROM placements p
JOIN candidates c ON c.candidate_id = p.candidate_id
WHERE p.joining_status = 'JOINED'
GROUP BY c.primary_technology
ORDER BY placed_count DESC, technology;

-- ----------------------------------------------------------------------------
-- Report 7 — Recruiter performance
-- Answers: how much work is each recruiter handling, and how far do
-- their applications get? (Applications assigned per recruiter, split
-- into selected/offered/joined vs. the rest.)
-- ----------------------------------------------------------------------------
SELECT
    u.full_name AS recruiter,
    COUNT(a.application_id) AS total_applications,
    SUM(CASE WHEN a.application_status IN ('SELECTED', 'OFFERED', 'JOINED')
             THEN 1 ELSE 0 END) AS selected_or_better,
    SUM(CASE WHEN a.application_status = 'JOINED'
             THEN 1 ELSE 0 END) AS joined_count
FROM applications a
JOIN users u ON u.user_id = a.assigned_recruiter_id
GROUP BY a.assigned_recruiter_id, u.full_name
ORDER BY total_applications DESC;

-- ----------------------------------------------------------------------------
-- Report 8 — Pending interviews
-- Answers: which interviews are still scheduled (not yet completed)?
-- ----------------------------------------------------------------------------
SELECT
    ir.interview_id,
    a.application_code,
    c.full_name AS candidate_name,
    j.job_title,
    ir.round_number,
    ir.round_type,
    ir.scheduled_date,
    ir.scheduled_time,
    ir.mode,
    ir.interviewer_name,
    ir.meeting_or_location
FROM interview_rounds ir
JOIN applications a ON a.application_id = ir.application_id
JOIN candidates c ON c.candidate_id = a.candidate_id
JOIN jobs j ON j.job_id = a.job_id
WHERE ir.status = 'SCHEDULED'
ORDER BY ir.scheduled_date, ir.scheduled_time;

-- ----------------------------------------------------------------------------
-- Report 9 — Offer-to-joining conversion
-- Answers: of all offers made, how many turned into actual joinings?
-- ----------------------------------------------------------------------------
SELECT
    (SELECT COUNT(*) FROM offers) AS total_offers,
    (SELECT COUNT(*) FROM offers WHERE offer_status = 'ACCEPTED') AS accepted_offers,
    (SELECT COUNT(*) FROM placements WHERE joining_status = 'JOINED') AS joined_count,
    ROUND(
        (SELECT COUNT(*) FROM placements WHERE joining_status = 'JOINED')
        / NULLIF((SELECT COUNT(*) FROM offers), 0) * 100, 2
    ) AS offer_to_joining_percent;

-- ----------------------------------------------------------------------------
-- Report 10 — Job aging
-- Answers: how long has each open job been waiting? (Oldest first —
-- the jobs that need attention.)
-- ----------------------------------------------------------------------------
SELECT
    j.job_code,
    j.job_title,
    c.company_name,
    j.posted_date,
    j.application_deadline,
    DATEDIFF(CURDATE(), j.posted_date) AS days_open,
    (SELECT COUNT(*) FROM applications a WHERE a.job_id = j.job_id)
        AS application_count
FROM jobs j
JOIN companies c ON c.company_id = j.company_id
WHERE j.status = 'OPEN'
ORDER BY days_open DESC;

-- ============================================================================
-- BONUS REPORTS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Bonus A — Placed candidates list
-- Answers: who is placed, where, and at what package?
-- ----------------------------------------------------------------------------
SELECT
    c.candidate_code,
    c.full_name AS candidate_name,
    c.primary_technology AS technology,
    comp.company_name,
    p.designation,
    p.final_package_lpa,
    p.actual_joining_date
FROM placements p
JOIN candidates c ON c.candidate_id = p.candidate_id
JOIN companies comp ON comp.company_id = p.company_id
WHERE p.joining_status = 'JOINED'
ORDER BY p.actual_joining_date, c.full_name;

-- ----------------------------------------------------------------------------
-- Bonus B — Unplaced candidates list
-- Answers: who is still looking, so recruiters know whom to push next?
-- ----------------------------------------------------------------------------
SELECT
    c.candidate_code,
    c.full_name AS candidate_name,
    c.primary_technology AS technology,
    c.qualification,
    c.passing_year,
    c.percentage,
    c.preferred_location,
    (SELECT COUNT(*) FROM applications a WHERE a.candidate_id = c.candidate_id)
        AS application_count
FROM candidates c
WHERE c.placement_status = 'NOT_PLACED'
  AND c.profile_status = 'ACTIVE'
ORDER BY c.percentage DESC, c.full_name;
