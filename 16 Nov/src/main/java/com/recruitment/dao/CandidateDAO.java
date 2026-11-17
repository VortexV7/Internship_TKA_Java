package com.recruitment.dao;

import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.model.Candidate;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CandidateDAO - all SQL for the candidates table.
 * Every query uses PreparedStatement (? placeholders) - never string concatenation.
 */
public class CandidateDAO {

    /** INSERT - returns the generated candidate_id. */
    public int create(Candidate c) throws SQLException {
        String sql = "INSERT INTO candidates (candidate_code, full_name, email, mobile, "
                + "date_of_birth, gender, qualification, college, university, passing_year, "
                + "percentage, cgpa, backlog_count, primary_technology, current_location, "
                + "preferred_location, profile_status, placement_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, c);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /** INSERT inside an existing transaction (caller owns commit/rollback). */
    public int create(Connection con, Candidate c) throws SQLException {
        String sql = "INSERT INTO candidates (candidate_code, full_name, email, mobile, "
                + "date_of_birth, gender, qualification, college, university, passing_year, "
                + "percentage, cgpa, backlog_count, primary_technology, current_location, "
                + "preferred_location, profile_status, placement_status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, c);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private void setParams(PreparedStatement ps, Candidate c) throws SQLException {
        ps.setString(1, c.getCandidateCode());
        ps.setString(2, c.getFullName());
        ps.setString(3, c.getEmail());
        ps.setString(4, c.getMobile());
        ps.setDate(5, c.getDateOfBirth());
        ps.setString(6, c.getGender());
        ps.setString(7, c.getQualification());
        ps.setString(8, c.getCollege());
        ps.setString(9, c.getUniversity());
        ps.setInt(10, c.getPassingYear());
        ps.setDouble(11, c.getPercentage());
        ps.setDouble(12, c.getCgpa());
        ps.setInt(13, c.getBacklogCount());
        ps.setString(14, c.getPrimaryTechnology());
        ps.setString(15, c.getCurrentLocation());
        ps.setString(16, c.getPreferredLocation());
        ps.setString(17, c.getProfileStatus());
        ps.setString(18, c.getPlacementStatus());
    }

    public Candidate findById(int candidateId) throws SQLException, CandidateNotFoundException {
        String sql = "SELECT * FROM candidates WHERE candidate_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        throw new CandidateNotFoundException("Candidate not found with id " + candidateId);
    }

    public Candidate findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE candidate_code = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Candidate findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public Candidate findByMobile(String mobile) throws SQLException {
        String sql = "SELECT * FROM candidates WHERE mobile = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mobile);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Candidate> findAll() throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY candidate_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Candidate> findByTechnology(String technology) throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE primary_technology = ? ORDER BY full_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, technology);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Candidate> findPlaced() throws SQLException {
        return findByPlacementStatus("PLACED");
    }

    public List<Candidate> findUnplaced() throws SQLException {
        return findByPlacementStatus("NOT_PLACED");
    }

    public List<Candidate> findByPlacementStatus(String status) throws SQLException {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates WHERE placement_status = ? ORDER BY full_name";
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

    public boolean update(Candidate c) throws SQLException {
        String sql = "UPDATE candidates SET full_name=?, email=?, mobile=?, date_of_birth=?, "
                + "gender=?, qualification=?, college=?, university=?, passing_year=?, "
                + "percentage=?, cgpa=?, backlog_count=?, primary_technology=?, "
                + "current_location=?, preferred_location=?, profile_status=?, "
                + "placement_status=? WHERE candidate_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getMobile());
            ps.setDate(4, c.getDateOfBirth());
            ps.setString(5, c.getGender());
            ps.setString(6, c.getQualification());
            ps.setString(7, c.getCollege());
            ps.setString(8, c.getUniversity());
            ps.setInt(9, c.getPassingYear());
            ps.setDouble(10, c.getPercentage());
            ps.setDouble(11, c.getCgpa());
            ps.setInt(12, c.getBacklogCount());
            ps.setString(13, c.getPrimaryTechnology());
            ps.setString(14, c.getCurrentLocation());
            ps.setString(15, c.getPreferredLocation());
            ps.setString(16, c.getProfileStatus());
            ps.setString(17, c.getPlacementStatus());
            ps.setInt(18, c.getCandidateId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Soft-delete only (Spec section 23): deactivate, never DELETE the row. */
    public boolean deactivate(int candidateId) throws SQLException {
        String sql = "UPDATE candidates SET profile_status = 'INACTIVE' WHERE candidate_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            return ps.executeUpdate() > 0;
        }
    }

    /** Used inside the joining transaction. */
    public boolean updatePlacementStatus(Connection con, int candidateId, String status) throws SQLException {
        String sql = "UPDATE candidates SET placement_status = ? WHERE candidate_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, candidateId);
            return ps.executeUpdate() > 0;
        }
    }

    public static Candidate map(ResultSet rs) throws SQLException {
        Candidate c = new Candidate();
        c.setCandidateId(rs.getInt("candidate_id"));
        c.setCandidateCode(rs.getString("candidate_code"));
        c.setFullName(rs.getString("full_name"));
        c.setEmail(rs.getString("email"));
        c.setMobile(rs.getString("mobile"));
        c.setDateOfBirth(rs.getDate("date_of_birth"));
        c.setGender(rs.getString("gender"));
        c.setQualification(rs.getString("qualification"));
        c.setCollege(rs.getString("college"));
        c.setUniversity(rs.getString("university"));
        c.setPassingYear(rs.getInt("passing_year"));
        c.setPercentage(rs.getDouble("percentage"));
        c.setCgpa(rs.getDouble("cgpa"));
        c.setBacklogCount(rs.getInt("backlog_count"));
        c.setPrimaryTechnology(rs.getString("primary_technology"));
        c.setCurrentLocation(rs.getString("current_location"));
        c.setPreferredLocation(rs.getString("preferred_location"));
        c.setProfileStatus(rs.getString("profile_status"));
        c.setPlacementStatus(rs.getString("placement_status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
