package com.janconnect.controller;

import com.janconnect.dto.ComplaintDTO;
import com.janconnect.dto.CreateComplaintRequest;
import com.janconnect.dto.EvidenceDTO;
import com.janconnect.dto.TimelineDTO;
import com.janconnect.entity.User;
import com.janconnect.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintDTO> createComplaint(
            @RequestBody CreateComplaintRequest request,
            @AuthenticationPrincipal User user
    ) {
        ComplaintDTO result = complaintService.createComplaint(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<ComplaintDTO>> getComplaints(@AuthenticationPrincipal User user) {
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        List<ComplaintDTO> complaints = isAdmin
                ? complaintService.getAllComplaints()
                : complaintService.getUserComplaints(user.getId());
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintDTO> getComplaint(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        ComplaintDTO complaint = complaintService.getComplaint(id, user.getId(), isAdmin);
        return ResponseEntity.ok(complaint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplaintDTO> updateComplaint(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user
    ) {
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        String newStatus = body.get("status");
        if (newStatus == null) newStatus = body.get("Status");
        ComplaintDTO updated = complaintService.updateComplaintStatus(id, newStatus, user.getName(), isAdmin);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComplaint(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        complaintService.deleteComplaint(id, user.getId(), isAdmin);
        return ResponseEntity.ok(Map.of("message", "Complaint deleted successfully"));
    }

    @GetMapping("/{id}/timeline")
    public ResponseEntity<List<TimelineDTO>> getTimeline(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        List<TimelineDTO> timeline = complaintService.getTimeline(id, user.getId(), isAdmin);
        return ResponseEntity.ok(timeline);
    }

    @PostMapping(value = "/{id}/evidence", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EvidenceDTO> uploadEvidence(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws IOException {
        boolean isAdmin = user.getRole() == User.Role.ADMIN;
        EvidenceDTO evidence = complaintService.uploadEvidence(id, file, user.getId(), isAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(evidence);
    }
}
