package com.recruitment.model;

import java.util.Objects;

/**
 * InterviewFeedback - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class InterviewFeedback {
    private int feedbackId;
    private int interviewId;
    private double technicalRating;
    private double communicationRating;
    private double problemSolvingRating;
    private double overallRating;
    private String recommendation;
    private String remarks;
    private int submittedBy;
    private java.sql.Timestamp submittedAt;

    public InterviewFeedback() {}

    public InterviewFeedback(int feedbackId, int interviewId, double technicalRating, double communicationRating, double problemSolvingRating, double overallRating, String recommendation, String remarks, int submittedBy, java.sql.Timestamp submittedAt) {
        this.feedbackId = feedbackId;
        this.interviewId = interviewId;
        this.technicalRating = technicalRating;
        this.communicationRating = communicationRating;
        this.problemSolvingRating = problemSolvingRating;
        this.overallRating = overallRating;
        this.recommendation = recommendation;
        this.remarks = remarks;
        this.submittedBy = submittedBy;
        this.submittedAt = submittedAt;
    }
    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }
    public int getInterviewId() { return interviewId; }
    public void setInterviewId(int interviewId) { this.interviewId = interviewId; }
    public double getTechnicalRating() { return technicalRating; }
    public void setTechnicalRating(double technicalRating) { this.technicalRating = technicalRating; }
    public double getCommunicationRating() { return communicationRating; }
    public void setCommunicationRating(double communicationRating) { this.communicationRating = communicationRating; }
    public double getProblemSolvingRating() { return problemSolvingRating; }
    public void setProblemSolvingRating(double problemSolvingRating) { this.problemSolvingRating = problemSolvingRating; }
    public double getOverallRating() { return overallRating; }
    public void setOverallRating(double overallRating) { this.overallRating = overallRating; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public int getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(int submittedBy) { this.submittedBy = submittedBy; }
    public java.sql.Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(java.sql.Timestamp submittedAt) { this.submittedAt = submittedAt; }

    @Override
    public String toString() {
        return "InterviewFeedback{" + "feedbackId=" + feedbackId + ", " + "interviewId=" + interviewId + ", " + "technicalRating=" + technicalRating + ", " + "communicationRating=" + communicationRating + ", " + "problemSolvingRating=" + problemSolvingRating + ", " + "overallRating=" + overallRating + ", " + "recommendation=" + recommendation + ", " + "remarks=" + remarks + ", " + "submittedBy=" + submittedBy + ", " + "submittedAt=" + submittedAt + "}";
    }
}
