package com.janconnect.dto;

import lombok.Data;

@Data
public class CreateComplaintRequest {
    private String originalText;
    private String language;
    private String categoryId;
    private String categoryName;
    private String departmentName;
    private String description;
    private String aiSummary;
    private String requestedAction;
    private String issueTitle;
    private String priority;

    // Location
    private String locationAddress;
    private String locationLandmark;
    private String locationDistrict;
    private String locationState;
    private String locationPincode;
    private Double latitude;
    private Double longitude;

    // Contact
    private String contactName;
    private String contactPhone;
    private String contactEmail;
}
