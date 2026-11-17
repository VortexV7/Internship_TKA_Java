package com.recruitment.dao;

import com.recruitment.model.Interview;
import com.recruitment.model.InterviewFeedback;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** InterviewDAO - SQL for interview_rounds and interview_feedback. */
public class InterviewDAO {

    public int scheduleInterview(Connection con, Interview interview) throws SQLException {
        String sql = "INSERT INTO interview_rounds (application_id, round_number, round_type, "
                + "scheduled_date, scheduled_time, mode, interviewer_name, "
                + "meeting_or_location, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, interview.getApplicationId());
            ps.setInt(2, interview.getRoundNumber());
            ps.setString(3, interview.getRoundType());
            ps.setDate(4, interview.getScheduledDate());
            ps.setTime(5, interview.getScheduledTime());
            ps.setString(6, interview.getMode());
            ps.setString(7, interview.getInterviewerName());
            ps.setString(8, interview.getMeetingOrLocation());
            ps.setString(9, interview.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public boolean roundExists(Connection con, int applicationId, int roundNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM interview_rounds "
                + "WHERE application_id = ? AND round_number = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ps.setInt(2, roundNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public Interview findById(int interviewId) throws SQLException {
        String sql = "SELECT * FROM interview_rounds WHERE interview_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, interviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Interview> findByApplication(int applicationId) throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = "SELECT * FROM interview_rounds WHERE application_id = ? ORDER BY round_number";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Interview> findScheduled() throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = "SELECT * FROM interview_rounds WHERE status = 'SCHEDULED' "
                + "ORDER BY scheduled_date, scheduled_time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Interview> findAll() throws SQLException {
        List<Interview> list = new ArrayList<>();
        String sql = "SELECT * FROM interview_rounds ORDER BY scheduled_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public boolean updateStatus(Connection con, int interviewId, String status) throws SQLException {
        String sql = "UPDATE interview_rounds SET status = ? WHERE interview_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, interviewId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Interview completion transaction piece: insert feedback and mark the
     * round COMPLETED in the same transaction as the caller.
     * UNIQUE(interview_id) in the schema allows only one feedback per round.
     */
    public int insertFeedback(Connection con, InterviewFeedback feedback) throws SQLException {
        String sql = "INSERT INTO interview_feedback (interview_id, technical_rating, "
                + "communication_rating, problem_solving_rating, overall_rating, "
                + "recommendation, remarks, submitted_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, feedback.getInterviewId());
            ps.setDouble(2, feedback.getTechnicalRating());
            ps.setDouble(3, feedback.getCommunicationRating());
            ps.setDouble(4, feedback.getProblemSolvingRating());
            ps.setDouble(5, feedback.getOverallRating());
            ps.setString(6, feedback.getRecommendation());
            ps.setString(7, feedback.getRemarks());
            if (feedback.getSubmittedBy() == 0) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, feedback.getSubmittedBy());
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public InterviewFeedback findFeedbackByInterview(int interviewId) throws SQLException {
        String sql = "SELECT * FROM interview_feedback WHERE interview_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, interviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InterviewFeedback f = new InterviewFeedback();
                    f.setFeedbackId(rs.getInt("feedback_id"));
                    f.setInterviewId(rs.getInt("interview_id"));
                    f.setTechnicalRating(rs.getDouble("technical_rating"));
                    f.setCommunicationRating(rs.getDouble("communication_rating"));
                    f.setProblemSolvingRating(rs.getDouble("problem_solving_rating"));
                    f.setOverallRating(rs.getDouble("overall_rating"));
                    f.setRecommendation(rs.getString("recommendation"));
                    f.setRemarks(rs.getString("remarks"));
                    f.setSubmittedBy(rs.getInt("submitted_by"));
                    f.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    return f;
                }
            }
        }
        return null;
    }

    public static Interview map(ResultSet rs) throws SQLException {
        Interview i = new Interview();
        i.setInterviewId(rs.getInt("interview_id"));
        i.setApplicationId(rs.getInt("application_id"));
        i.setRoundNumber(rs.getInt("round_number"));
        i.setRoundType(rs.getString("round_type"));
        i.setScheduledDate(rs.getDate("scheduled_date"));
        i.setScheduledTime(rs.getTime("scheduled_time"));
        i.setMode(rs.getString("mode"));
        i.setInterviewerName(rs.getString("interviewer_name"));
        i.setMeetingOrLocation(rs.getString("meeting_or_location"));
        i.setStatus(rs.getString("status"));
        i.setCreatedAt(rs.getTimestamp("created_at"));
        return i;
    }
}
