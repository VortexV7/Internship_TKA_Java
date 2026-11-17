package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.CandidateDAO;
import com.recruitment.dao.JobDAO;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.JobNotFoundException;
import com.recruitment.model.Candidate;
import com.recruitment.model.EligibilityResult;
import com.recruitment.model.Job;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Eligibility Engine (Spec section 7).
 * Checks run IN THIS ORDER and the first failure decides the reason:
 *   1. Candidate is active
 *   2. Job is open
 *   3. Application deadline is valid
 *   4. Candidate has not already applied
 *   5. Percentage criteria
 *   6. Passing-year criteria
 *   7. Backlog criteria
 * Result: ELIGIBLE or NOT ELIGIBLE with a clear reason.
 */
public class EligibilityService {

    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public EligibilityResult checkEligibility(int candidateId, int jobId)
            throws SQLException, CandidateNotFoundException, JobNotFoundException {
        Candidate candidate = candidateDAO.findById(candidateId);
        Job job = jobDAO.findById(jobId);

        // 1. Candidate is active
        if (!"ACTIVE".equalsIgnoreCase(candidate.getProfileStatus())) {
            return new EligibilityResult(false,
                    "Candidate profile is " + candidate.getProfileStatus() + " (must be ACTIVE).");
        }
        // 2. Job is open
        if (!"OPEN".equalsIgnoreCase(job.getStatus())) {
            return new EligibilityResult(false,
                    "Job is " + job.getStatus() + " (must be OPEN).");
        }
        // 3. Application deadline is valid
        if (job.getApplicationDeadline() != null
                && job.getApplicationDeadline().toLocalDate().isBefore(LocalDate.now())) {
            return new EligibilityResult(false,
                    "Application deadline " + job.getApplicationDeadline() + " has passed.");
        }
        // 4. Candidate has not already applied
        if (applicationDAO.existsByCandidateAndJob(candidateId, jobId)) {
            return new EligibilityResult(false,
                    "Candidate has already applied for this job.");
        }
        // 5. Percentage criteria
        if (candidate.getPercentage() < job.getMinPercentage()) {
            return new EligibilityResult(false,
                    "Percentage " + candidate.getPercentage()
                    + " is below the required minimum " + job.getMinPercentage() + ".");
        }
        // 6. Passing-year criteria
        if (job.getMinPassingYear() > 0 && candidate.getPassingYear() < job.getMinPassingYear()) {
            return new EligibilityResult(false,
                    "Passing year " + candidate.getPassingYear()
                    + " is before the required minimum " + job.getMinPassingYear() + ".");
        }
        if (job.getMaxPassingYear() > 0 && candidate.getPassingYear() > job.getMaxPassingYear()) {
            return new EligibilityResult(false,
                    "Passing year " + candidate.getPassingYear()
                    + " is after the allowed maximum " + job.getMaxPassingYear() + ".");
        }
        // 7. Backlog criteria
        if (candidate.getBacklogCount() > job.getMaxBacklogs()) {
            return new EligibilityResult(false,
                    "Backlog count " + candidate.getBacklogCount()
                    + " exceeds the allowed maximum " + job.getMaxBacklogs() + ".");
        }
        // Optional: technology match against the candidate's primary technology.
        return new EligibilityResult(true,
                "All eligibility criteria satisfied for job " + job.getJobCode() + ".");
    }
}
