package com.recruitment.service;

import com.recruitment.dao.ApplicationDAO;
import com.recruitment.dao.OfferDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Application;
import com.recruitment.model.Offer;
import com.recruitment.util.ActivityLogger;
import com.recruitment.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * OfferService - creating offers for SELECTED applications.
 * Selection transaction (Spec section 28):
 * create offer -> update application status -> insert application history.
 * Only one offer per application (UNIQUE(application_id) in the schema).
 */
public class OfferService {

    private final OfferDAO offerDAO = new OfferDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public int createOffer(Offer offer, int changedBy) throws ValidationException, SQLException {
        if (offer.getOfferedPackageLpa() <= 0) {
            throw new ValidationException("Offered package must be greater than zero.");
        }
        if (offer.getOfferDate() == null) {
            throw new ValidationException("Offer date is required.");
        }
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);
            Application app = applicationDAO.findById(con, offer.getApplicationId());
            if (app == null) {
                throw new ValidationException(
                        "Application not found with id " + offer.getApplicationId() + " - offer rejected.");
            }
            if (!"SELECTED".equals(app.getApplicationStatus())) {
                throw new ValidationException("Offer can only be created for a SELECTED application "
                        + "(current status: " + app.getApplicationStatus() + ").");
            }
            if (offerDAO.findByApplication(offer.getApplicationId()) != null) {
                throw new ValidationException("An offer already exists for this application.");
            }
            if (offer.getOfferStatus() == null || offer.getOfferStatus().isBlank()) {
                offer.setOfferStatus("OFFERED");
            }
            int id = offerDAO.create(con, offer);
            applicationDAO.updateStatus(con, offer.getApplicationId(), "OFFERED");
            applicationDAO.insertHistory(con, offer.getApplicationId(),
                    "SELECTED", "OFFERED", "Offer " + offer.getOfferCode() + " created", changedBy);
            con.commit();
            ActivityLogger.log("Offer Created", offer.getOfferCode());
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

    public boolean updateOfferStatus(int offerId, String status) throws SQLException {
        boolean ok = offerDAO.updateStatus(offerId, status);
        if (ok) {
            ActivityLogger.log("Offer Status Changed", "Offer " + offerId + " -> " + status);
        }
        return ok;
    }

    public Offer getOffer(int offerId) throws SQLException {
        return offerDAO.findById(offerId);
    }

    public Offer getOfferByApplication(int applicationId) throws SQLException {
        return offerDAO.findByApplication(applicationId);
    }

    public List<Offer> getAllOffers() throws SQLException {
        return offerDAO.findAll();
    }

    public List<Offer> getOffersByStatus(String status) throws SQLException {
        return offerDAO.findByStatus(status);
    }
}
