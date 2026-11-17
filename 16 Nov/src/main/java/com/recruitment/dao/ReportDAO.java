package com.recruitment.dao;

import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ReportDAO - the reporting queries from DB Spec section 26.
 * Each method returns rows as "label -> count" or as String[] rows for the console UI.
 */
public class ReportDAO {

    /** 1. Candidate count by technology - GROUP BY primary_technology. */
    public Map<String, Integer> candidatesByTechnology() throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT primary_technology, COUNT(*) AS total FROM candidates "
                + "GROUP BY primary_technology ORDER BY total DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("primary_technology"), rs.getInt("total"));
            }
        }
        return result;
    }

    /** 2. Open jobs by company - JOIN jobs + companies, filter OPEN. */
    public List<String[]> openJobsByCompany() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT c.company_name, j.job_title, j.location, j.openings, "
                + "j.min_package_lpa, j.max_package_lpa, j.application_deadline "
                + "FROM jobs j JOIN companies c ON c.company_id = j.company_id "
                + "WHERE j.status = 'OPEN' ORDER BY c.company_name, j.job_title";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("company_name"), rs.getString("job_title"),
                        rs.getString("location"), String.valueOf(rs.getInt("openings")),
                        rs.getDouble("min_package_lpa") + " - " + rs.getDouble("max_package_lpa"),
                        String.valueOf(rs.getDate("application_deadline"))});
            }
        }
        return rows;
    }

    /** 3. Applications by status - GROUP BY application_status. */
    public Map<String, Integer> applicationsByStatus() throws SQLException {
        return new ApplicationDAO().countByStatus();
    }

    /** 4. Recruitment funnel - counts of the major application stages. */
    public Map<String, Integer> recruitmentFunnel() throws SQLException {
        Map<String, Integer> funnel = new LinkedHashMap<>();
        String sql = "SELECT application_status, COUNT(*) AS total FROM applications "
                + "WHERE application_status IN "
                + "('APPLIED','SHORTLISTED','INTERVIEW_SCHEDULED','INTERVIEWED','SELECTED','OFFERED','JOINED') "
                + "GROUP BY application_status";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                funnel.put(rs.getString("application_status"), rs.getInt("total"));
            }
        }
        return funnel;
    }

    /** 5. Company-wise placements (Spec section 27). */
    public Map<String, Integer> companyWisePlacements() throws SQLException {
        return new PlacementDAO().countByCompany();
    }

    /** 6. Technology-wise placements - JOIN candidates + placements. */
    public Map<String, Integer> placementsByTechnology() throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT c.primary_technology, COUNT(p.placement_id) AS placed_count "
                + "FROM placements p JOIN candidates c ON c.candidate_id = p.candidate_id "
                + "WHERE p.joining_status = 'JOINED' "
                + "GROUP BY c.primary_technology ORDER BY placed_count DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("primary_technology"), rs.getInt("placed_count"));
            }
        }
        return result;
    }

    /** 7. Recruiter performance - GROUP BY assigned_recruiter_id. */
    public List<String[]> recruiterPerformance() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT u.full_name, COUNT(a.application_id) AS total_applications, "
                + "SUM(CASE WHEN a.application_status IN ('SELECTED','OFFERED','JOINED') THEN 1 ELSE 0 END) AS successful "
                + "FROM applications a LEFT JOIN users u ON u.user_id = a.assigned_recruiter_id "
                + "GROUP BY u.user_id, u.full_name ORDER BY total_applications DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{rs.getString("full_name"),
                        String.valueOf(rs.getInt("total_applications")),
                        String.valueOf(rs.getInt("successful"))});
            }
        }
        return rows;
    }

    /** 8. Pending interviews - filter SCHEDULED. */
    public List<String[]> pendingInterviews() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT ir.interview_id, ir.round_number, ir.round_type, ir.scheduled_date, "
                + "ir.scheduled_time, ir.mode, ir.interviewer_name "
                + "FROM interview_rounds ir WHERE ir.status = 'SCHEDULED' "
                + "ORDER BY ir.scheduled_date, ir.scheduled_time";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{String.valueOf(rs.getInt("interview_id")),
                        String.valueOf(rs.getInt("round_number")), rs.getString("round_type"),
                        String.valueOf(rs.getDate("scheduled_date")),
                        String.valueOf(rs.getTime("scheduled_time")),
                        rs.getString("mode"), rs.getString("interviewer_name")});
            }
        }
        return rows;
    }

    /** 9. Offer-to-joining conversion - offers vs JOINED placements. */
    public String[] offerToJoiningConversion() throws SQLException {
        String sql = "SELECT (SELECT COUNT(*) FROM offers) AS total_offers, "
                + "(SELECT COUNT(*) FROM offers WHERE offer_status = 'ACCEPTED') AS accepted_offers, "
                + "(SELECT COUNT(*) FROM placements WHERE joining_status = 'JOINED') AS joined";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new String[]{String.valueOf(rs.getInt("total_offers")),
                        String.valueOf(rs.getInt("accepted_offers")),
                        String.valueOf(rs.getInt("joined"))};
            }
        }
        return new String[]{"0", "0", "0"};
    }

    /** 10. Job aging - DATEDIFF(CURDATE(), posted_date). */
    public List<String[]> jobAging() throws SQLException {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT job_code, job_title, posted_date, "
                + "DATEDIFF(CURDATE(), posted_date) AS age_days, status "
                + "FROM jobs ORDER BY age_days DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{rs.getString("job_code"), rs.getString("job_title"),
                        String.valueOf(rs.getDate("posted_date")),
                        String.valueOf(rs.getInt("age_days")), rs.getString("status")});
            }
        }
        return rows;
    }
}
