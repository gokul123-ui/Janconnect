package com.janconnect.service;

import com.janconnect.dto.ComplaintDTO;
import com.janconnect.dto.CreateComplaintRequest;
import com.janconnect.dto.EvidenceDTO;
import com.janconnect.dto.TimelineDTO;
import com.janconnect.entity.*;
import com.janconnect.exception.BadRequestException;
import com.janconnect.exception.ResourceNotFoundException;
import com.janconnect.exception.UnauthorizedException;
import com.janconnect.mapper.ComplaintMapper;
import com.janconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintTimelineRepository timelineRepository;
    private final EvidenceRepository evidenceRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final ComplaintMapper complaintMapper;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public ComplaintDTO createComplaint(CreateComplaintRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Find department by name
        Department department = null;
        if (request.getDepartmentName() != null) {
            department = departmentRepository.findByName(request.getDepartmentName()).orElse(null);
            if (department == null) {
                // Try by code (categoryId)
                department = departmentRepository.findByCode(request.getCategoryId()).orElse(null);
            }
        }

        // Generate unique complaint ID: JC-YYYY-XXXXX
        String complaintId = generateComplaintId();

        // Map priority
        Complaint.Priority priority = Complaint.Priority.MEDIUM;
        if (request.getPriority() != null) {
            try {
                String p = request.getPriority().toUpperCase();
                if (p.equals("HIGH") || p.equals("CRITICAL")) priority = Complaint.Priority.HIGH;
                else if (p.equals("LOW")) priority = Complaint.Priority.LOW;
                else priority = Complaint.Priority.MEDIUM;
            } catch (Exception e) {
                priority = Complaint.Priority.MEDIUM;
            }
        }

        Complaint complaint = Complaint.builder()
                .complaintId(complaintId)
                .user(user)
                .originalText(request.getOriginalText())
                .language(request.getLanguage() != null ? request.getLanguage() : "en")
                .categoryId(request.getCategoryId())
                .categoryName(request.getCategoryName())
                .departmentName(request.getDepartmentName())
                .department(department)
                .description(request.getDescription())
                .aiSummary(request.getAiSummary())
                .requestedAction(request.getRequestedAction())
                .issueTitle(request.getIssueTitle())
                .priority(priority)
                .status(Complaint.Status.SUBMITTED)
                .locationAddress(request.getLocationAddress())
                .locationLandmark(request.getLocationLandmark())
                .locationDistrict(request.getLocationDistrict())
                .locationState(request.getLocationState())
                .locationPincode(request.getLocationPincode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .contactName(request.getContactName())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .build();

        Complaint saved = complaintRepository.save(complaint);

        // Create initial timeline entry
        ComplaintTimeline initialTimeline = ComplaintTimeline.builder()
                .complaint(saved)
                .status("Submitted")
                .description("Complaint submitted successfully to JanConnect citizen grievance portal.")
                .changedBy(user.getName())
                .build();
        timelineRepository.save(initialTimeline);

        // Reload to get all relations properly fetched
        Complaint fresh = complaintRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found after save"));

        // Manually load timeline since it might not be in session
        List<ComplaintTimeline> timelines = timelineRepository.findByComplaintIdOrderByCreatedAtAsc(fresh.getId());
        fresh.setTimeline(timelines);

        log.info("Complaint created: {} by user: {}", complaintId, user.getEmail());
        return complaintMapper.toDTO(fresh);
    }

    @Transactional(readOnly = true)
    public List<ComplaintDTO> getUserComplaints(String userId) {
        List<Complaint> complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return complaintMapper.toDTOList(complaints);
    }

    @Transactional(readOnly = true)
    public List<ComplaintDTO> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAllWithUser();
        return complaintMapper.toDTOList(complaints);
    }

    @Transactional(readOnly = true)
    public ComplaintDTO getComplaint(String id, String userId, boolean isAdmin) {
        Complaint complaint = findComplaintByIdOrComplaintId(id);

        if (!isAdmin && !complaint.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Access denied to this complaint");
        }

        return complaintMapper.toDTO(complaint);
    }

    @Transactional
    public ComplaintDTO updateComplaintStatus(String id, String newStatus, String changedBy, boolean isAdmin) {
        if (!isAdmin) throw new UnauthorizedException("Only admins can update complaint status");

        Complaint complaint = findComplaintByIdOrComplaintId(id);

        Complaint.Status status;
        try {
            status = Complaint.Status.valueOf(newStatus.toUpperCase().replace(" ", "_"));
        } catch (Exception e) {
            throw new BadRequestException("Invalid status: " + newStatus);
        }

        complaint.setStatus(status);
        Complaint updated = complaintRepository.save(complaint);

        // Add timeline entry
        ComplaintTimeline timeline = ComplaintTimeline.builder()
                .complaint(updated)
                .status(status.getDisplayName())
                .description("Status updated to: " + status.getDisplayName())
                .changedBy(changedBy)
                .build();
        timelineRepository.save(timeline);

        return complaintMapper.toDTO(complaintRepository.findById(updated.getId()).orElse(updated));
    }

    @Transactional
    public void deleteComplaint(String id, String userId, boolean isAdmin) {
        Complaint complaint = findComplaintByIdOrComplaintId(id);
        if (!isAdmin && !complaint.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Access denied");
        }
        complaintRepository.delete(complaint);
    }

    public List<TimelineDTO> getTimeline(String id, String userId, boolean isAdmin) {
        Complaint complaint = findComplaintByIdOrComplaintId(id);
        if (!isAdmin && !complaint.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Access denied");
        }
        List<ComplaintTimeline> timelines = timelineRepository.findByComplaintIdOrderByCreatedAtAsc(complaint.getId());
        return timelines.stream().map(complaintMapper::toTimelineDTO).toList();
    }

    @Transactional
    public EvidenceDTO uploadEvidence(String id, MultipartFile file, String userId, boolean isAdmin) throws IOException {
        Complaint complaint = findComplaintByIdOrComplaintId(id);
        if (!isAdmin && !complaint.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Access denied");
        }

        // Create uploads directory
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save file with unique name
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String uniqueFilename = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        Evidence evidence = Evidence.builder()
                .complaint(complaint)
                .fileName(originalFilename != null ? originalFilename : uniqueFilename)
                .filePath(uniqueFilename)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        Evidence saved = evidenceRepository.save(evidence);
        log.info("Evidence uploaded for complaint: {}", complaint.getComplaintId());
        return complaintMapper.toEvidenceDTO(saved);
    }

    private Complaint findComplaintByIdOrComplaintId(String id) {
        // Try by UUID with eager user fetch first, then by JC-YYYY-XXXXX format
        return complaintRepository.findByIdWithUser(id)
                .or(() -> complaintRepository.findByComplaintId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + id));
    }

    private String generateComplaintId() {
        int year = Year.now().getValue();
        String prefix = "JC-" + year + "-";
        // Find a unique 5-digit number
        for (int i = 0; i < 100; i++) {
            int num = (int) (Math.random() * 90000) + 10000;
            String candidate = prefix + num;
            if (!complaintRepository.existsByComplaintId(candidate)) {
                return candidate;
            }
        }
        // Fallback: use timestamp-based
        return prefix + System.currentTimeMillis() % 100000;
    }
}
