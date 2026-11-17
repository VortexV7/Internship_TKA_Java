package com.recruitment.model;

/** Result of the Eligibility Engine: ELIGIBLE / NOT ELIGIBLE plus a clear reason. */
public class EligibilityResult {
    private boolean eligible;
    private String reason;

    public EligibilityResult(boolean eligible, String reason) {
        this.eligible = eligible;
        this.reason = reason;
    }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    /** Spec wording: "ELIGIBLE" or "NOT ELIGIBLE". */
    public String getStatus() { return eligible ? "ELIGIBLE" : "NOT ELIGIBLE"; }

    @Override
    public String toString() {
        return "EligibilityResult{" + getStatus() + ": " + reason + "}";
    }
}
