-- ============================================================================
-- Recruitment & Placement Management System — Sample Data
-- File : sql/sample_data.sql
--
-- Load order (this file assumes the schema already exists):
--   mysql -u root -p < sql/database_schema.sql
--   mysql -u root -p recruitment_db < sql/sample_data.sql
--
-- Notes
--   * All INSERTs use explicit IDs so foreign keys line up every time.
--     Re-running this file on a non-empty database will fail on UNIQUE keys;
--     reload on a fresh database (drop + recreate) instead.
--   * All names, emails and phone numbers below are fictional sample data
--     for a student project. No real person's data is used.
--   * Password hashes are PLACEHOLDER hashes only. Set real bcrypt hashes
--     before use; never store plain-text passwords.
--
-- Row counts in this file (verify with: grep -c "^INSERT INTO <table>" ):
--   roles 3 | users 5 | skills 10 | qualifications 5 | locations 6
--   candidates 30 | companies 8 | jobs 12
--   candidate_skills 50 | job_skills 24 | applications 45
--   application_history 45 | interview_rounds 22 | interview_feedback 16
--   offers 9 | placements 6 | activity_logs 10
-- ============================================================================

USE recruitment_db;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE activity_logs;
TRUNCATE TABLE placements;
TRUNCATE TABLE offers;
TRUNCATE TABLE interview_feedback;
TRUNCATE TABLE interview_rounds;
TRUNCATE TABLE application_history;
TRUNCATE TABLE applications;
TRUNCATE TABLE job_skills;
TRUNCATE TABLE jobs;
TRUNCATE TABLE candidate_skills;
TRUNCATE TABLE candidates;
TRUNCATE TABLE companies;
TRUNCATE TABLE users;
TRUNCATE TABLE roles;
TRUNCATE TABLE skills;
TRUNCATE TABLE qualifications;
TRUNCATE TABLE locations;
SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- roles (3)
-- ----------------------------------------------------------------------------
INSERT INTO roles (role_id, role_code, role_name, is_active) VALUES
(1, 'ADMIN', 'Administrator', 1),
(2, 'RECRUITER', 'Recruiter', 1),
(3, 'PLACEMENT_OFFICER', 'Placement Officer', 1);

-- ----------------------------------------------------------------------------
-- users (5)
-- Placeholder hashes only — set real bcrypt hashes before use.
-- Never store plain-text passwords.
-- ----------------------------------------------------------------------------
INSERT INTO users (user_id, username, password_hash, full_name, email, role_id, status) VALUES
(1, 'admin',             '$2a$10$PLACEHOLDER_HASH_DO_NOT_USE_IN_PRODUCTION_ADMIN00000000000001', 'Admin User',      'admin@recruitment.local',      1, 'ACTIVE'),
(2, 'recruiter1',        '$2a$10$PLACEHOLDER_HASH_DO_NOT_USE_IN_PRODUCTION_RECRUITER10000000002', 'Rahul Verma',     'rahul.verma@recruitment.local', 2, 'ACTIVE'),
(3, 'recruiter2',        '$2a$10$PLACEHOLDER_HASH_DO_NOT_USE_IN_PRODUCTION_RECRUITER20000000003', 'Sneha Kulkarni',  'sneha.kulkarni@recruitment.local', 2, 'ACTIVE'),
(4, 'placement_officer', '$2a$10$PLACEHOLDER_HASH_DO_NOT_USE_IN_PRODUCTION_PLACEMENT0000000004', 'Amit Desai',      'amit.desai@recruitment.local', 3, 'ACTIVE'),
(5, 'hr_viewer',         '$2a$10$PLACEHOLDER_HASH_DO_NOT_USE_IN_PRODUCTION_HRVIEWER00000000005', 'Neha Sharma',     'neha.sharma@recruitment.local', 2, 'ACTIVE');

-- ----------------------------------------------------------------------------
-- skills (10)
-- ----------------------------------------------------------------------------
INSERT INTO skills (skill_id, skill_name, category, is_active) VALUES
(1,  'Java',            'Programming', 1),
(2,  'Python',          'Programming', 1),
(3,  'SQL',             'Database',    1),
(4,  'JavaScript',      'Programming', 1),
(5,  'React',           'Frontend',    1),
(6,  'Spring Boot',     'Backend',     1),
(7,  'HTML/CSS',        'Frontend',    1),
(8,  'Data Structures', 'Core CS',     1),
(9,  'Testing',         'QA',          1),
(10, 'Communication',   'Soft Skills', 1);

-- ----------------------------------------------------------------------------
-- qualifications (5)
-- ----------------------------------------------------------------------------
INSERT INTO qualifications (qualification_id, qualification_code, qualification_name, is_active) VALUES
(1, 'BTECH', 'Bachelor of Technology', 1),
(2, 'BE',    'Bachelor of Engineering', 1),
(3, 'MCA',   'Master of Computer Applications', 1),
(4, 'BSC',   'Bachelor of Science', 1),
(5, 'MTECH', 'Master of Technology', 1);

-- ----------------------------------------------------------------------------
-- locations (6)
-- ----------------------------------------------------------------------------
INSERT INTO locations (location_id, location_code, city, state, is_active) VALUES
(1, 'NAG', 'Nagpur',    'Maharashtra', 1),
(2, 'PUN', 'Pune',      'Maharashtra', 1),
(3, 'MUM', 'Mumbai',    'Maharashtra', 1),
(4, 'BLR', 'Bengaluru', 'Karnataka',   1),
(5, 'HYD', 'Hyderabad', 'Telangana',   1),
(6, 'CHE', 'Chennai',   'Tamil Nadu',  1);

-- ----------------------------------------------------------------------------
-- companies (8)
-- ----------------------------------------------------------------------------
INSERT INTO companies (company_id, company_code, company_name, industry, website, location, company_type, hr_name, hr_email, hr_mobile, status) VALUES
(1, 'COM-001', 'TCS',           'IT Services', 'https://www.tcs.com',        'Mumbai',    'MNC', 'Rajesh Kumar',   'hr.recruitment@tcs.com',        '9822012345', 'ACTIVE'),
(2, 'COM-002', 'Infosys',       'IT Services', 'https://www.infosys.com',    'Pune',      'MNC', 'Sunita Patel',   'hr.careers@infosys.com',        '9933012346', 'ACTIVE'),
(3, 'COM-003', 'Wipro',         'IT Services', 'https://www.wipro.com',      'Bengaluru', 'MNC', 'Manoj Iyer',     'hr.talent@wipro.com',           '9744012347', 'ACTIVE'),
(4, 'COM-004', 'Accenture',     'Consulting',  'https://www.accenture.com',  'Mumbai',    'MNC', 'Kavita Menon',   'hr.india@accenture.com',        '9825012348', 'ACTIVE'),
(5, 'COM-005', 'Capgemini',     'IT Services', 'https://www.capgemini.com',  'Pune',      'MNC', 'Arvind Nair',    'hr.hiring@capgemini.com',       '9936012349', 'ACTIVE'),
(6, 'COM-006', 'Cognizant',     'IT Services', 'https://www.cognizant.com',  'Chennai',   'MNC', 'Divya Rao',      'hr.recruit@cognizant.com',      '9747012350', 'ACTIVE'),
(7, 'COM-007', 'Tech Mahindra', 'IT Services', 'https://www.techmahindra.com','Hyderabad','MNC', 'Suresh Reddy',   'hr.campus@techmahindra.com',    '9828012351', 'ACTIVE'),
(8, 'COM-008', 'HCLTech',       'IT Services', 'https://www.hcltech.com',    'Nagpur',    'MNC', 'Pooja Deshmukh', 'hr.nagpur@hcltech.com',         '9939012352', 'ACTIVE');

