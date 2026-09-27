package com.janconnect.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class TimelineDTO {
    private Long id;
    private String complaintId;
    private String status;
    private String description;
    private String changedBy;
    private Instant createdAt;
    // For frontend compatibility - map to "timestamp" and "note"
    private String timestamp;
    private NoteDTO note;

    @Data
    public static class NoteDTO {
        private String en;
        private String ta;
        private String hi;
        private String kn;
    }
}
