package com.recruitment.dao;

import com.recruitment.exception.JobNotFoundException;
import com.recruitment.model.Job;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** JobDAO - all SQL for the jobs table (PreparedStatement only). */
public class JobDAO {

    public int create(Job j) throws SQLException {
        String sql = "INSERT INTO jobs (job_code, company_id, job_title, employment_type, "
                + "location, openings, min_package_lpa, max_package_lpa, min_percentage, "
                + "min_passing_year, max_passing_year, max_backlogs, experience_min_years, "
                + "qualification_rule, posted_date, application_deadline, status, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, j);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    private void setParams(PreparedStatement ps, Job j) throws SQLException {
        ps.setString(1, j.getJobCode());
        ps.setInt(2, j.getCompanyId());
        ps.setString(3, j.getJobTitle());
        ps.setString(4, j.getEmploymentType());
        ps.setString(5, j.getLocation());
        ps.setInt(6, j.getOpenings());
        ps.setDouble(7, j.getMinPackageLpa());
        ps.setDouble(8, j.getMaxPackageLpa());
        ps.setDouble(9, j.getMinPercentage());
        ps.setInt(10, j.getMinPassingYear());
        ps.setInt(11, j.getMaxPassingYear());
        ps.setInt(12, j.getMaxBacklogs());
        ps.setDouble(13, j.getExperienceMinYears());
        ps.setString(14, j.getQualificationRule());
        ps.setDate(15, j.getPostedDate());
        ps.setDate(16, j.getApplicationDeadline());
        ps.setString(17, j.getStatus());
        ps.setInt(18, j.getCreatedBy());
    }

    public Job findById(int jobId) throws SQLException, JobNotFoundException {
        String sql = "SELECT * FROM jobs WHERE job_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        throw new JobNotFoundException("Job not found with id " + jobId);
    }

    /** Same lookup inside a transaction (no new connection). */
    public Job findById(Connection con, int jobId) throws SQLException, JobNotFoundException {
        String sql = "SELECT * FROM jobs WHERE job_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        throw new JobNotFoundException("Job not found with id " + jobId);
    }

    public List<Job> findAll() throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs ORDER BY posted_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Job> findOpenJobs() throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE status = 'OPEN' ORDER BY application_deadline";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public List<Job> findByCompany(int companyId) throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE company_id = ? ORDER BY posted_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Job> searchByTitle(String keyword) throws SQLException {
        List<Job> list = new ArrayList<>();
        String sql = "SELECT * FROM jobs WHERE job_title LIKE ? ORDER BY posted_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public boolean update(Job j) throws SQLException {
        String sql = "UPDATE jobs SET job_title=?, employment_type=?, location=?, openings=?, "
                + "min_package_lpa=?, max_package_lpa=?, min_percentage=?, min_passing_year=?, "
                + "max_passing_year=?, max_backlogs=?, experience_min_years=?, "
                + "qualification_rule=?, posted_date=?, application_deadline=?, status=? "
                + "WHERE job_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, j.getJobTitle());
            ps.setString(2, j.getEmploymentType());
            ps.setString(3, j.getLocation());
            ps.setInt(4, j.getOpenings());
            ps.setDouble(5, j.getMinPackageLpa());
            ps.setDouble(6, j.getMaxPackageLpa());
            ps.setDouble(7, j.getMinPercentage());
            ps.setInt(8, j.getMinPassingYear());
            ps.setInt(9, j.getMaxPassingYear());
            ps.setInt(10, j.getMaxBacklogs());
            ps.setDouble(11, j.getExperienceMinYears());
            ps.setString(12, j.getQualificationRule());
            ps.setDate(13, j.getPostedDate());
            ps.setDate(14, j.getApplicationDeadline());
            ps.setString(15, j.getStatus());
            ps.setInt(16, j.getJobId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Close a job (CLOSED) - soft status change, never DELETE (Spec section 23). */
    public boolean closeJob(int jobId) throws SQLException {
        String sql = "UPDATE jobs SET status = 'CLOSED' WHERE job_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            return ps.executeUpdate() > 0;
        }
    }

    public static Job map(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setJobId(rs.getInt("job_id"));
        j.setJobCode(rs.getString("job_code"));
        j.setCompanyId(rs.getInt("company_id"));
        j.setJobTitle(rs.getString("job_title"));
        j.setEmploymentType(rs.getString("employment_type"));
        j.setLocation(rs.getString("location"));
        j.setOpenings(rs.getInt("openings"));
        j.setMinPackageLpa(rs.getDouble("min_package_lpa"));
        j.setMaxPackageLpa(rs.getDouble("max_package_lpa"));
        j.setMinPercentage(rs.getDouble("min_percentage"));
        j.setMinPassingYear(rs.getInt("min_passing_year"));
        j.setMaxPassingYear(rs.getInt("max_passing_year"));
        j.setMaxBacklogs(rs.getInt("max_backlogs"));
        j.setExperienceMinYears(rs.getDouble("experience_min_years"));
        j.setQualificationRule(rs.getString("qualification_rule"));
        j.setPostedDate(rs.getDate("posted_date"));
        j.setApplicationDeadline(rs.getDate("application_deadline"));
        j.setStatus(rs.getString("status"));
        j.setCreatedBy(rs.getInt("created_by"));
        j.setCreatedAt(rs.getTimestamp("created_at"));
        return j;
    }
}
