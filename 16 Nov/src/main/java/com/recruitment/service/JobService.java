package com.recruitment.service;

import com.recruitment.dao.CompanyDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Job;
import com.recruitment.util.ActivityLogger;
import com.recruitment.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/** JobService - business rules for jobs (Spec sections 6 and 29). */
public class JobService {

    private final JobDAO jobDAO = new JobDAO();
    private final CompanyDAO companyDAO = new CompanyDAO();

    public int createJob(Job job) throws ValidationException, SQLException {
        ValidationUtil.requireNotBlank(job.getJobTitle(), "Job title");
        ValidationUtil.requireNotBlank(job.getLocation(), "Location");
        ValidationUtil.checkOpenings(job.getOpenings());
        ValidationUtil.checkPackageRange(job.getMinPackageLpa(), job.getMaxPackageLpa());
        if (job.getMinPercentage() < 0 || job.getMinPercentage() > 100) {
            throw new ValidationException("Minimum percentage must be between 0 and 100.");
        }
        if (job.getMaxBacklogs() < 0) {
            throw new ValidationException("Maximum backlogs cannot be negative.");
        }
        if (job.getPostedDate() != null && job.getApplicationDeadline() != null
                && job.getApplicationDeadline().before(job.getPostedDate())) {
            throw new ValidationException("Application deadline cannot be before the posted date.");
        }
        // Job must reference an existing company (FK also enforces this).
        if (companyDAO.findById(job.getCompanyId()) == null) {
            throw new ValidationException("Company not found with id " + job.getCompanyId());
        }
        if (job.getStatus() == null || job.getStatus().isBlank()) {
            job.setStatus("OPEN");
        }
        if (job.getEmploymentType() == null || job.getEmploymentType().isBlank()) {
            job.setEmploymentType("FULL_TIME");
        }
        int id = jobDAO.create(job);
        job.setJobId(id);
        ActivityLogger.log("Job Created", job.getJobCode());
        return id;
    }

    public Job getJob(int jobId) throws SQLException, JobNotFoundException {
        return jobDAO.findById(jobId);
    }

    public List<Job> getAllJobs() throws SQLException {
        return jobDAO.findAll();
    }

    public List<Job> getOpenJobs() throws SQLException {
        return jobDAO.findOpenJobs();
    }

    public List<Job> searchJobs(String keyword) throws SQLException {
        return jobDAO.searchByTitle(keyword);
    }

    public boolean updateJob(Job job) throws ValidationException, SQLException {
        ValidationUtil.checkOpenings(job.getOpenings());
        ValidationUtil.checkPackageRange(job.getMinPackageLpa(), job.getMaxPackageLpa());
        return jobDAO.update(job);
    }

    /** Close a job (CLOSED) - never hard-delete (Spec section 23). */
    public boolean closeJob(int jobId) throws SQLException {
        boolean ok = jobDAO.closeJob(jobId);
        if (ok) {
            ActivityLogger.log("Job Closed", String.valueOf(jobId));
        }
        return ok;
    }
}
