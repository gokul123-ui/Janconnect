package com.janconnect.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class EvidenceDTO {
    private Long id;
    private String complaintId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Instant uploadedAt;
}
