package com.recruitment.app;

import com.recruitment.exception.CandidateNotEligibleException;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.DuplicateApplicationException;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Application;
import com.recruitment.model.Candidate;
import com.recruitment.model.Company;
import com.recruitment.model.Interview;
import com.recruitment.model.InterviewFeedback;
import com.recruitment.model.Job;
import com.recruitment.model.Offer;
import com.recruitment.model.Placement;
import com.recruitment.service.ApplicationService;
import com.recruitment.service.CandidateService;
import com.recruitment.service.CompanyService;
import com.recruitment.service.InterviewService;
import com.recruitment.service.JobService;
import com.recruitment.service.OfferService;
import com.recruitment.service.PlacementService;
import com.recruitment.service.ReportService;
import com.recruitment.util.DBConnection;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * RecruitmentApp - console user interface (Spec section 15).
 *
 * Main menu (exactly as specified):
 *   1. Candidates   2. Companies   3. Jobs   4. Applications   5. Interviews
 *   6. Offers       7. Placements  8. Reports  9. Exit
 *
 * If MySQL is not reachable the menu still shows, and every database
 * operation prints a clear message instead of crashing.
 */
public class RecruitmentApp {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final CandidateService CANDIDATE_SERVICE = new CandidateService();
    private static final CompanyService COMPANY_SERVICE = new CompanyService();
    private static final JobService JOB_SERVICE = new JobService();
    private static final ApplicationService APPLICATION_SERVICE = new ApplicationService();
    private static final InterviewService INTERVIEW_SERVICE = new InterviewService();
    private static final OfferService OFFER_SERVICE = new OfferService();
    private static final PlacementService PLACEMENT_SERVICE = new PlacementService();
    private static final ReportService REPORT_SERVICE = new ReportService();

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("RECRUITMENT & PLACEMENT SYSTEM");
        System.out.println("==================================");
        if (!DBConnection.isDatabaseAvailable()) {
            System.out.println("[WARNING] Could not connect to MySQL (recruitment_db).");
            System.out.println("          Check that MySQL is running and db.properties is correct.");
            System.out.println("          The menu below will still open; database actions will report the error.");
        } else {
            System.out.println("Connected to MySQL database: recruitment_db");
        }
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> candidateMenu();
                case 2 -> companyMenu();
                case 3 -> jobMenu();
                case 4 -> applicationMenu();
                case 5 -> interviewMenu();
                case 6 -> offerMenu();
                case 7 -> placementMenu();
                case 8 -> reportMenu();
                case 9 -> {
                    running = false;
                    System.out.println("Thank you for using the Recruitment & Placement System. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please enter a number from 1 to 9.");
            }
        }
        SCANNER.close();
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println("==================================");
        System.out.println("RECRUITMENT & PLACEMENT SYSTEM");
        System.out.println("==================================");
        System.out.println("1. Candidates");
        System.out.println("2. Companies");
        System.out.println("3. Jobs");
        System.out.println("4. Applications");
        System.out.println("5. Interviews");
        System.out.println("6. Offers");
        System.out.println("7. Placements");
        System.out.println("8. Reports");
        System.out.println("9. Exit");
    }

    // ------------------------------------------------------------------ helpers

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = SCANNER.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    private static Date readDate(String prompt) {
        while (true) {
            String text = readLine(prompt + " (yyyy-MM-dd, blank = none): ");
            if (text.isEmpty()) {
                return null;
            }
            try {
                return Date.valueOf(LocalDate.parse(text));
            } catch (Exception e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd, e.g. 2026-03-15.");
            }
        }
    }

    /** Friendly error line used by every menu when the database is down or an operation fails. */
    private static void showError(Exception e) {
        System.out.println("[ERROR] " + e.getMessage());
        if (e instanceof SQLException) {
            System.out.println("        (Database error - is MySQL running? Check db.properties.)");
        }
    }

    private static void pause() {
        System.out.print("Press Enter to continue...");
        SCANNER.nextLine();
    }

    // ------------------------------------------------------------------ 1. Candidates

    private static void candidateMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Candidates -----");
            System.out.println("1. Add Candidate");
            System.out.println("2. View All Candidates");
            System.out.println("3. Search Candidate by ID");
            System.out.println("4. Search Candidates by Technology");
            System.out.println("5. Update Candidate");
            System.out.println("6. Deactivate Candidate");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> addCandidate();
                    case 2 -> {
                        List<Candidate> list = CANDIDATE_SERVICE.getAllCandidates();
                        System.out.println("ID | Code | Name | Email | Mobile | % | Year | Backlogs | Tech | Profile | Placement");
                        for (Candidate c : list) {
                            System.out.printf("%d | %s | %s | %s | %s | %.2f | %d | %d | %s | %s | %s%n",
                                    c.getCandidateId(), c.getCandidateCode(), c.getFullName(),
                                    c.getEmail(), c.getMobile(), c.getPercentage(),
                                    c.getPassingYear(), c.getBacklogCount(),
                                    c.getPrimaryTechnology(), c.getProfileStatus(),
                                    c.getPlacementStatus());
                        }
                        System.out.println("Total candidates: " + list.size());
                    }
                    case 3 -> {
                        int id = readInt("Candidate ID: ");
                        System.out.println(CANDIDATE_SERVICE.getCandidate(id));
                    }
                    case 4 -> {
                        String tech = readLine("Technology (e.g. Java): ");
                        List<Candidate> list = CANDIDATE_SERVICE.getCandidatesByTechnology(tech);
                        for (Candidate c : list) {
                            System.out.println(c.getCandidateCode() + " - " + c.getFullName()
                                    + " (" + c.getPercentage() + "%, " + c.getPassingYear() + ")");
                        }
                        System.out.println("Found: " + list.size());
                    }
                    case 5 -> updateCandidate();
                    case 6 -> {
                        int id = readInt("Candidate ID to deactivate: ");
                        System.out.println(CANDIDATE_SERVICE.deactivateCandidate(id)
                                ? "Candidate deactivated (soft-delete: profile set to INACTIVE)."
                                : "Candidate not found.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (CandidateNotFoundException | ValidationException | SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    private static void addCandidate() throws ValidationException, SQLException {
        Candidate c = new Candidate();
        c.setCandidateCode(readLine("Candidate code (e.g. CAN-2026-0031): "));
        c.setFullName(readLine("Full name: "));
        c.setEmail(readLine("Email: "));
        c.setMobile(readLine("Mobile (10 digits): "));
        c.setGender(readLine("Gender (optional): "));
        c.setQualification(readLine("Qualification (e.g. BTech): "));
        c.setCollege(readLine("College: "));
        c.setUniversity(readLine("University: "));
        c.setPassingYear(readInt("Passing year: "));
        c.setPercentage(readDouble("Percentage (0-100): "));
        c.setCgpa(readDouble("CGPA (0 if none): "));
        c.setBacklogCount(readInt("Backlog count: "));
        c.setPrimaryTechnology(readLine("Primary technology (e.g. Java): "));
        c.setCurrentLocation(readLine("Current location: "));
        c.setPreferredLocation(readLine("Preferred location: "));
        int id = CANDIDATE_SERVICE.registerCandidate(c);
        System.out.println("Candidate registered successfully. Candidate ID: " + id);
    }

    private static void updateCandidate() throws ValidationException, SQLException, CandidateNotFoundException {
        int id = readInt("Candidate ID to update: ");
        Candidate c = CANDIDATE_SERVICE.getCandidate(id);
        System.out.println("Current: " + c);
        String name = readLine("New full name (blank = keep '" + c.getFullName() + "'): ");
        if (!name.isEmpty()) {
            c.setFullName(name);
        }
        String tech = readLine("New primary technology (blank = keep): ");
        if (!tech.isEmpty()) {
            c.setPrimaryTechnology(tech);
        }
        String pct = readLine("New percentage (blank = keep): ");
        if (!pct.isEmpty()) {
            c.setPercentage(Double.parseDouble(pct));
        }
        System.out.println(CANDIDATE_SERVICE.updateCandidate(c)
                ? "Candidate updated." : "Update failed.");
    }

    // ------------------------------------------------------------------ 2. Companies

    private static void companyMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Companies -----");
            System.out.println("1. Add Company");
            System.out.println("2. View All Companies");
            System.out.println("3. View Company by ID");
            System.out.println("4. Deactivate Company");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> {
                        Company c = new Company();
                        c.setCompanyCode(readLine("Company code (e.g. COM-009): "));
                        c.setCompanyName(readLine("Company name: "));
                        c.setIndustry(readLine("Industry: "));
                        c.setWebsite(readLine("Website: "));
                        c.setLocation(readLine("Location: "));
                        c.setCompanyType(readLine("Type (MNC / STARTUP / MID_SIZE / CONSULTANCY): "));
                        c.setHrName(readLine("HR name: "));
                        c.setHrEmail(readLine("HR email: "));
                        c.setHrMobile(readLine("HR mobile: "));
                        int id = COMPANY_SERVICE.addCompany(c);
                        System.out.println("Company added. Company ID: " + id);
                    }
                    case 2 -> {
                        for (Company c : COMPANY_SERVICE.getAllCompanies()) {
                            System.out.println(c.getCompanyId() + " | " + c.getCompanyCode() + " | "
                                    + c.getCompanyName() + " | " + c.getLocation() + " | " + c.getStatus());
                        }
                    }
                    case 3 -> {
                        int id = readInt("Company ID: ");
                        System.out.println(COMPANY_SERVICE.getCompany(id));
                    }
                    case 4 -> {
                        int id = readInt("Company ID to deactivate: ");
                        System.out.println(COMPANY_SERVICE.deactivateCompany(id)
                                ? "Company set to INACTIVE." : "Company not found.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ValidationException | SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    // ------------------------------------------------------------------ 3. Jobs

    private static void jobMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Jobs -----");
            System.out.println("1. Create Job");
            System.out.println("2. View All Jobs");
            System.out.println("3. View Open Jobs");
            System.out.println("4. Search Job by Title");
            System.out.println("5. Close Job");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> createJob();
                    case 2 -> printJobs(JOB_SERVICE.getAllJobs());
                    case 3 -> printJobs(JOB_SERVICE.getOpenJobs());
                    case 4 -> {
                        String keyword = readLine("Title keyword: ");
                        printJobs(JOB_SERVICE.searchJobs(keyword));
                    }
                    case 5 -> {
                        int id = readInt("Job ID to close: ");
                        System.out.println(JOB_SERVICE.closeJob(id)
                                ? "Job closed." : "Job not found.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ValidationException | SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    private static void printJobs(List<Job> jobs) {
        System.out.println("ID | Code | Title | Company ID | Location | Openings | Package (LPA) | Min % | Year | Backlogs | Deadline | Status");
        for (Job j : jobs) {
            System.out.printf("%d | %s | %s | %d | %s | %d | %.2f-%.2f | %.2f | %d-%d | %d | %s | %s%n",
                    j.getJobId(), j.getJobCode(), j.getJobTitle(), j.getCompanyId(),
                    j.getLocation(), j.getOpenings(), j.getMinPackageLpa(), j.getMaxPackageLpa(),
                    j.getMinPercentage(), j.getMinPassingYear(), j.getMaxPassingYear(),
                    j.getMaxBacklogs(), j.getApplicationDeadline(), j.getStatus());
        }
        System.out.println("Total jobs: " + jobs.size());
    }

    private static void createJob() throws ValidationException, SQLException {
        Job j = new Job();
        j.setJobCode(readLine("Job code (e.g. JOB-2026-013): "));
        j.setCompanyId(readInt("Company ID: "));
        j.setJobTitle(readLine("Job title: "));
        j.setEmploymentType(readLine("Employment type (FULL_TIME / INTERNSHIP / CONTRACT): "));
        j.setLocation(readLine("Location: "));
        j.setOpenings(readInt("Openings: "));
        j.setMinPackageLpa(readDouble("Minimum package (LPA): "));
        j.setMaxPackageLpa(readDouble("Maximum package (LPA): "));
        j.setMinPercentage(readDouble("Minimum percentage: "));
        j.setMinPassingYear(readInt("Minimum passing year: "));
        j.setMaxPassingYear(readInt("Maximum passing year: "));
        j.setMaxBacklogs(readInt("Maximum backlogs allowed: "));
        j.setQualificationRule(readLine("Qualification rule (e.g. BTech / BE / MCA): "));
        j.setPostedDate(readDate("Posted date"));
        j.setApplicationDeadline(readDate("Application deadline"));
        int id = JOB_SERVICE.createJob(j);
        System.out.println("Job created successfully. Job ID: " + id);
    }

    // ------------------------------------------------------------------ 4. Applications

    private static void applicationMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Applications -----");
            System.out.println("1. Apply for a Job (runs Eligibility Check)");
            System.out.println("2. View All Applications");
            System.out.println("3. View Applications by Status");
            System.out.println("4. Shortlist Application");
            System.out.println("5. Change Application Status");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> {
                        int candidateId = readInt("Candidate ID: ");
                        int jobId = readInt("Job ID: ");
                        String source = readLine("Source (Portal / Recruiter / Referral): ");
                        int id = APPLICATION_SERVICE.applyForJob(candidateId, jobId, source, 0);
                        System.out.println("Application created (ELIGIBLE). Application ID: " + id);
                    }
                    case 2 -> printApplications(APPLICATION_SERVICE.getAllApplications());
                    case 3 -> {
                        String status = readLine("Status (e.g. APPLIED, SHORTLISTED, SELECTED): ");
                        printApplications(APPLICATION_SERVICE.getApplicationsByStatus(status));
                    }
                    case 4 -> {
                        int id = readInt("Application ID to shortlist: ");
                        APPLICATION_SERVICE.shortlistApplication(id, 0);
                        System.out.println("Application shortlisted.");
                    }
                    case 5 -> {
                        int id = readInt("Application ID: ");
                        String status = readLine("New status: ");
                        String remarks = readLine("Remarks: ");
                        APPLICATION_SERVICE.changeStatus(id, status, remarks, 0);
                        System.out.println("Status updated to " + status + ".");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (CandidateNotFoundException | JobNotFoundException
                    | DuplicateApplicationException | CandidateNotEligibleException e) {
                System.out.println("[NOT ELIGIBLE / BLOCKED] " + e.getMessage());
            } catch (IllegalStateException | SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    private static void printApplications(List<Application> apps) {
        System.out.println("ID | Code | Candidate ID | Job ID | Eligibility | Status | Applied At");
        for (Application a : apps) {
            System.out.printf("%d | %s | %d | %d | %s | %s | %s%n",
                    a.getApplicationId(), a.getApplicationCode(), a.getCandidateId(),
                    a.getJobId(), a.getEligibilityStatus(), a.getApplicationStatus(),
                    a.getAppliedAt());
            if (a.getEligibilityReason() != null) {
                System.out.println("     Reason: " + a.getEligibilityReason());
            }
        }
        System.out.println("Total applications: " + apps.size());
    }

    // ------------------------------------------------------------------ 5. Interviews

    private static void interviewMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Interviews -----");
            System.out.println("1. Schedule Interview");
            System.out.println("2. View Scheduled Interviews");
            System.out.println("3. View Interviews by Application");
            System.out.println("4. Submit Interview Feedback");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> {
                        Interview interview = new Interview();
                        interview.setApplicationId(readInt("Application ID: "));
                        interview.setRoundNumber(readInt("Round number: "));
                        interview.setRoundType(readLine("Round type (APTITUDE / TECHNICAL / MANAGERIAL / HR): "));
                        interview.setScheduledDate(readDate("Scheduled date"));
                        interview.setMode(readLine("Mode (ONLINE / OFFLINE / TELEPHONIC): "));
                        interview.setInterviewerName(readLine("Interviewer name: "));
                        interview.setMeetingOrLocation(readLine("Meeting link / venue: "));
                        int id = INTERVIEW_SERVICE.scheduleInterview(interview, 0);
                        System.out.println("Interview scheduled. Interview ID: " + id);
                    }
                    case 2 -> {
                        for (Interview i : INTERVIEW_SERVICE.getScheduledInterviews()) {
                            System.out.println(i.getInterviewId() + " | App " + i.getApplicationId()
                                    + " | Round " + i.getRoundNumber() + " " + i.getRoundType()
                                    + " | " + i.getScheduledDate() + " " + i.getScheduledTime()
                                    + " | " + i.getMode() + " | " + i.getInterviewerName()
                                    + " | " + i.getStatus());
                        }
                    }
                    case 3 -> {
                        int appId = readInt("Application ID: ");
                        for (Interview i : INTERVIEW_SERVICE.getInterviewsByApplication(appId)) {
                            System.out.println(i);
                        }
                    }
                    case 4 -> {
                        InterviewFeedback feedback = new InterviewFeedback();
                        feedback.setInterviewId(readInt("Interview ID: "));
                        feedback.setTechnicalRating(readDouble("Technical rating (0-5): "));
                        feedback.setCommunicationRating(readDouble("Communication rating (0-5): "));
                        feedback.setProblemSolvingRating(readDouble("Problem solving rating (0-5): "));
                        feedback.setOverallRating(readDouble("Overall rating (0-5): "));
                        feedback.setRecommendation(readLine("Recommendation (NEXT_ROUND / SELECTED / REJECTED / ON_HOLD): "));
                        feedback.setRemarks(readLine("Remarks: "));
                        int id = INTERVIEW_SERVICE.submitFeedback(feedback, 0);
                        System.out.println("Feedback submitted. Feedback ID: " + id);
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ValidationException | SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    // ------------------------------------------------------------------ 6. Offers

    private static void offerMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Offers -----");
            System.out.println("1. Create Offer (for a SELECTED application)");
            System.out.println("2. View All Offers");
            System.out.println("3. View Offer by Application");
            System.out.println("4. Update Offer Status");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> {
                        Offer offer = new Offer();
                        offer.setOfferCode(readLine("Offer code (e.g. OFF-2026-010): "));
                        offer.setApplicationId(readInt("Application ID (must be SELECTED): "));
                        offer.setDesignation(readLine("Designation: "));
                        offer.setOfferedPackageLpa(readDouble("Offered package (LPA): "));
                        offer.setOfferDate(readDate("Offer date"));
                        offer.setExpectedJoiningDate(readDate("Expected joining date"));
                        offer.setRemarks(readLine("Remarks: "));
                        int id = OFFER_SERVICE.createOffer(offer, 0);
                        System.out.println("Offer created. Offer ID: " + id);
                    }
                    case 2 -> {
                        for (Offer o : OFFER_SERVICE.getAllOffers()) {
                            System.out.println(o.getOfferId() + " | " + o.getOfferCode()
                                    + " | App " + o.getApplicationId() + " | " + o.getDesignation()
                                    + " | " + o.getOfferedPackageLpa() + " LPA | "
                                    + o.getOfferDate() + " | " + o.getOfferStatus());
                        }
                    }
                    case 3 -> {
                        int appId = readInt("Application ID: ");
                        System.out.println(OFFER_SERVICE.getOfferByApplication(appId));
                    }
                    case 4 -> {
                        int id = readInt("Offer ID: ");
                        String status = readLine("New status (OFFERED / ACCEPTED / REJECTED / EXPIRED / WITHDRAWN): ");
                        System.out.println(OFFER_SERVICE.updateOfferStatus(id, status)
                                ? "Offer status updated." : "Offer not found.");
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ValidationException | SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    // ------------------------------------------------------------------ 7. Placements

    private static void placementMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Placements -----");
            System.out.println("1. Confirm Joining (create Placement for an ACCEPTED offer)");
            System.out.println("2. View All Placements");
            System.out.println("3. View Joined Placements");
            System.out.println("4. Company-wise Placements");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> {
                        Placement p = new Placement();
                        p.setOfferId(readInt("Offer ID (must be ACCEPTED): "));
                        p.setCandidateId(readInt("Candidate ID: "));
                        p.setCompanyId(readInt("Company ID: "));
                        p.setJobId(readInt("Job ID: "));
                        p.setDesignation(readLine("Designation: "));
                        p.setFinalPackageLpa(readDouble("Final package (LPA): "));
                        p.setExpectedJoiningDate(readDate("Expected joining date"));
                        p.setActualJoiningDate(readDate("Actual joining date"));
                        p.setJoiningStatus(readLine("Joining status (JOINED / PENDING / DEFERRED / NO_SHOW): "));
                        int id = PLACEMENT_SERVICE.confirmJoining(p, 0);
                        System.out.println("Placement recorded. Placement ID: " + id);
                    }
                    case 2 -> {
                        for (Placement p : PLACEMENT_SERVICE.getAllPlacements()) {
                            System.out.println(p.getPlacementId() + " | Candidate " + p.getCandidateId()
                                    + " | Company " + p.getCompanyId() + " | " + p.getDesignation()
                                    + " | " + p.getFinalPackageLpa() + " LPA | " + p.getJoiningStatus());
                        }
                    }
                    case 3 -> {
                        for (Placement p : PLACEMENT_SERVICE.getJoinedPlacements()) {
                            System.out.println(p);
                        }
                    }
                    case 4 -> {
                        for (Map.Entry<String, Integer> e : PLACEMENT_SERVICE.getCompanyWisePlacements().entrySet()) {
                            System.out.println(e.getKey() + ": " + e.getValue() + " placed");
                        }
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (ValidationException | SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    // ------------------------------------------------------------------ 8. Reports

    private static void reportMenu() {
        while (true) {
            System.out.println();
            System.out.println("----- Reports -----");
            System.out.println("1. Candidates by Technology");
            System.out.println("2. Open Jobs");
            System.out.println("3. Applications by Status");
            System.out.println("4. Recruitment Funnel");
            System.out.println("5. Company-wise Placements");
            System.out.println("6. Technology-wise Placements");
            System.out.println("7. Recruiter Performance");
            System.out.println("8. Pending Interviews");
            System.out.println("9. Offer-to-Joining Conversion");
            System.out.println("10. Job Aging");
            System.out.println("11. Placed Candidates");
            System.out.println("12. Unplaced Candidates");
            System.out.println("0. Back to Main Menu");
            int choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> printCounts("Candidates by Technology", REPORT_SERVICE.candidatesByTechnology());
                    case 2 -> {
                        for (String[] row : REPORT_SERVICE.openJobsByCompany()) {
                            System.out.println(String.join(" | ", row));
                        }
                    }
                    case 3 -> printCounts("Applications by Status", REPORT_SERVICE.applicationsByStatus());
                    case 4 -> printCounts("Recruitment Funnel", REPORT_SERVICE.recruitmentFunnel());
                    case 5 -> printCounts("Company-wise Placements", REPORT_SERVICE.companyWisePlacements());
                    case 6 -> printCounts("Technology-wise Placements", REPORT_SERVICE.placementsByTechnology());
                    case 7 -> {
                        System.out.println("Recruiter | Total Applications | Successful (Selected/Offered/Joined)");
                        for (String[] row : REPORT_SERVICE.recruiterPerformance()) {
                            System.out.println(String.join(" | ", row));
                        }
                    }
                    case 8 -> {
                        System.out.println("ID | Round | Type | Date | Time | Mode | Interviewer");
                        for (String[] row : REPORT_SERVICE.pendingInterviews()) {
                            System.out.println(String.join(" | ", row));
                        }
                    }
                    case 9 -> {
                        String[] r = REPORT_SERVICE.offerToJoiningConversion();
                        System.out.println("Total offers: " + r[0]);
                        System.out.println("Accepted offers: " + r[1]);
                        System.out.println("Joined: " + r[2]);
                        if (!"0".equals(r[0])) {
                            double pct = 100.0 * Integer.parseInt(r[2]) / Integer.parseInt(r[0]);
                            System.out.printf("Offer-to-joining conversion: %.1f%%%n", pct);
                        }
                    }
                    case 10 -> {
                        System.out.println("Job Code | Title | Posted | Age (days) | Status");
                        for (String[] row : REPORT_SERVICE.jobAging()) {
                            System.out.println(String.join(" | ", row));
                        }
                    }
                    case 11 -> {
                        for (Candidate c : REPORT_SERVICE.placedCandidates()) {
                            System.out.println(c.getCandidateCode() + " - " + c.getFullName()
                                    + " (" + c.getPrimaryTechnology() + ")");
                        }
                    }
                    case 12 -> {
                        for (Candidate c : REPORT_SERVICE.unplacedCandidates()) {
                            System.out.println(c.getCandidateCode() + " - " + c.getFullName()
                                    + " (" + c.getPrimaryTechnology() + ")");
                        }
                    }
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                showError(e);
            }
            pause();
        }
    }

    private static void printCounts(String title, Map<String, Integer> counts) {
        System.out.println("----- " + title + " -----");
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }
    }
}