-- ----------------------------------------------------------------------------
-- candidates (30)
-- Codes CAN-2026-0001..0030. Candidates 1-5 are PLACED (they match the
-- 5 JOINED placements below); all others are NOT_PLACED.
-- Profile status: mostly ACTIVE, candidate 29 INACTIVE, candidate 30 ON_HOLD
-- (both were deactivated / put on hold after their interviews, at their own
-- request, so their INTERVIEWED applications stay valid history).
-- A few percentages are below 60 so eligibility checks stay interesting.
-- ----------------------------------------------------------------------------
INSERT INTO candidates (candidate_id, candidate_code, full_name, email, mobile, date_of_birth, gender, qualification, college, university, passing_year, percentage, cgpa, backlog_count, primary_technology, current_location, preferred_location, profile_status, placement_status) VALUES
(1,  'CAN-2026-0001', 'Aarav Sharma',    'aarav.sharma01@gmail.com',    '9823014567', '2003-05-14', 'Male',   'BTech', 'G H Raisoni College of Engineering, Nagpur',       'RTMNU', 2025, 87.50, 9.20, 0, 'Java',       'Nagpur', 'Pune',      'ACTIVE', 'PLACED'),
(2,  'CAN-2026-0002', 'Ananya Deshmukh', 'ananya.deshmukh02@gmail.com', '9934015678', '2003-08-22', 'Female', 'BTech', 'Yeshwantrao Chavan College of Engineering, Nagpur', 'RTMNU', 2025, 84.20, 8.90, 0, 'Python',     'Nagpur', 'Bengaluru', 'ACTIVE', 'PLACED'),
(3,  'CAN-2026-0003', 'Rohan Patil',     'rohan.patil03@gmail.com',     '9745016789', '2004-01-09', 'Male',   'BE',    'Priyadarshini College of Engineering, Nagpur',       'RTMNU', 2026, 78.60, 8.30, 0, 'React',      'Nagpur', 'Pune',      'ACTIVE', 'PLACED'),
(4,  'CAN-2026-0004', 'Sneha Kulkarni',  'sneha.kulkarni04@gmail.com',  '9826017890', '2002-11-30', 'Female', 'BTech', 'Ramdeobaba College of Engineering and Management, Nagpur', 'RTMNU', 2024, 81.30, 8.60, 0, 'SQL',        'Nagpur', 'Mumbai',    'ACTIVE', 'PLACED'),
(5,  'CAN-2026-0005', 'Vikram Joshi',    'vikram.joshi05@gmail.com',    '9937018901', '2003-03-17', 'Male',   'MCA',   'Visvesvaraya National Institute of Technology, Nagpur', 'RTMNU', 2025, 76.40, 8.00, 0, 'Java',       'Nagpur', 'Mumbai',    'ACTIVE', 'PLACED'),
(6,  'CAN-2026-0006', 'Priya Nair',      'priya.nair06@gmail.com',      '9748019012', '2004-06-25', 'Female', 'BTech', 'G H Raisoni College of Engineering, Nagpur',       'RTMNU', 2026, 58.00, 6.10, 0, 'JavaScript', 'Nagpur', 'Pune',      'ACTIVE', 'NOT_PLACED'),
(7,  'CAN-2026-0007', 'Arjun Mehta',     'arjun.mehta07@gmail.com',     '9829010123', '2003-09-11', 'Male',   'BE',    'G H Raisoni College of Engineering, Nagpur',       'RTMNU', 2025, 57.50, 6.00, 1, 'Python',     'Nagpur', 'Hyderabad', 'ACTIVE', 'NOT_PLACED'),
(8,  'CAN-2026-0008', 'Kavya Reddy',     'kavya.reddy08@gmail.com',     '9940011234', '2004-02-28', 'Female', 'BTech', 'Yeshwantrao Chavan College of Engineering, Nagpur', 'RTMNU', 2026, 59.00, 6.20, 2, 'SQL',        'Nagpur', 'Chennai',   'ACTIVE', 'NOT_PLACED'),
(9,  'CAN-2026-0009', 'Aditya Singh',    'aditya.singh09@gmail.com',    '9751012345', '2002-07-19', 'Male',   'BTech', 'Priyadarshini College of Engineering, Nagpur',       'RTMNU', 2024, 88.90, 9.40, 0, 'Java',       'Nagpur', 'Bengaluru', 'ACTIVE', 'NOT_PLACED'),
(10, 'CAN-2026-0010', 'Ishita Verma',    'ishita.verma10@gmail.com',    '9832013456', '2003-12-05', 'Female', 'MCA',   'Ramdeobaba College of Engineering and Management, Nagpur', 'RTMNU', 2025, 72.10, 7.60, 0, 'React',      'Nagpur', 'Pune',      'ACTIVE', 'NOT_PLACED'),
(11, 'CAN-2026-0011', 'Nikhil Agarwal',  'nikhil.agarwal11@gmail.com',  '9943014567', '2004-04-16', 'Male',   'BTech', 'Visvesvaraya National Institute of Technology, Nagpur', 'RTMNU', 2026, 91.20, 9.60, 0, 'Python',     'Nagpur', 'Bengaluru', 'ACTIVE', 'NOT_PLACED'),
(12, 'CAN-2026-0012', 'Riya Gupta',      'riya.gupta12@gmail.com',      '9754015678', '2002-10-08', 'Female', 'BE',    'GWCET Nagpur',                               'RTMNU', 2024, 69.80, 7.30, 0, 'Java',       'Nagpur', 'Mumbai',    'ACTIVE', 'NOT_PLACED'),
(13, 'CAN-2026-0013', 'Karan Malhotra',  'karan.malhotra13@gmail.com',  '9835016789', '2003-01-27', 'Male',   'BTech', 'G H Raisoni College of Engineering, Nagpur',       'RTMNU', 2025, 83.40, 8.80, 0, 'JavaScript', 'Nagpur', 'Hyderabad', 'ACTIVE', 'NOT_PLACED'),
(14, 'CAN-2026-0014', 'Divya Iyer',      'divya.iyer14@gmail.com',      '9946017890', '2004-08-13', 'Female', 'BTech', 'Yeshwantrao Chavan College of Engineering, Nagpur', 'RTMNU', 2026, 77.70, 8.20, 0, 'SQL',        'Nagpur', 'Pune',      'ACTIVE', 'NOT_PLACED'),
(15, 'CAN-2026-0015', 'Sahil Khan',      'sahil.khan15@gmail.com',      '9757018901', '2003-06-02', 'Male',   'MCA',   'Priyadarshini College of Engineering, Nagpur',       'RTMNU', 2025, 65.20, 6.90, 1, 'Java',       'Nagpur', 'Mumbai',    'ACTIVE', 'NOT_PLACED'),
(16, 'CAN-2026-0016', 'Tanvi Bhatt',     'tanvi.bhatt16@gmail.com',     '9838019012', '2004-09-21', 'Female', 'BTech', 'Ramdeobaba College of Engineering and Management, Nagpur', 'RTMNU', 2026, 90.50, 9.50, 0, 'React',      'Nagpur', 'Bengaluru', 'ACTIVE', 'NOT_PLACED'),
(17, 'CAN-2026-0017', 'Manav Choudhary', 'manav.choudhary17@gmail.com', '9949010123', '2002-12-18', 'Male',   'BE',    'Visvesvaraya National Institute of Technology, Nagpur', 'RTMNU', 2024, 56.50, 5.90, 1, 'Python',     'Nagpur', 'Pune',      'ACTIVE', 'NOT_PLACED'),
(18, 'CAN-2026-0018', 'Neha Pandey',     'neha.pandey18@gmail.com',     '9760011234', '2003-04-07', 'Female', 'BTech', 'GWCET Nagpur',                               'RTMNU', 2025, 74.60, 7.80, 0, 'Java',       'Nagpur', 'Mumbai',    'ACTIVE', 'NOT_PLACED'),
(19, 'CAN-2026-0019', 'Varun Tiwari',    'varun.tiwari19@gmail.com',    '9841012345', '2004-03-29', 'Male',   'BTech', 'G H Raisoni College of Engineering, Nagpur',       'RTMNU', 2026, 68.90, 7.20, 0, 'JavaScript', 'Nagpur', 'Chennai',   'ACTIVE', 'NOT_PLACED'),
(20, 'CAN-2026-0020', 'Anika Rao',       'anika.rao20@gmail.com',       '9952013456', '2002-05-11', 'Female', 'BTech', 'Yeshwantrao Chavan College of Engineering, Nagpur', 'RTMNU', 2024, 85.75, 9.00, 0, 'SQL',        'Nagpur', 'Hyderabad', 'ACTIVE', 'NOT_PLACED'),
(21, 'CAN-2026-0021', 'Dev Patel',       'dev.patel21@gmail.com',       '9763014567', '2003-10-23', 'Male',   'MCA',   'Priyadarshini College of Engineering, Nagpur',       'RTMNU', 2025, 79.30, 8.30, 0, 'Java',       'Nagpur', 'Pune',      'ACTIVE', 'NOT_PLACED'),
(22, 'CAN-2026-0022', 'Sara Sheikh',     'sara.sheikh22@gmail.com',     '9844015678', '2004-07-04', 'Female', 'BE',    'Ramdeobaba College of Engineering and Management, Nagpur', 'RTMNU', 2026, 59.25, 6.20, 0, 'React',      'Nagpur', 'Mumbai',    'ACTIVE', 'NOT_PLACED'),
(23, 'CAN-2026-0023', 'Yash Kapoor',     'yash.kapoor23@gmail.com',     '9955016789', '2002-08-15', 'Male',   'BTech', 'Visvesvaraya National Institute of Technology, Nagpur', 'RTMNU', 2024, 92.50, 9.70, 0, 'Python',     'Nagpur', 'Bengaluru', 'ACTIVE', 'NOT_PLACED'),
(24, 'CAN-2026-0024', 'Meera Nambiar',   'meera.nambiar24@gmail.com',   '9766017890', '2003-02-20', 'Female', 'BTech', 'GWCET Nagpur',                               'RTMNU', 2025, 71.80, 7.50, 0, 'Java',       'Nagpur', 'Pune',      'ACTIVE', 'NOT_PLACED'),
(25, 'CAN-2026-0025', 'Rahul Saxena',    'rahul.saxena25@gmail.com',    '9847018901', '2004-11-06', 'Male',   'BE',    'G H Raisoni College of Engineering, Nagpur',       'RTMNU', 2026, 66.40, 7.00, 1, 'JavaScript', 'Nagpur', 'Hyderabad', 'ACTIVE', 'NOT_PLACED'),
(26, 'CAN-2026-0026', 'Pooja Menon',     'pooja.menon26@gmail.com',     '9958019012', '2002-09-14', 'Female', 'BTech', 'Yeshwantrao Chavan College of Engineering, Nagpur', 'RTMNU', 2024, 80.10, 8.40, 0, 'SQL',        'Nagpur', 'Chennai',   'ACTIVE', 'NOT_PLACED'),
(27, 'CAN-2026-0027', 'Aman Qureshi',    'aman.qureshi27@gmail.com',    '9769010123', '2003-07-26', 'Male',   'MCA',   'Priyadarshini College of Engineering, Nagpur',       'RTMNU', 2025, 63.70, 6.70, 0, 'Java',       'Nagpur', 'Mumbai',    'ACTIVE', 'NOT_PLACED'),
(28, 'CAN-2026-0028', 'Nisha Yadav',     'nisha.yadav28@gmail.com',     '9850011234', '2004-05-30', 'Female', 'BTech', 'Ramdeobaba College of Engineering and Management, Nagpur', 'RTMNU', 2026, 86.20, 9.10, 0, 'React',      'Nagpur', 'Bengaluru', 'ACTIVE', 'NOT_PLACED'),
(29, 'CAN-2026-0029', 'Kabir Anand',     'kabir.anand29@gmail.com',     '9961012345', '2002-06-09', 'Male',   'BE',    'Visvesvaraya National Institute of Technology, Nagpur', 'RTMNU', 2024, 73.50, 7.70, 0, 'Python',     'Nagpur', 'Pune',      'INACTIVE', 'NOT_PLACED'),
(30, 'CAN-2026-0030', 'Zara Hussain',    'zara.hussain30@gmail.com',    '9772013456', '2003-11-12', 'Female', 'BTech', 'GWCET Nagpur',                               'RTMNU', 2025, 67.90, 7.10, 0, 'Java',       'Nagpur', 'Mumbai',    'ON_HOLD',  'NOT_PLACED');

