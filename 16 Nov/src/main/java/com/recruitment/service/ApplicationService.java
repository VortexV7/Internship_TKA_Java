package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.CandidateDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.exception.CandidateNotEligibleException;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.DuplicateApplicationException;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.model.Application;
import com.recruitment.model.EligibilityResult;
import com.recruitment.util.ActivityLogger;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ApplicationService - job applications, eligibility, and the recruitment workflow.
 *
 * Workflow (Spec section 8):
 *   APPLIED -> SHORTLISTED -> INTERVIEW -> SELECTED -> OFFERED -> JOINED
 *   REJECTED is allowed from any stage. Invalid jumps (e.g. APPLIED -> JOINED)
 *   are blocked. The database stores the finer-grained statuses from DB Spec
 *   section 21; the map below covers both spellings.
 */
public class ApplicationService {

    /** Allowed next statuses for each current status. REJECTED is allowed from any stage. */
    public static final Map<String, Set<String>> ALLOWED_TRANSITIONS = new HashMap<>();

    static {
        ALLOWED_TRANSITIONS.put("APPLIED",
                new HashSet<>(Arrays.asList("ELIGIBILITY_VERIFIED", "SHORTLISTED", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("ELIGIBILITY_VERIFIED",
                new HashSet<>(Arrays.asList("SHORTLISTED", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("SHORTLISTED",
                new HashSet<>(Arrays.asList("INTERVIEW_SCHEDULED", "INTERVIEW", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("INTERVIEW_SCHEDULED",
                new HashSet<>(Arrays.asList("INTERVIEWED", "INTERVIEW", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("INTERVIEW",
                new HashSet<>(Arrays.asList("INTERVIEWED", "SELECTED", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("INTERVIEWED",
                new HashSet<>(Arrays.asList("SELECTED", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("SELECTED",
                new HashSet<>(Arrays.asList("OFFERED", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("OFFERED",
                new HashSet<>(Arrays.asList("JOINED", "REJECTED", "WITHDRAWN")));
        ALLOWED_TRANSITIONS.put("JOINED", new HashSet<>());
        ALLOWED_TRANSITIONS.put("REJECTED", new HashSet<>());
        ALLOWED_TRANSITIONS.put("WITHDRAWN", new HashSet<>());
    }

    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final JobDAO jobDAO = new JobDAO();
    private final EligibilityService eligibilityService = new EligibilityService();

    /** Returns true if this is a legal workflow move (REJECTED always allowed). */
    public static boolean isValidTransition(String fromStatus, String toStatus) {
        if ("REJECTED".equals(toStatus)) {
            return true;
        }
        Set<String> allowed = ALLOWED_TRANSITIONS.get(fromStatus);
        return allowed != null && allowed.contains(toStatus);
    }

    /**
     * Creates an application. Runs the Eligibility Engine first; an ineligible
     * candidate is rejected with CandidateNotEligibleException and no row is created.
     * The insert + history row run in one transaction.
     */
    public int applyForJob(int candidateId, int jobId, String source, int recruiterId)
            throws SQLException, CandidateNotFoundException, JobNotFoundException,
            DuplicateApplicationException, CandidateNotEligibleException {
        // Existence checks (throw the spec exceptions).
        candidateDAO.findById(candidateId);
        jobDAO.findById(jobId);

        EligibilityResult result = eligibilityService.checkEligibility(candidateId, jobId);
        if (!result.isEligible()) {
            if (result.getReason().contains("already applied")) {
                throw new DuplicateApplicationException(result.getReason());
            }
            throw new CandidateNotEligibleException(result.getReason());
        }

        Application app = new Application();
        app.setApplicationCode("APP-" + System.currentTimeMillis());
        app.setCandidateId(candidateId);
        app.setJobId(jobId);
        app.setEligibilityStatus("ELIGIBLE");
        app.setEligibilityReason(result.getReason());
        app.setApplicationStatus("APPLIED");
        app.setSource(source == null ? "Portal" : source);
        app.setAssignedRecruiterId(recruiterId);

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);                       // transaction starts
            int id = applicationDAO.create(con, app);
            applicationDAO.insertHistory(con, id, null, "APPLIED",
                    "Application created - " + result.getReason(), recruiterId);
            con.commit();                                   // transaction ends
            app.setApplicationId(id);
            ActivityLogger.log("Application Created", app.getApplicationCode());
            ActivityLogger.log("Eligibility Checked", app.getApplicationCode() + " - ELIGIBLE");
            return id;
        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) { /* rollback best effort */ }
            }
            if (e.getMessage() != null && e.getMessage().contains("uq_candidate_job")) {
                throw new DuplicateApplicationException(
                        "Candidate has already applied for this job.");
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) { /* ignore */ }
            }
        }
    }

    /**
     * Moves an application to a new status. Invalid jumps are blocked.
     * Status update + history row run in one transaction (Spec section 28).
     */
    public void changeStatus(int applicationId, String newStatus, String remarks, int changedBy)
            throws SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            Application app = applicationDAO.findById(con, applicationId);
            if (app == null) {
                throw new SQLException("Application not found with id " + applicationId);
            }
            String oldStatus = app.getApplicationStatus();
            if (!isValidTransition(oldStatus, newStatus)) {
                throw new IllegalStateException("Invalid status transition: "
                        + oldStatus + " -> " + newStatus + ".");
            }
            applicationDAO.updateStatus(con, applicationId, newStatus);
            applicationDAO.insertHistory(con, applicationId, oldStatus, newStatus, remarks, changedBy);
            con.commit();
            ActivityLogger.log("Application Status Changed",
                    "APP" + applicationId + ": " + oldStatus + " -> " + newStatus);
        } catch (SQLException | IllegalStateException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) { /* ignore */ }
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) { /* ignore */ }
            }
        }
    }

    /** Shortlist = APPLIED/ELIGIBILITY_VERIFIED -> SHORTLISTED. */
    public void shortlistApplication(int applicationId, int changedBy) throws SQLException {
        Application app = applicationDAO.findById(applicationId);
        if (app == null) {
            throw new SQLException("Application not found with id " + applicationId);
        }
        String from = app.getApplicationStatus();
        String to = "SHORTLISTED";
        if (!isValidTransition(from, to)) {
            throw new IllegalStateException("Cannot shortlist an application in status " + from + ".");
        }
        changeStatus(applicationId, to, "Shortlisted by recruiter", changedBy);
        ActivityLogger.log("Candidate Shortlisted", "APP" + applicationId);
    }

    public Application getApplication(int applicationId) throws SQLException {
        return applicationDAO.findById(applicationId);
    }

    public List<Application> getAllApplications() throws SQLException {
        return applicationDAO.findAll();
    }

    public List<Application> getApplicationsByStatus(String status) throws SQLException {
        return applicationDAO.findByStatus(status);
    }

    public List<Application> getApplicationsByCandidate(int candidateId) throws SQLException {
        return applicationDAO.findByCandidate(candidateId);
    }
}
