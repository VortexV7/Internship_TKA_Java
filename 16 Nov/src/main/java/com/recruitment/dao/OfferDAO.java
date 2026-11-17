package com.recruitment.dao;

import com.recruitment.model.Offer;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** OfferDAO - SQL for the offers table. UNIQUE(application_id): one offer per application. */
public class OfferDAO {

    public int create(Connection con, Offer offer) throws SQLException {
        String sql = "INSERT INTO offers (offer_code, application_id, designation, "
                + "offered_package_lpa, offer_date, expected_joining_date, offer_status, remarks) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, offer.getOfferCode());
            ps.setInt(2, offer.getApplicationId());
            ps.setString(3, offer.getDesignation());
            ps.setDouble(4, offer.getOfferedPackageLpa());
            ps.setDate(5, offer.getOfferDate());
            ps.setDate(6, offer.getExpectedJoiningDate());
            ps.setString(7, offer.getOfferStatus());
            ps.setString(8, offer.getRemarks());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Offer findById(int offerId) throws SQLException {
        String sql = "SELECT * FROM offers WHERE offer_id = ?";
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

    public Offer findByApplication(int applicationId) throws SQLException {
        String sql = "SELECT * FROM offers WHERE application_id = ?";
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

    public List<Offer> findAll() throws SQLException {
        List<Offer> list = new ArrayList<>();
        String sql = "SELECT * FROM offers ORDER BY offer_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Offer> findByStatus(String status) throws SQLException {
        List<Offer> list = new ArrayList<>();
        String sql = "SELECT * FROM offers WHERE offer_status = ? ORDER BY offer_date DESC";
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

    public boolean updateStatus(int offerId, String status) throws SQLException {
        String sql = "UPDATE offers SET offer_status = ? WHERE offer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, offerId);
            return ps.executeUpdate() > 0;
        }
    }

    public static Offer map(ResultSet rs) throws SQLException {
        Offer o = new Offer();
        o.setOfferId(rs.getInt("offer_id"));
        o.setOfferCode(rs.getString("offer_code"));
        o.setApplicationId(rs.getInt("application_id"));
        o.setDesignation(rs.getString("designation"));
        o.setOfferedPackageLpa(rs.getDouble("offered_package_lpa"));
        o.setOfferDate(rs.getDate("offer_date"));
        o.setExpectedJoiningDate(rs.getDate("expected_joining_date"));
        o.setOfferStatus(rs.getString("offer_status"));
        o.setRemarks(rs.getString("remarks"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
