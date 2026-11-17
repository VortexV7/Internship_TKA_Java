package com.recruitment.dao;

import com.recruitment.model.Placement;
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

/** PlacementDAO - SQL for the placements table. UNIQUE(offer_id): one placement per offer. */
public class PlacementDAO {

    public int create(Connection con, Placement p) throws SQLException {
        String sql = "INSERT INTO placements (candidate_id, company_id, job_id, offer_id, "
                + "designation, final_package_lpa, expected_joining_date, actual_joining_date, "
                + "joining_status, placement_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getCandidateId());
            ps.setInt(2, p.getCompanyId());
            ps.setInt(3, p.getJobId());
            ps.setInt(4, p.getOfferId());
            ps.setString(5, p.getDesignation());
            ps.setDouble(6, p.getFinalPackageLpa());
            ps.setDate(7, p.getExpectedJoiningDate());
            ps.setDate(8, p.getActualJoiningDate());
            ps.setString(9, p.getJoiningStatus());
            ps.setString(10, p.getPlacementStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Placement findById(int placementId) throws SQLException {
        String sql = "SELECT * FROM placements WHERE placement_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, placementId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Placement findByOffer(int offerId) throws SQLException {
        String sql = "SELECT * FROM placements WHERE offer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Placement> findAll() throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = "SELECT * FROM placements ORDER BY created_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Placement> findJoined() throws SQLException {
        List<Placement> list = new ArrayList<>();
        String sql = "SELECT * FROM placements WHERE joining_status = 'JOINED' ORDER BY actual_joining_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /** Company-wise placements (Spec section 27 sample query). */
    public Map<String, Integer> countByCompany() throws SQLException {
        Map<String, Integer> counts = new LinkedHashMap<>();
        String sql = "SELECT c.company_name, COUNT(p.placement_id) AS placed_count "
                + "FROM placements p JOIN companies c ON c.company_id = p.company_id "
                + "WHERE p.joining_status = 'JOINED' "
                + "GROUP BY c.company_id, c.company_name ORDER BY placed_count DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("company_name"), rs.getInt("placed_count"));
            }
        }
        return counts;
    }

    public boolean updateJoiningStatus(Connection con, int placementId, String status,
                                       java.sql.Date actualJoiningDate) throws SQLException {
        String sql = "UPDATE placements SET joining_status = ?, actual_joining_date = ? "
                + "WHERE placement_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setDate(2, actualJoiningDate);
            ps.setInt(3, placementId);
            return ps.executeUpdate() > 0;
        }
    }

    public static Placement map(ResultSet rs) throws SQLException {
        Placement p = new Placement();
        p.setPlacementId(rs.getInt("placement_id"));
        p.setCandidateId(rs.getInt("candidate_id"));
        p.setCompanyId(rs.getInt("company_id"));
        p.setJobId(rs.getInt("job_id"));
        p.setOfferId(rs.getInt("offer_id"));
        p.setDesignation(rs.getString("designation"));
        p.setFinalPackageLpa(rs.getDouble("final_package_lpa"));
        p.setExpectedJoiningDate(rs.getDate("expected_joining_date"));
        p.setActualJoiningDate(rs.getDate("actual_joining_date"));
        p.setJoiningStatus(rs.getString("joining_status"));
        p.setPlacementStatus(rs.getString("placement_status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
