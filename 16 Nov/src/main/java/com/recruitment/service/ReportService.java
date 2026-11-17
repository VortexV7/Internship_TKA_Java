package com.recruitment.service;

import com.recruitment.dao.CandidateDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.dao.ReportDAO;
import com.recruitment.model.Candidate;
import com.recruitment.model.Job;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/** ReportService - thin layer over ReportDAO (Spec section 14 + DB Spec section 26). */
public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final JobDAO jobDAO = new JobDAO();

    public Map<String, Integer> candidatesByTechnology() throws SQLException {
        return reportDAO.candidatesByTechnology();
    }

    public List<String[]> openJobsByCompany() throws SQLException {
        return reportDAO.openJobsByCompany();
    }

    public Map<String, Integer> applicationsByStatus() throws SQLException {
        return reportDAO.applicationsByStatus();
    }

    public Map<String, Integer> recruitmentFunnel() throws SQLException {
        return reportDAO.recruitmentFunnel();
    }

    public Map<String, Integer> companyWisePlacements() throws SQLException {
        return reportDAO.companyWisePlacements();
    }

    public Map<String, Integer> placementsByTechnology() throws SQLException {
        return reportDAO.placementsByTechnology();
    }

    public List<String[]> recruiterPerformance() throws SQLException {
        return reportDAO.recruiterPerformance();
    }

    public List<String[]> pendingInterviews() throws SQLException {
        return reportDAO.pendingInterviews();
    }

    public String[] offerToJoiningConversion() throws SQLException {
        return reportDAO.offerToJoiningConversion();
    }

    public List<String[]> jobAging() throws SQLException {
        return reportDAO.jobAging();
    }

    public List<Candidate> placedCandidates() throws SQLException {
        return candidateDAO.findPlaced();
    }

    public List<Candidate> unplacedCandidates() throws SQLException {
        return candidateDAO.findUnplaced();
    }

    public List<Job> openJobs() throws SQLException {
        return jobDAO.findOpenJobs();
    }
}