-- ----------------------------------------------------------------------------
-- jobs (12)
-- Codes JOB-2026-001..012. Mostly OPEN; job 11 CLOSED, job 12 DRAFT.
-- Employment type FULL_TIME except jobs 8 and 9 (INTERNSHIP).
-- ----------------------------------------------------------------------------
INSERT INTO jobs (job_id, job_code, company_id, job_title, employment_type, location, openings, min_package_lpa, max_package_lpa, min_percentage, min_passing_year, max_passing_year, max_backlogs, experience_min_years, qualification_rule, posted_date, application_deadline, status, created_by) VALUES
(1,  'JOB-2026-001', 1, 'Java Developer',            'FULL_TIME',  'Mumbai',    5,  4.50,  6.00, 60.00, 2024, 2026, 0, 0.0, 'BTech/BE/MCA', '2026-01-05', '2026-03-31', 'OPEN',   2),
(2,  'JOB-2026-002', 2, 'Python Developer',          'FULL_TIME',  'Pune',      4,  4.00,  5.50, 60.00, 2024, 2026, 1, 0.0, 'BTech/BE/MCA', '2026-01-08', '2026-04-15', 'OPEN',   2),
(3,  'JOB-2026-003', 3, 'Frontend Developer (React)','FULL_TIME',  'Bengaluru', 6,  3.50,  5.00, 55.00, 2025, 2026, 1, 0.0, 'BTech/BE',     '2026-01-12', '2026-04-30', 'OPEN',   3),
(4,  'JOB-2026-004', 4, 'SQL / Data Analyst',        'FULL_TIME',  'Mumbai',    3,  5.00,  7.00, 65.00, 2024, 2026, 0, 0.0, 'BTech/BE/MCA', '2026-01-15', '2026-03-31', 'OPEN',   2),
(5,  'JOB-2026-005', 5, 'Software Engineer Trainee', 'FULL_TIME',  'Pune',      10, 3.50,  4.50, 60.00, 2025, 2026, 0, 0.0, 'BTech/BE',     '2026-01-20', '2026-05-15', 'OPEN',   3),
(6,  'JOB-2026-006', 6, 'QA Engineer',               'FULL_TIME',  'Chennai',   4,  3.00,  4.50, 55.00, 2024, 2026, 1, 0.0, 'BTech/BE/MCA', '2026-01-25', '2026-04-30', 'OPEN',   2),
(7,  'JOB-2026-007', 7, 'Full Stack Developer',      'FULL_TIME',  'Hyderabad', 3,  5.50,  8.00, 65.00, 2024, 2025, 0, 1.0, 'BTech/BE',     '2026-02-01', '2026-05-31', 'OPEN',   3),
(8,  'JOB-2026-008', 8, 'Support Engineer',          'INTERNSHIP', 'Nagpur',    8,  3.00,  4.00, 55.00, 2024, 2026, 1, 0.0, 'BTech/BE/MCA', '2026-02-03', '2026-04-15', 'OPEN',   2),
(9,  'JOB-2026-009', 1, 'DevOps Trainee',            'INTERNSHIP', 'Pune',      2,  4.00,  5.00, 60.00, 2025, 2026, 0, 0.0, 'BTech/BE',     '2026-02-05', '2026-05-31', 'OPEN',   3),
(10, 'JOB-2026-010', 2, 'Business Analyst',          'FULL_TIME',  'Mumbai',    2,  6.00,  9.50, 65.00, 2024, 2026, 0, 1.0, 'BTech/BE/MCA', '2026-02-10', '2026-05-15', 'OPEN',   2),
(11, 'JOB-2026-011', 3, 'Java Backend Developer',    'FULL_TIME',  'Bengaluru', 5,  5.00,  7.50, 60.00, 2024, 2026, 1, 1.0, 'BTech/BE/MCA', '2026-01-18', '2026-04-30', 'CLOSED', 3),
(12, 'JOB-2026-012', 4, 'React Developer',           'FULL_TIME',  'Pune',      4,  5.00,  7.00, 60.00, 2025, 2026, 0, 0.0, 'BTech/BE',     '2026-02-08', '2026-05-31', 'DRAFT',  2);

