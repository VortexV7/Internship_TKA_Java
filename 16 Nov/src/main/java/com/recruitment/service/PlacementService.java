package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.CandidateDAO;
import com.recruitment.dao.OfferDAO;
import com.recruitment.dao.PlacementDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Offer;
import com.recruitment.model.Placement;
import com.recruitment.util.ActivityLogger;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * PlacementService - joining / placement results.
 * Joining transaction (Spec section 28):
 * create placement -> update candidate placement status -> update application
 * -> insert application history. Any failure rolls the whole thing back.
 */
public class PlacementService {

    private final PlacementDAO placementDAO = new PlacementDAO();
    private final OfferDAO offerDAO = new OfferDAO();
    private final CandidateDAO candidateDAO = new CandidateDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public int confirmJoining(Placement placement, int changedBy)
            throws ValidationException, SQLException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            Offer offer = offerDAO.findById(placement.getOfferId());
            if (offer == null) {
                throw new ValidationException(
                        "Placement must reference a valid offer (offer id "
                        + placement.getOfferId() + " not found).");
            }
            if (!"ACCEPTED".equals(offer.getOfferStatus())) {
                throw new ValidationException("Offer must be ACCEPTED before joining can be confirmed "
                        + "(current status: " + offer.getOfferStatus() + ").");
            }
            if (placementDAO.findByOffer(placement.getOfferId()) != null) {
                throw new ValidationException("A placement already exists for this offer.");
            }
            if (placement.getJoiningStatus() == null || placement.getJoiningStatus().isBlank()) {
                placement.setJoiningStatus("JOINED");
            }
            if (placement.getPlacementStatus() == null || placement.getPlacementStatus().isBlank()) {
                placement.setPlacementStatus("ACTIVE");
            }
            int id = placementDAO.create(con, placement);
            // Candidate becomes PLACED only when actually joined.
            if ("JOINED".equals(placement.getJoiningStatus())) {
                candidateDAO.updatePlacementStatus(con, placement.getCandidateId(), "PLACED");
                applicationDAO.updateStatus(con, offer.getApplicationId(), "JOINED");
                applicationDAO.insertHistory(con, offer.getApplicationId(),
                        "OFFERED", "JOINED",
                        "Candidate joined " + placement.getDesignation(), changedBy);
            }
            con.commit();
            ActivityLogger.log("Placement Created",
                    "Candidate " + placement.getCandidateId() + " - " + placement.getDesignation());
            return id;
        } catch (SQLException | ValidationException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ignored) { /* ignore */ }
            }
            throw e;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ignored) { /* ignore */ }
            }
        }
    }

    public List<Placement> getAllPlacements() throws SQLException {
        return placementDAO.findAll();
    }

    public List<Placement> getJoinedPlacements() throws SQLException {
        return placementDAO.findJoined();
    }

    public Map<String, Integer> getCompanyWisePlacements() throws SQLException {
        return placementDAO.countByCompany();
    }
}
