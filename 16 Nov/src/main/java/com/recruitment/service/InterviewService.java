package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.InterviewDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Interview;
import com.recruitment.model.InterviewFeedback;
import com.recruitment.util.ActivityLogger;
import com.recruitment.util.DBConnection;
import com.recruitment.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * InterviewService - scheduling rounds and recording feedback.
 * Interview completion transaction (Spec section 28):
 * update interview -> insert feedback -> update application (if required).
 */
public class InterviewService {

    private final InterviewDAO interviewDAO = new InterviewDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    /** Schedules a round; duplicate (application, round_number) is rejected. */
    public int scheduleInterview(Interview interview, int changedBy)
            throws ValidationException, SQLException {
        if (interview.getRoundNumber() < 1) {
            throw new ValidationException("Round number must be 1 or higher.");
        }
        if (interview.getScheduledDate() == null) {
            throw new ValidationException("Scheduled date is required.");
        }
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            if (interviewDAO.roundExists(con, interview.getApplicationId(), interview.getRoundNumber())) {
                throw new ValidationException("Round " + interview.getRoundNumber()
                        + " already exists for this application.");
            }
            if (interview.getStatus() == null || interview.getStatus().isBlank()) {
                interview.setStatus("SCHEDULED");
            }
            int id = interviewDAO.scheduleInterview(con, interview);
            // Move the application to INTERVIEW_SCHEDULED (allowed from SHORTLISTED).
            try {
                applicationDAO.updateStatus(con, interview.getApplicationId(), "INTERVIEW_SCHEDULED");
                applicationDAO.insertHistory(con, interview.getApplicationId(),
                        "SHORTLISTED", "INTERVIEW_SCHEDULED",
                        "Interview round " + interview.getRoundNumber() + " scheduled", changedBy);
            } catch (SQLException e) {
                // If the application was not SHORTLISTED, keep the interview but do not fail:
                // the status move is best-effort here; the caller can move it explicitly.
                con.rollback();
                throw e;
            }
            con.commit();
            ActivityLogger.log("Interview Scheduled",
                    "APP" + interview.getApplicationId() + " Round " + interview.getRoundNumber());
            return id;
        } catch (SQLException | ValidationException e) {
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

    /**
     * Records feedback for a round and marks it COMPLETED in one transaction.
     * If the recommendation is REJECTED the application moves to REJECTED;
     * if SELECTED it moves to SELECTED (ready for an offer).
     */
    public int submitFeedback(InterviewFeedback feedback, int changedBy)
            throws ValidationException, SQLException {
        ValidationUtil.checkRating(feedback.getTechnicalRating(), "Technical");
        ValidationUtil.checkRating(feedback.getCommunicationRating(), "Communication");
        ValidationUtil.checkRating(feedback.getProblemSolvingRating(), "Problem solving");
        ValidationUtil.checkRating(feedback.getOverallRating(), "Overall");
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            Interview interview = interviewDAO.findById(feedback.getInterviewId());
            if (interview == null) {
                throw new ValidationException("Interview not found with id " + feedback.getInterviewId());
            }
            if (interviewDAO.findFeedbackByInterview(feedback.getInterviewId()) != null) {
                throw new ValidationException(
                        "Feedback already exists for this interview round (one feedback per round).");
            }
            int feedbackId = interviewDAO.insertFeedback(con, feedback);
            interviewDAO.updateStatus(con, feedback.getInterviewId(), "COMPLETED");
            String recommendation = feedback.getRecommendation();
            if ("REJECTED".equals(recommendation)) {
                applicationDAO.updateStatus(con, interview.getApplicationId(), "REJECTED");
                applicationDAO.insertHistory(con, interview.getApplicationId(),
                        "INTERVIEWED", "REJECTED", "Rejected after interview feedback", changedBy);
            } else if ("SELECTED".equals(recommendation)) {
                applicationDAO.updateStatus(con, interview.getApplicationId(), "SELECTED");
                applicationDAO.insertHistory(con, interview.getApplicationId(),
                        "INTERVIEWED", "SELECTED", "Selected after interview feedback", changedBy);
            } else {
                applicationDAO.updateStatus(con, interview.getApplicationId(), "INTERVIEWED");
                applicationDAO.insertHistory(con, interview.getApplicationId(),
                        "INTERVIEW_SCHEDULED", "INTERVIEWED",
                        "Interview round " + interview.getRoundNumber() + " completed", changedBy);
            }
            con.commit();
            ActivityLogger.log("Interview Feedback Submitted",
                    "Interview " + feedback.getInterviewId() + " - " + recommendation);
            return feedbackId;
        } catch (SQLException | ValidationException e) {
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

    public List<Interview> getInterviewsByApplication(int applicationId) throws SQLException {
        return interviewDAO.findByApplication(applicationId);
    }

    public List<Interview> getScheduledInterviews() throws SQLException {
        return interviewDAO.findScheduled();
    }

    public List<Interview> getAllInterviews() throws SQLException {
        return interviewDAO.findAll();
    }

    public InterviewFeedback getFeedback(int interviewId) throws SQLException {
        return interviewDAO.findFeedbackByInterview(interviewId);
    }
}
