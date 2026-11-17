package com.recruitment.dao;

import com.recruitment.model.Company;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** CompanyDAO - all SQL for the companies table (PreparedStatement only). */
public class CompanyDAO {

    public int create(Company c) throws SQLException {
        String sql = "INSERT INTO companies (company_code, company_name, industry, website, "
                + "location, company_type, hr_name, hr_email, hr_mobile, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCompanyCode());
            ps.setString(2, c.getCompanyName());
            ps.setString(3, c.getIndustry());
            ps.setString(4, c.getWebsite());
            ps.setString(5, c.getLocation());
            ps.setString(6, c.getCompanyType());
            ps.setString(7, c.getHrName());
            ps.setString(8, c.getHrEmail());
            ps.setString(9, c.getHrMobile());
            ps.setString(10, c.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Company findById(int companyId) throws SQLException {
        String sql = "SELECT * FROM companies WHERE company_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public List<Company> findAll() throws SQLException {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies ORDER BY company_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Company> findActive() throws SQLException {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT * FROM companies WHERE status = 'ACTIVE' ORDER BY company_name";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public boolean update(Company c) throws SQLException {
        String sql = "UPDATE companies SET company_name=?, industry=?, website=?, location=?, "
                + "company_type=?, hr_name=?, hr_email=?, hr_mobile=?, status=? WHERE company_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCompanyName());
            ps.setString(2, c.getIndustry());
            ps.setString(3, c.getWebsite());
            ps.setString(4, c.getLocation());
            ps.setString(5, c.getCompanyType());
            ps.setString(6, c.getHrName());
            ps.setString(7, c.getHrEmail());
            ps.setString(8, c.getHrMobile());
            ps.setString(9, c.getStatus());
            ps.setInt(10, c.getCompanyId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Soft-delete only: set INACTIVE rather than delete if jobs exist (Spec section 23). */
    public boolean deactivate(int companyId) throws SQLException {
        String sql = "UPDATE companies SET status = 'INACTIVE' WHERE company_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            return ps.executeUpdate() > 0;
        }
    }

    public static Company map(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setCompanyId(rs.getInt("company_id"));
        c.setCompanyCode(rs.getString("company_code"));
        c.setCompanyName(rs.getString("company_name"));
        c.setIndustry(rs.getString("industry"));
        c.setWebsite(rs.getString("website"));
        c.setLocation(rs.getString("location"));
        c.setCompanyType(rs.getString("company_type"));
        c.setHrName(rs.getString("hr_name"));
        c.setHrEmail(rs.getString("hr_email"));
        c.setHrMobile(rs.getString("hr_mobile"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
