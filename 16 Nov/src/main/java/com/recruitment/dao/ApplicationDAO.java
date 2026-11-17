package com.recruitment.dao;

import com.recruitment.model.Application;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ApplicationDAO - SQL for applications + application_history.
 * The UNIQUE(candidate_id, job_id) constraint in the schema blocks duplicate
 * applications at database level as well as in the service layer.
 */
public class ApplicationDAO {

    public int create(Connection con, Application a) throws SQLException {
        String sql = "INSERT INTO applications (application_code, candidate_id, job_id, "
                + "eligibility_status, eligibility_reason, application_status, source, "
                + "assigned_recruiter_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getApplicationCode());
            ps.setInt(2, a.getCandidateId());
            ps.setInt(3, a.getJobId());
            ps.setString(4, a.getEligibilityStatus());
            ps.setString(5, a.getEligibilityReason());
            ps.setString(6, a.getApplicationStatus());
            ps.setString(7, a.getSource());
            if (a.getAssignedRecruiterId() == 0) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, a.getAssignedRecruiterId());
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

    public Application findById(int applicationId) throws SQLException {
        String sql = "SELECT * FROM applications WHERE application_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Application findById(Connection con, int applicationId) throws SQLException {
        String sql = "SELECT * FROM applications WHERE application_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    /** Returns true if this candidate already applied for this job. */
    public boolean existsByCandidateAndJob(int candidateId, int jobId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applications WHERE candidate_id = ? AND job_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            ps.setInt(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean existsByCandidateAndJob(Connection con, int candidateId, int jobId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM applications WHERE candidate_id = ? AND job_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            ps.setInt(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Application> findAll() throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications ORDER BY applied_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Application> findByStatus(String status) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE application_status = ? ORDER BY applied_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Application> findByCandidate(int candidateId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE candidate_id = ? ORDER BY applied_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Application> findByJob(int jobId) throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE job_id = ? ORDER BY applied_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /** Status change inside a transaction; history row is written by the service. */
    public boolean updateStatus(Connection con, int applicationId, String newStatus) throws SQLException {
        String sql = "UPDATE applications SET application_status = ? WHERE application_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateEligibility(Connection con, int applicationId,
                                     String eligibilityStatus, String reason) throws SQLException {
        String sql = "UPDATE applications SET eligibility_status = ?, eligibility_reason = ? "
                + "WHERE application_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, eligibilityStatus);
            ps.setString(2, reason);
            ps.setInt(3, applicationId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Audit trail: every status change writes one history row (Spec section 28). */
    public void insertHistory(Connection con, int applicationId, String oldStatus,
                              String newStatus, String remarks, int changedBy) throws SQLException {
        String sql = "INSERT INTO application_history "
                + "(application_id, old_status, new_status, remarks, changed_by) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ps.setString(2, oldStatus);
            ps.setString(3, newStatus);
            ps.setString(4, remarks);
            if (changedBy == 0) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, changedBy);
            }
            ps.executeUpdate();
        }
    }

    /** Report helper: count of applications grouped by status. */
    public Map<String, Integer> countByStatus() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT application_status, COUNT(*) AS total FROM applications "
                + "GROUP BY application_status ORDER BY total DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("application_status"), rs.getInt("total"));
            }
        }
        return counts;
    }

    public static Application map(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setApplicationId(rs.getInt("application_id"));
        a.setApplicationCode(rs.getString("application_code"));
        a.setCandidateId(rs.getInt("candidate_id"));
        a.setJobId(rs.getInt("job_id"));
        a.setAppliedAt(rs.getTimestamp("applied_at"));
        a.setEligibilityStatus(rs.getString("eligibility_status"));
        a.setEligibilityReason(rs.getString("eligibility_reason"));
        a.setApplicationStatus(rs.getString("application_status"));
        a.setSource(rs.getString("source"));
        a.setAssignedRecruiterId(rs.getInt("assigned_recruiter_id"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        return a;
    }
}
