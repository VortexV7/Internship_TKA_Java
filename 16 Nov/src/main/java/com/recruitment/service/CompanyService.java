package com.recruitment.service;

import com.recruitment.dao.CompanyDAO;
import com.recruitment.exception.ValidationException;
import com.recruitment.model.Company;
import com.recruitment.util.ActivityLogger;
import com.recruitment.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

/** CompanyService - business rules for companies. */
public class CompanyService {

    private final CompanyDAO companyDAO = new CompanyDAO();

    public int addCompany(Company c) throws ValidationException, SQLException {
        ValidationUtil.requireNotBlank(c.getCompanyName(), "Company name");
        ValidationUtil.requireNotBlank(c.getCompanyCode(), "Company code");
        if (c.getStatus() == null || c.getStatus().isBlank()) {
            c.setStatus("ACTIVE");
        }
        int id = companyDAO.create(c);
        c.setCompanyId(id);
        ActivityLogger.log("Company Created", c.getCompanyCode());
        return id;
    }

    public Company getCompany(int companyId) throws SQLException {
        return companyDAO.findById(companyId);
    }

    public List<Company> getAllCompanies() throws SQLException {
        return companyDAO.findAll();
    }

    public List<Company> getActiveCompanies() throws SQLException {
        return companyDAO.findActive();
    }

    public boolean updateCompany(Company c) throws SQLException {
        return companyDAO.update(c);
    }

    /** Soft-delete: set INACTIVE rather than delete if jobs exist (Spec section 23). */
    public boolean deactivateCompany(int companyId) throws SQLException {
        return companyDAO.deactivate(companyId);
    }
}