-- ----------------------------------------------------------------------------
-- candidate_skills (50)
-- Every candidate has a primary skill (is_primary = 1); candidates 1-20
-- also have one secondary skill. Skill IDs: 1 Java, 2 Python, 3 SQL,
-- 4 JavaScript, 5 React, 6 Spring Boot, 7 HTML/CSS, 8 Data Structures,
-- 9 Testing, 10 Communication.
-- ----------------------------------------------------------------------------
INSERT INTO candidate_skills (candidate_skill_id, candidate_id, skill_id, proficiency_level, years_experience, is_primary) VALUES
(1,  1,  1, 'Advanced',     2.0, 1),
(2,  2,  2, 'Advanced',     1.5, 1),
(3,  3,  5, 'Intermediate', 1.0, 1),
(4,  4,  3, 'Advanced',     2.0, 1),
(5,  5,  1, 'Intermediate', 1.0, 1),
(6,  6,  4, 'Intermediate', 1.0, 1),
(7,  7,  2, 'Intermediate', 1.0, 1),
(8,  8,  3, 'Intermediate', 1.0, 1),
(9,  9,  1, 'Advanced',     2.0, 1),
(10, 10, 5, 'Intermediate', 1.0, 1),
(11, 11, 2, 'Advanced',     2.0, 1),
(12, 12, 1, 'Intermediate', 1.0, 1),
(13, 13, 4, 'Advanced',     1.5, 1),
(14, 14, 3, 'Intermediate', 1.0, 1),
(15, 15, 1, 'Intermediate', 0.5, 1),
(16, 16, 5, 'Advanced',     1.5, 1),
(17, 17, 2, 'Beginner',     0.5, 1),
(18, 18, 1, 'Intermediate', 1.0, 1),
(19, 19, 4, 'Intermediate', 1.0, 1),
(20, 20, 3, 'Advanced',     2.0, 1),
(21, 21, 1, 'Intermediate', 1.0, 1),
(22, 22, 5, 'Beginner',     0.5, 1),
(23, 23, 2, 'Advanced',     2.0, 1),
(24, 24, 1, 'Intermediate', 1.0, 1),
(25, 25, 4, 'Intermediate', 0.5, 1),
(26, 26, 3, 'Intermediate', 1.0, 1),
(27, 27, 1, 'Beginner',     0.5, 1),
(28, 28, 5, 'Advanced',     1.5, 1),
(29, 29, 2, 'Intermediate', 1.0, 1),
(30, 30, 1, 'Intermediate', 1.0, 1),
(31, 1,  6, 'Intermediate', 1.0, 0),
(32, 2,  3, 'Intermediate', 1.0, 0),
(33, 3,  7, 'Intermediate', 1.0, 0),
(34, 4,  10, 'Advanced',    2.0, 0),
(35, 5,  8, 'Intermediate', 1.0, 0),
(36, 6,  5, 'Beginner',     0.5, 0),
(37, 7,  8, 'Intermediate', 1.0, 0),
(38, 8,  2, 'Beginner',     0.5, 0),
(39, 9,  6, 'Advanced',     1.5, 0),
(40, 10, 4, 'Intermediate', 1.0, 0),
(41, 11, 3, 'Intermediate', 1.0, 0),
(42, 12, 10, 'Intermediate', 1.0, 0),
(43, 13, 7, 'Intermediate', 1.0, 0),
(44, 14, 2, 'Beginner',     0.5, 0),
(45, 15, 8, 'Beginner',     0.5, 0),
(46, 16, 4, 'Intermediate', 1.0, 0),
(47, 17, 3, 'Beginner',     0.5, 0),
(48, 18, 6, 'Intermediate', 1.0, 0),
(49, 19, 5, 'Intermediate', 1.0, 0),
(50, 20, 10, 'Intermediate', 1.0, 0);

-- ----------------------------------------------------------------------------
-- job_skills (24) — two required skills per job
-- ----------------------------------------------------------------------------
INSERT INTO job_skills (job_skill_id, job_id, skill_id, minimum_level, mandatory_flag) VALUES
(1,  1,  1, 'Advanced',     1),
(2,  1,  6, 'Intermediate', 1),
(3,  2,  2, 'Advanced',     1),
(4,  2,  3, 'Intermediate', 1),
(5,  3,  5, 'Advanced',     1),
(6,  3,  4, 'Intermediate', 1),
(7,  4,  3, 'Advanced',     1),
(8,  4,  2, 'Intermediate', 0),
(9,  5,  1, 'Intermediate', 1),
(10, 5,  8, 'Intermediate', 1),
(11, 6,  9, 'Intermediate', 1),
(12, 6,  3, 'Beginner',     0),
(13, 7,  4, 'Advanced',     1),
(14, 7,  5, 'Advanced',     1),
(15, 8,  10, 'Intermediate', 1),
(16, 8,  3, 'Beginner',     0),
(17, 9,  2, 'Intermediate', 1),
(18, 9,  3, 'Intermediate', 0),
(19, 10, 3, 'Advanced',     1),
(20, 10, 10, 'Advanced',    1),
(21, 11, 1, 'Advanced',     1),
(22, 11, 6, 'Advanced',     1),
(23, 12, 5, 'Advanced',     1),
(24, 12, 7, 'Intermediate', 1);

