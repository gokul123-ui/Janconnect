package com.janconnect.mapper;

import com.janconnect.dto.ComplaintDTO;
import com.janconnect.dto.EvidenceDTO;
import com.janconnect.dto.TimelineDTO;
import com.janconnect.entity.Complaint;
import com.janconnect.entity.ComplaintTimeline;
import com.janconnect.entity.Evidence;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ComplaintMapper {

    public ComplaintDTO toDTO(Complaint complaint) {
        ComplaintDTO dto = new ComplaintDTO();
        dto.setId(complaint.getId());
        dto.setComplaintId(complaint.getComplaintId());
        dto.setUserId(complaint.getUser().getId());
        dto.setUserName(complaint.getUser().getName());
        dto.setOriginalText(complaint.getOriginalText());
        dto.setLanguage(complaint.getLanguage());
        dto.setCategoryId(complaint.getCategoryId());
        dto.setCategoryName(complaint.getCategoryName());
        dto.setDepartmentName(complaint.getDepartmentName());
        dto.setDepartmentId(complaint.getDepartment() != null ? String.valueOf(complaint.getDepartment().getId()) : null);
        dto.setDescription(complaint.getDescription());
        dto.setAiSummary(complaint.getAiSummary());
        dto.setRequestedAction(complaint.getRequestedAction());
        dto.setIssueTitle(complaint.getIssueTitle());
        dto.setPriority(complaint.getPriority() != null ? complaint.getPriority().name() : "MEDIUM");
        dto.setStatus(complaint.getStatus() != null ? complaint.getStatus().name() : "SUBMITTED");
        dto.setStatusDisplay(complaint.getStatus() != null ? complaint.getStatus().getDisplayName() : "Submitted");

        // Location
        dto.setLocationAddress(complaint.getLocationAddress());
        dto.setLocationLandmark(complaint.getLocationLandmark());
        dto.setLocationDistrict(complaint.getLocationDistrict());
        dto.setLocationState(complaint.getLocationState());
        dto.setLocationPincode(complaint.getLocationPincode());
        dto.setLatitude(complaint.getLatitude());
        dto.setLongitude(complaint.getLongitude());

        // Contact
        dto.setContactName(complaint.getContactName());
        dto.setContactPhone(complaint.getContactPhone());
        dto.setContactEmail(complaint.getContactEmail());

        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());

        // Timeline
        if (complaint.getTimeline() != null) {
            dto.setStatusHistory(complaint.getTimeline().stream()
                    .map(this::toTimelineDTO)
                    .collect(Collectors.toList()));
        }

        // Evidence
        if (complaint.getEvidences() != null) {
            dto.setEvidence(complaint.getEvidences().stream()
                    .map(this::toEvidenceDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public TimelineDTO toTimelineDTO(ComplaintTimeline timeline) {
        TimelineDTO dto = new TimelineDTO();
        dto.setId(timeline.getId());
        dto.setComplaintId(timeline.getComplaint().getId());
        dto.setStatus(timeline.getStatus());
        dto.setDescription(timeline.getDescription());
        dto.setChangedBy(timeline.getChangedBy());
        dto.setCreatedAt(timeline.getCreatedAt());
        dto.setTimestamp(timeline.getCreatedAt() != null ? timeline.getCreatedAt().toString() : null);

        // Build multilingual note from description
        TimelineDTO.NoteDTO note = new TimelineDTO.NoteDTO();
        String desc = timeline.getDescription() != null ? timeline.getDescription() : "";
        note.setEn(desc);
        note.setTa(desc);
        note.setHi(desc);
        note.setKn(desc);
        dto.setNote(note);

        return dto;
    }

    public EvidenceDTO toEvidenceDTO(Evidence evidence) {
        EvidenceDTO dto = new EvidenceDTO();
        dto.setId(evidence.getId());
        dto.setComplaintId(evidence.getComplaint().getId());
        dto.setFileName(evidence.getFileName());
        dto.setFileUrl("/uploads/" + evidence.getFilePath());
        dto.setFileType(evidence.getFileType());
        dto.setFileSize(evidence.getFileSize());
        dto.setUploadedAt(evidence.getUploadedAt());
        return dto;
    }

    public List<ComplaintDTO> toDTOList(List<Complaint> complaints) {
        return complaints.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
