package com.recruitment.service;

import com.recruitment.dao.CandidateDAO;
import com.recruitment.exception.CandidateNotFoundException;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Candidate;
import com.recruitment.util.ActivityLogger;
import com.recruitment.util.ValidationUtil;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * CandidateService - business rules for candidates (Spec section 5).
 * Duplicate email/mobile are blocked here AND by UNIQUE constraints in the DB.
 */
public class CandidateService {

    private final CandidateDAO candidateDAO = new CandidateDAO();

    public int registerCandidate(Candidate c) throws ValidationException, SQLException {
        ValidationUtil.requireNotBlank(c.getFullName(), "Full name");
        ValidationUtil.requireNotBlank(c.getEmail(), "Email");
        ValidationUtil.requireNotBlank(c.getMobile(), "Mobile");
        ValidationUtil.requireNotBlank(c.getQualification(), "Qualification");
        if (!ValidationUtil.isValidEmail(c.getEmail())) {
            throw new ValidationException("Invalid email format: " + c.getEmail());
        }
        if (!ValidationUtil.isValidMobile(c.getMobile())) {
            throw new ValidationException("Mobile must be exactly 10 digits.");
        }
        ValidationUtil.checkPercentage(c.getPercentage());
        ValidationUtil.checkBacklogs(c.getBacklogCount());
        if (c.getProfileStatus() == null || c.getProfileStatus().isBlank()) {
            c.setProfileStatus("ACTIVE");
        }
        if (c.getPlacementStatus() == null || c.getPlacementStatus().isBlank()) {
            c.setPlacementStatus("NOT_PLACED");
        }
        // Duplicate checks before insert (DB UNIQUE constraint is the second line of defence).
        if (candidateDAO.findByEmail(c.getEmail()) != null) {
            throw new ValidationException("Duplicate email - a candidate with this email already exists.");
        }
        if (candidateDAO.findByMobile(c.getMobile()) != null) {
            throw new ValidationException("Duplicate mobile - a candidate with this mobile already exists.");
        }
        try {
            int id = candidateDAO.create(c);
            c.setCandidateId(id);
            ActivityLogger.log("Candidate Created", c.getCandidateCode());
            return id;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new ValidationException("Duplicate email or mobile - candidate already exists.");
        }
    }

    public Candidate getCandidate(int candidateId) throws SQLException, CandidateNotFoundException {
        return candidateDAO.findById(candidateId);
    }

    public List<Candidate> getAllCandidates() throws SQLException {
        return candidateDAO.findAll();
    }

    public List<Candidate> getCandidatesByTechnology(String technology) throws SQLException {
        return candidateDAO.findByTechnology(technology);
    }

    public List<Candidate> getPlacedCandidates() throws SQLException {
        return candidateDAO.findPlaced();
    }

    public List<Candidate> getUnplacedCandidates() throws SQLException {
        return candidateDAO.findUnplaced();
    }

    public boolean updateCandidate(Candidate c) throws ValidationException, SQLException {
        ValidationUtil.checkPercentage(c.getPercentage());
        ValidationUtil.checkBacklogs(c.getBacklogCount());
        boolean ok = candidateDAO.update(c);
        if (ok) {
            ActivityLogger.log("Candidate Updated", c.getCandidateCode());
        }
        return ok;
    }

    /** Soft-delete: deactivate, never hard-delete (Spec section 23). */
    public boolean deactivateCandidate(int candidateId) throws SQLException {
        boolean ok = candidateDAO.deactivate(candidateId);
        if (ok) {
            ActivityLogger.log("Candidate Deactivated", String.valueOf(candidateId));
        }
        return ok;
    }
}