-- ----------------------------------------------------------------------------
-- applications (45)
-- Codes APP-2026-0001..0045. Every (candidate_id, job_id) pair is unique.
-- Status counts: APPLIED 8, ELIGIBILITY_VERIFIED 5, SHORTLISTED 5,
-- INTERVIEW_SCHEDULED 4, INTERVIEWED 5, SELECTED 2, REJECTED 6,
-- OFFERED 2, JOINED 5, WITHDRAWN 3. All 10 statuses appear.
-- Eligibility: PENDING for the 8 fresh applications, ELIGIBLE for most,
-- NOT_ELIGIBLE for 3 (apps 30-32) with the reason recorded.
-- ----------------------------------------------------------------------------
INSERT INTO applications (application_id, application_code, candidate_id, job_id, applied_at, eligibility_status, eligibility_reason, application_status, source, assigned_recruiter_id) VALUES
(1,  'APP-2026-0001', 6,  1,  '2026-02-02 10:15:00', 'PENDING',  NULL, 'APPLIED',               'Website',       2),
(2,  'APP-2026-0002', 7,  2,  '2026-02-02 11:20:00', 'PENDING',  NULL, 'APPLIED',               'College Drive', 3),
(3,  'APP-2026-0003', 8,  3,  '2026-02-03 09:45:00', 'PENDING',  NULL, 'APPLIED',               'Website',       2),
(4,  'APP-2026-0004', 9,  4,  '2026-02-03 14:05:00', 'PENDING',  NULL, 'APPLIED',               'Referral',      3),
(5,  'APP-2026-0005', 10, 5,  '2026-02-04 10:30:00', 'PENDING',  NULL, 'APPLIED',               'College Drive', 2),
(6,  'APP-2026-0006', 11, 6,  '2026-02-04 15:40:00', 'PENDING',  NULL, 'APPLIED',               'Website',       3),
(7,  'APP-2026-0007', 12, 7,  '2026-02-05 09:10:00', 'PENDING',  NULL, 'APPLIED',               'Walk-in',       2),
(8,  'APP-2026-0008', 13, 8,  '2026-02-05 12:25:00', 'PENDING',  NULL, 'APPLIED',               'Website',       3),
(9,  'APP-2026-0009', 14, 1,  '2026-02-06 10:00:00', 'ELIGIBLE', NULL, 'ELIGIBILITY_VERIFIED',  'College Drive', 2),
(10, 'APP-2026-0010', 15, 2,  '2026-02-06 11:35:00', 'ELIGIBLE', NULL, 'ELIGIBILITY_VERIFIED',  'Website',       3),
(11, 'APP-2026-0011', 16, 3,  '2026-02-07 09:50:00', 'ELIGIBLE', NULL, 'ELIGIBILITY_VERIFIED',  'College Drive', 2),
(12, 'APP-2026-0012', 17, 6,  '2026-02-07 13:15:00', 'ELIGIBLE', NULL, 'ELIGIBILITY_VERIFIED',  'Referral',      3),
(13, 'APP-2026-0013', 18, 5,  '2026-02-08 10:20:00', 'ELIGIBLE', NULL, 'ELIGIBILITY_VERIFIED',  'Website',       2),
(14, 'APP-2026-0014', 19, 6,  '2026-02-09 10:05:00', 'ELIGIBLE', NULL, 'SHORTLISTED',           'College Drive', 3),
(15, 'APP-2026-0015', 20, 7,  '2026-02-09 14:30:00', 'ELIGIBLE', NULL, 'SHORTLISTED',           'Website',       2),
(16, 'APP-2026-0016', 21, 8,  '2026-02-10 09:40:00', 'ELIGIBLE', NULL, 'SHORTLISTED',           'Walk-in',       3),
(17, 'APP-2026-0017', 22, 3,  '2026-02-10 11:55:00', 'ELIGIBLE', NULL, 'SHORTLISTED',           'College Drive', 2),
(18, 'APP-2026-0018', 23, 10, '2026-02-11 10:10:00', 'ELIGIBLE', NULL, 'SHORTLISTED',           'Referral',      3),
(19, 'APP-2026-0019', 24, 1,  '2026-02-12 10:00:00', 'ELIGIBLE', NULL, 'INTERVIEW_SCHEDULED',   'Website',       2),
(20, 'APP-2026-0020', 25, 2,  '2026-02-12 12:00:00', 'ELIGIBLE', NULL, 'INTERVIEW_SCHEDULED',   'College Drive', 3),
(21, 'APP-2026-0021', 26, 6,  '2026-02-13 09:30:00', 'ELIGIBLE', NULL, 'INTERVIEW_SCHEDULED',   'Website',       2),
(22, 'APP-2026-0022', 27, 2,  '2026-02-13 15:00:00', 'ELIGIBLE', NULL, 'INTERVIEW_SCHEDULED',   'Referral',      3),
(23, 'APP-2026-0023', 28, 5,  '2026-02-14 10:15:00', 'ELIGIBLE', NULL, 'INTERVIEWED',           'College Drive', 2),
(24, 'APP-2026-0024', 29, 6,  '2026-02-14 11:45:00', 'ELIGIBLE', NULL, 'INTERVIEWED',           'Website',       3),
(25, 'APP-2026-0025', 30, 7,  '2026-02-15 09:20:00', 'ELIGIBLE', NULL, 'INTERVIEWED',           'College Drive', 2),
(26, 'APP-2026-0026', 1,  8,  '2026-02-15 13:40:00', 'ELIGIBLE', NULL, 'INTERVIEWED',           'Website',       3),
(27, 'APP-2026-0027', 2,  9,  '2026-02-16 10:05:00', 'ELIGIBLE', NULL, 'INTERVIEWED',           'Referral',      2),
(28, 'APP-2026-0028', 3,  10, '2026-02-16 11:30:00', 'ELIGIBLE', NULL, 'SELECTED',              'College Drive', 3),
(29, 'APP-2026-0029', 4,  1,  '2026-02-17 09:55:00', 'ELIGIBLE', NULL, 'SELECTED',              'Website',       2),
(30, 'APP-2026-0030', 6,  4,  '2026-02-17 14:20:00', 'NOT_ELIGIBLE', 'Percentage 58.00 below required 65.00', 'REJECTED', 'Website', 3),
(31, 'APP-2026-0031', 7,  5,  '2026-02-18 10:10:00', 'NOT_ELIGIBLE', 'Percentage 57.50 below required 60.00', 'REJECTED', 'College Drive', 2),
(32, 'APP-2026-0032', 8,  6,  '2026-02-18 12:35:00', 'NOT_ELIGIBLE', 'Backlog count 2 exceeds allowed maximum 1', 'REJECTED', 'Website', 3),
(33, 'APP-2026-0033', 9,  7,  '2026-02-19 10:00:00', 'ELIGIBLE', NULL, 'REJECTED',              'Referral',      2),
(34, 'APP-2026-0034', 10, 8,  '2026-02-19 11:25:00', 'ELIGIBLE', NULL, 'REJECTED',              'College Drive', 3),
(35, 'APP-2026-0035', 11, 9,  '2026-02-20 09:45:00', 'ELIGIBLE', NULL, 'REJECTED',              'Website',       2),
(36, 'APP-2026-0036', 12, 10, '2026-02-20 14:10:00', 'ELIGIBLE', NULL, 'OFFERED',               'College Drive', 3),
(37, 'APP-2026-0037', 13, 1,  '2026-02-21 10:30:00', 'ELIGIBLE', NULL, 'OFFERED',               'Website',       2),
(38, 'APP-2026-0038', 1,  2,  '2026-02-21 12:00:00', 'ELIGIBLE', NULL, 'JOINED',                'College Drive', 3),
(39, 'APP-2026-0039', 2,  3,  '2026-02-22 09:15:00', 'ELIGIBLE', NULL, 'JOINED',                'Website',       2),
(40, 'APP-2026-0040', 3,  5,  '2026-02-22 11:40:00', 'ELIGIBLE', NULL, 'JOINED',                'College Drive', 3),
(41, 'APP-2026-0041', 4,  7,  '2026-02-23 10:20:00', 'ELIGIBLE', NULL, 'JOINED',                'Referral',      2),
(42, 'APP-2026-0042', 5,  10, '2026-02-23 13:55:00', 'ELIGIBLE', NULL, 'JOINED',                'Website',       3),
(43, 'APP-2026-0043', 14, 3,  '2026-02-24 10:05:00', 'ELIGIBLE', NULL, 'WITHDRAWN',             'College Drive', 2),
(44, 'APP-2026-0044', 15, 6,  '2026-02-24 11:30:00', 'ELIGIBLE', NULL, 'WITHDRAWN',             'Website',       3),
(45, 'APP-2026-0045', 16, 5,  '2026-02-25 09:50:00', 'ELIGIBLE', NULL, 'WITHDRAWN',             'College Drive', 2);

