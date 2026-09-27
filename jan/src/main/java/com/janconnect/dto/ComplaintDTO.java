package com.janconnect.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ComplaintDTO {
    private String id;
    private String complaintId;  // JC-2026-XXXXX
    private String userId;
    private String userName;
    private String originalText;
    private String language;
    private String categoryId;
    private String categoryName;
    private String departmentName;
    private String departmentId;
    private String description;
    private String aiSummary;
    private String requestedAction;
    private String issueTitle;
    private String priority;
    private String status;
    private String statusDisplay;

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

    private Instant createdAt;
    private Instant updatedAt;

    private List<TimelineDTO> statusHistory;
    private List<EvidenceDTO> evidence;
}