-- ----------------------------------------------------------------------------
-- application_history (45) — one row per application.
-- new_status = the application's current status; old_status = the previous
-- stage (NULL for a brand-new application).
-- ----------------------------------------------------------------------------
INSERT INTO application_history (history_id, application_id, old_status, new_status, remarks, changed_by, changed_at) VALUES
(1,  1,  NULL, 'APPLIED',               'Application received', 2, '2026-02-02 10:15:00'),
(2,  2,  NULL, 'APPLIED',               'Application received', 3, '2026-02-02 11:20:00'),
(3,  3,  NULL, 'APPLIED',               'Application received', 2, '2026-02-03 09:45:00'),
(4,  4,  NULL, 'APPLIED',               'Application received', 3, '2026-02-03 14:05:00'),
(5,  5,  NULL, 'APPLIED',               'Application received', 2, '2026-02-04 10:30:00'),
(6,  6,  NULL, 'APPLIED',               'Application received', 3, '2026-02-04 15:40:00'),
(7,  7,  NULL, 'APPLIED',               'Application received', 2, '2026-02-05 09:10:00'),
(8,  8,  NULL, 'APPLIED',               'Application received', 3, '2026-02-05 12:25:00'),
(9,  9,  'APPLIED', 'ELIGIBILITY_VERIFIED', 'Eligibility criteria met', 2, '2026-02-10 10:00:00'),
(10, 10, 'APPLIED', 'ELIGIBILITY_VERIFIED', 'Eligibility criteria met', 3, '2026-02-10 11:00:00'),
(11, 11, 'APPLIED', 'ELIGIBILITY_VERIFIED', 'Eligibility criteria met', 2, '2026-02-11 09:00:00'),
(12, 12, 'APPLIED', 'ELIGIBILITY_VERIFIED', 'Eligibility criteria met', 3, '2026-02-11 10:30:00'),
(13, 13, 'APPLIED', 'ELIGIBILITY_VERIFIED', 'Eligibility criteria met', 2, '2026-02-12 09:00:00'),
(14, 14, 'ELIGIBILITY_VERIFIED', 'SHORTLISTED', 'Shortlisted for interview', 3, '2026-02-15 10:00:00'),
(15, 15, 'ELIGIBILITY_VERIFIED', 'SHORTLISTED', 'Shortlisted for interview', 2, '2026-02-15 11:00:00'),
(16, 16, 'ELIGIBILITY_VERIFIED', 'SHORTLISTED', 'Shortlisted for interview', 3, '2026-02-16 09:00:00'),
(17, 17, 'ELIGIBILITY_VERIFIED', 'SHORTLISTED', 'Shortlisted for interview', 2, '2026-02-16 10:00:00'),
(18, 18, 'ELIGIBILITY_VERIFIED', 'SHORTLISTED', 'Shortlisted for interview', 3, '2026-02-17 09:00:00'),
(19, 19, 'SHORTLISTED', 'INTERVIEW_SCHEDULED', 'Round 1 scheduled', 2, '2026-03-01 10:00:00'),
(20, 20, 'SHORTLISTED', 'INTERVIEW_SCHEDULED', 'Round 1 scheduled', 3, '2026-03-01 11:00:00'),
(21, 21, 'SHORTLISTED', 'INTERVIEW_SCHEDULED', 'Round 1 scheduled', 2, '2026-03-02 09:00:00'),
(22, 22, 'SHORTLISTED', 'INTERVIEW_SCHEDULED', 'Round 1 scheduled', 3, '2026-03-02 10:00:00'),
(23, 23, 'INTERVIEW_SCHEDULED', 'INTERVIEWED', 'All rounds completed', 2, '2026-03-15 12:00:00'),
(24, 24, 'INTERVIEW_SCHEDULED', 'INTERVIEWED', 'All rounds completed', 3, '2026-03-16 12:00:00'),
(25, 25, 'INTERVIEW_SCHEDULED', 'INTERVIEWED', 'Round 1 completed', 2, '2026-03-13 12:00:00'),
(26, 26, 'INTERVIEW_SCHEDULED', 'INTERVIEWED', 'Round 1 completed', 3, '2026-03-12 12:00:00'),
(27, 27, 'INTERVIEW_SCHEDULED', 'INTERVIEWED', 'Round 1 completed', 2, '2026-03-14 12:00:00'),
(28, 28, 'INTERVIEWED', 'SELECTED', 'Selected after technical round', 3, '2026-03-18 10:00:00'),
(29, 29, 'INTERVIEWED', 'SELECTED', 'Selected after technical round', 2, '2026-03-19 10:00:00'),
(30, 30, 'ELIGIBILITY_VERIFIED', 'REJECTED', 'Percentage 58.00 below required 65.00', 3, '2026-02-20 10:00:00'),
(31, 31, 'ELIGIBILITY_VERIFIED', 'REJECTED', 'Percentage 57.50 below required 60.00', 2, '2026-02-20 11:00:00'),
(32, 32, 'ELIGIBILITY_VERIFIED', 'REJECTED', 'Backlog count 2 exceeds allowed maximum 1', 3, '2026-02-21 09:00:00'),
(33, 33, 'SHORTLISTED', 'REJECTED', 'Not shortlisted after screening', 2, '2026-02-22 10:00:00'),
(34, 34, 'SHORTLISTED', 'REJECTED', 'Not shortlisted after screening', 3, '2026-02-22 11:00:00'),
(35, 35, 'SHORTLISTED', 'REJECTED', 'Not shortlisted after screening', 2, '2026-02-23 09:00:00'),
(36, 36, 'SELECTED', 'OFFERED', 'Offer released', 3, '2026-03-24 10:00:00'),
(37, 37, 'SELECTED', 'OFFERED', 'Offer released', 2, '2026-03-24 11:00:00'),
(38, 38, 'OFFERED', 'JOINED', 'Candidate joined Infosys', 3, '2026-04-10 10:00:00'),
(39, 39, 'OFFERED', 'JOINED', 'Candidate joined Wipro', 2, '2026-04-10 10:00:00'),
(40, 40, 'OFFERED', 'JOINED', 'Candidate joined Capgemini', 3, '2026-04-15 10:00:00'),
(41, 41, 'OFFERED', 'JOINED', 'Candidate joined Tech Mahindra', 2, '2026-04-15 10:00:00'),
(42, 42, 'OFFERED', 'JOINED', 'Candidate joined Infosys', 3, '2026-04-20 10:00:00'),
(43, 43, 'APPLIED', 'WITHDRAWN', 'Candidate withdrew application', 2, '2026-03-05 10:00:00'),
(44, 44, 'APPLIED', 'WITHDRAWN', 'Candidate withdrew application', 3, '2026-03-06 10:00:00'),
(45, 45, 'APPLIED', 'WITHDRAWN', 'Candidate withdrew application', 2, '2026-03-07 10:00:00');

-- ----------------------------------------------------------------------------
-- interview_rounds (22)
-- (application_id, round_number) is unique. 16 COMPLETED, 6 SCHEDULED.
-- Apps 19-22: round 1 still scheduled. Apps 23-27: round 1 done, round 2
-- done for 23-24 and scheduled for 25-26. Apps 28-29 and 36-42: round 1 done.
-- ----------------------------------------------------------------------------
INSERT INTO interview_rounds (interview_id, application_id, round_number, round_type, scheduled_date, scheduled_time, mode, interviewer_name, meeting_or_location, status) VALUES
(1,  19, 1, 'APTITUDE',  '2026-04-06', '10:00:00', 'ONLINE',  'Amit Desai',   'Google Meet link shared by email', 'SCHEDULED'),
(2,  20, 1, 'APTITUDE',  '2026-04-06', '11:00:00', 'ONLINE',  'Neha Sharma',  'Google Meet link shared by email', 'SCHEDULED'),
(3,  21, 1, 'TECHNICAL', '2026-04-07', '10:30:00', 'OFFLINE', 'Rahul Verma',  'TCS Office, Mumbai',             'SCHEDULED'),
(4,  22, 1, 'APTITUDE',  '2026-04-07', '14:00:00', 'ONLINE',  'Pooja Singh',  'Google Meet link shared by email', 'SCHEDULED'),
(5,  23, 1, 'APTITUDE',  '2026-03-10', '10:00:00', 'ONLINE',  'Amit Desai',   'Google Meet link shared by email', 'COMPLETED'),
(6,  23, 2, 'TECHNICAL', '2026-03-14', '11:00:00', 'ONLINE',  'Sanjay Patel', 'Google Meet link shared by email', 'COMPLETED'),
(7,  24, 1, 'APTITUDE',  '2026-03-10', '10:00:00', 'ONLINE',  'Neha Sharma',  'Google Meet link shared by email', 'COMPLETED'),
(8,  24, 2, 'TECHNICAL', '2026-03-15', '10:30:00', 'OFFLINE', 'Rahul Verma',  'Wipro Office, Bengaluru',        'COMPLETED'),
(9,  25, 1, 'TECHNICAL', '2026-03-12', '09:30:00', 'ONLINE',  'Vikram Rao',   'Google Meet link shared by email', 'COMPLETED'),
(10, 25, 2, 'MANAGERIAL','2026-04-08', '15:00:00', 'ONLINE',  'Anita Nair',   'Google Meet link shared by email', 'SCHEDULED'),
(11, 26, 1, 'APTITUDE',  '2026-03-11', '10:00:00', 'ONLINE',  'Amit Desai',   'Google Meet link shared by email', 'COMPLETED'),
(12, 26, 2, 'TECHNICAL', '2026-04-09', '11:00:00', 'ONLINE',  'Sanjay Patel', 'Google Meet link shared by email', 'SCHEDULED'),
(13, 27, 1, 'TECHNICAL', '2026-03-13', '10:00:00', 'OFFLINE', 'Rahul Verma',  'TCS Office, Mumbai',             'COMPLETED'),
(14, 28, 1, 'TECHNICAL', '2026-03-16', '10:00:00', 'ONLINE',  'Sanjay Patel', 'Google Meet link shared by email', 'COMPLETED'),
(15, 29, 1, 'TECHNICAL', '2026-03-17', '11:00:00', 'ONLINE',  'Vikram Rao',   'Google Meet link shared by email', 'COMPLETED'),
(16, 36, 1, 'TECHNICAL', '2026-03-20', '10:00:00', 'ONLINE',  'Sanjay Patel', 'Google Meet link shared by email', 'COMPLETED'),
(17, 37, 1, 'TECHNICAL', '2026-03-20', '11:30:00', 'ONLINE',  'Vikram Rao',   'Google Meet link shared by email', 'COMPLETED'),
(18, 38, 1, 'TECHNICAL', '2026-03-22', '10:00:00', 'OFFLINE', 'Rahul Verma',  'Infosys Office, Pune',           'COMPLETED'),
(19, 39, 1, 'TECHNICAL', '2026-03-23', '10:00:00', 'ONLINE',  'Sanjay Patel', 'Google Meet link shared by email', 'COMPLETED'),
(20, 40, 1, 'TECHNICAL', '2026-03-24', '11:00:00', 'ONLINE',  'Vikram Rao',   'Google Meet link shared by email', 'COMPLETED'),
(21, 41, 1, 'HR',        '2026-03-25', '12:00:00', 'ONLINE',  'Kavita Joshi', 'Google Meet link shared by email', 'COMPLETED'),
(22, 42, 1, 'TECHNICAL', '2026-03-26', '10:00:00', 'ONLINE',  'Sanjay Patel', 'Google Meet link shared by email', 'COMPLETED');

-- ----------------------------------------------------------------------------
-- interview_feedback (16) — exactly one row per COMPLETED interview.
-- Ratings are on a 0-5 scale. Recommendations cover NEXT_ROUND, SELECTED,
-- REJECTED and ON_HOLD.
-- ----------------------------------------------------------------------------
INSERT INTO interview_feedback (feedback_id, interview_id, technical_rating, communication_rating, problem_solving_rating, overall_rating, recommendation, remarks, submitted_by, submitted_at) VALUES
(1,  5,  4.0, 4.5, 3.5, 4.0, 'NEXT_ROUND', 'Good aptitude, clear for technical round', 2, '2026-03-10 12:00:00'),
(2,  6,  4.5, 4.0, 4.5, 4.5, 'NEXT_ROUND', 'Strong Java basics, recommend next round', 2, '2026-03-14 13:00:00'),
(3,  7,  3.5, 4.0, 3.0, 3.5, 'NEXT_ROUND', 'Average aptitude, can proceed', 3, '2026-03-10 12:00:00'),
(4,  8,  4.0, 3.5, 4.0, 4.0, 'ON_HOLD',   'Decent technical, hold for comparison with other candidates', 3, '2026-03-15 12:30:00'),
(5,  9,  2.5, 3.0, 2.5, 2.5, 'REJECTED',  'Weak in core concepts, not recommended', 2, '2026-03-12 11:30:00'),
(6,  11, 4.0, 4.0, 4.0, 4.0, 'NEXT_ROUND', 'Good aptitude score, proceed to technical', 3, '2026-03-11 12:00:00'),
(7,  13, 3.0, 3.5, 3.0, 3.0, 'NEXT_ROUND', 'Basic concepts okay, needs deeper technical check', 2, '2026-03-13 12:00:00'),
(8,  14, 4.5, 4.5, 4.0, 4.5, 'SELECTED',  'Excellent problem solving and communication', 3, '2026-03-16 12:00:00'),
(9,  15, 4.0, 4.5, 4.5, 4.5, 'SELECTED',  'Strong SQL and clear explanations', 2, '2026-03-17 13:00:00'),
(10, 16, 4.5, 4.0, 4.5, 4.5, 'SELECTED',  'Confident, good analytical thinking', 3, '2026-03-20 12:00:00'),
(11, 17, 4.0, 4.0, 4.0, 4.0, 'SELECTED',  'Solid Java knowledge, suitable for role', 2, '2026-03-20 13:30:00'),
(12, 18, 4.5, 4.5, 4.5, 4.5, 'SELECTED',  'Very good Python and SQL, ready to join', 3, '2026-03-22 12:00:00'),
(13, 19, 4.0, 4.0, 4.5, 4.0, 'SELECTED',  'Good React skills, clean code approach', 2, '2026-03-23 12:00:00'),
(14, 20, 5.0, 4.5, 4.5, 5.0, 'SELECTED',  'Outstanding candidate, top of the batch', 3, '2026-03-24 13:00:00'),
(15, 21, 4.5, 4.0, 4.0, 4.0, 'SELECTED',  'Good HR round, positive attitude', 2, '2026-03-25 14:00:00'),
(16, 22, 4.0, 4.5, 4.0, 4.0, 'SELECTED',  'Good business understanding, recommended', 3, '2026-03-26 12:00:00');

-- ----------------------------------------------------------------------------
-- offers (9)
-- Codes OFF-2026-001..009. One per SELECTED / OFFERED / JOINED application.
-- 6 ACCEPTED (offers 3, 5, 6, 7, 8, 9) and 3 OFFERED (offers 1, 2, 4).
-- Designations and packages match the job each application is for.
-- ----------------------------------------------------------------------------
INSERT INTO offers (offer_id, offer_code, application_id, designation, offered_package_lpa, offer_date, expected_joining_date, offer_status, remarks) VALUES
(1, 'OFF-2026-001', 28, 'Business Analyst',           7.00, '2026-03-20', '2026-04-15', 'OFFERED',  'Offer released, awaiting candidate response'),
(2, 'OFF-2026-002', 29, 'Java Developer',             5.50, '2026-03-21', '2026-04-20', 'OFFERED',  'Offer released, awaiting candidate response'),
(3, 'OFF-2026-003', 36, 'Business Analyst',           7.00, '2026-03-24', '2026-05-01', 'ACCEPTED', 'Candidate accepted, joining on 1 May 2026'),
(4, 'OFF-2026-004', 37, 'Java Developer',             5.00, '2026-03-24', '2026-04-25', 'OFFERED',  'Offer released, awaiting candidate response'),
(5, 'OFF-2026-005', 38, 'Python Developer',           5.00, '2026-03-26', '2026-04-10', 'ACCEPTED', 'Candidate accepted and joined'),
(6, 'OFF-2026-006', 39, 'Frontend Developer (React)', 4.50, '2026-03-27', '2026-04-10', 'ACCEPTED', 'Candidate accepted and joined'),
(7, 'OFF-2026-007', 40, 'Software Engineer Trainee',  4.00, '2026-03-28', '2026-04-15', 'ACCEPTED', 'Candidate accepted and joined'),
(8, 'OFF-2026-008', 41, 'Full Stack Developer',       7.50, '2026-03-29', '2026-04-15', 'ACCEPTED', 'Candidate accepted and joined'),
(9, 'OFF-2026-009', 42, 'Business Analyst',           8.50, '2026-03-30', '2026-04-20', 'ACCEPTED', 'Candidate accepted and joined');

-- ----------------------------------------------------------------------------
-- placements (6) — one per ACCEPTED offer.
-- 5 JOINED (offers 5, 6, 7, 8, 9 — candidates 1-5, who are the 5 PLACED
-- candidates) and 1 PENDING (offer 3, joining on 1 May 2026).
-- ----------------------------------------------------------------------------
INSERT INTO placements (placement_id, candidate_id, company_id, job_id, offer_id, designation, final_package_lpa, expected_joining_date, actual_joining_date, joining_status, placement_status) VALUES
(1, 12, 2, 10, 3, 'Business Analyst',           7.00, '2026-05-01', NULL,         'PENDING', 'ACTIVE'),
(2, 1,  2, 2,  5, 'Python Developer',           5.00, '2026-04-10', '2026-04-10', 'JOINED',  'ACTIVE'),
(3, 2,  3, 3,  6, 'Frontend Developer (React)', 4.50, '2026-04-10', '2026-04-10', 'JOINED',  'ACTIVE'),
(4, 3,  5, 5,  7, 'Software Engineer Trainee',  4.00, '2026-04-15', '2026-04-15', 'JOINED',  'ACTIVE'),
(5, 4,  7, 7,  8, 'Full Stack Developer',       7.50, '2026-04-15', '2026-04-15', 'JOINED',  'ACTIVE'),
(6, 5,  2, 10, 9, 'Business Analyst',           8.50, '2026-04-20', '2026-04-20', 'JOINED',  'ACTIVE');

-- ----------------------------------------------------------------------------
-- activity_logs (10)
-- ----------------------------------------------------------------------------
INSERT INTO activity_logs (log_id, user_id, module_name, action_name, record_id, description, logged_at) VALUES
(1,  1, 'Users',        'LOGIN',          '1',          'Admin user logged in',  '2026-02-02 09:00:00'),
(2,  2, 'Jobs',         'CREATE',         'JOB-2026-001', 'Job created: Java Developer at TCS', '2026-01-05 10:00:00'),
(3,  3, 'Jobs',         'CREATE',         'JOB-2026-003', 'Job created: Frontend Developer (React) at Wipro', '2026-01-12 11:00:00'),
(4,  2, 'Candidates',   'CREATE',         'CAN-2026-0001', 'Candidate registered: Aarav Sharma', '2026-02-01 10:00:00'),
(5,  3, 'Applications', 'STATUS_CHANGE',  'APP-2026-0028', 'Application moved to SELECTED', '2026-03-18 10:00:00'),
(6,  2, 'Interviews',   'SCHEDULE',       '19',           'Round 1 interview scheduled for APP-2026-0019', '2026-03-01 10:00:00'),
(7,  4, 'Offers',       'CREATE',         'OFF-2026-005', 'Offer released for APP-2026-0038', '2026-03-26 10:00:00'),
(8,  4, 'Placements',   'CREATE',         '2',            'Placement recorded for Aarav Sharma at Infosys', '2026-04-10 11:00:00'),
(9,  1, 'Companies',    'UPDATE',         'COM-008',      'Company details updated: HCLTech', '2026-02-03 12:00:00'),
(10, 2, 'Applications', 'STATUS_CHANGE',  'APP-2026-0030', 'Application rejected: percentage below required', '2026-02-20 10:00:00');

-- End of sample data.
-- Quick checks after loading:
--   SELECT COUNT(*) FROM candidates;            -- 30
--   SELECT COUNT(*) FROM applications;          -- 45
--   SELECT COUNT(*) FROM placements WHERE joining_status = 'JOINED';  -- 5
