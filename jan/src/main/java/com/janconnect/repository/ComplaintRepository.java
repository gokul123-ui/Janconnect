package com.janconnect.repository;

import com.janconnect.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, String> {

    // Eager-fetch user and department to avoid LazyInitializationException
    @Query("SELECT c FROM Complaint c JOIN FETCH c.user WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Complaint> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

    @Query("SELECT c FROM Complaint c JOIN FETCH c.user ORDER BY c.createdAt DESC")
    List<Complaint> findAllWithUser();

    @Query("SELECT c FROM Complaint c JOIN FETCH c.user WHERE c.complaintId = :complaintId")
    Optional<Complaint> findByComplaintId(@Param("complaintId") String complaintId);

    @Query("SELECT c FROM Complaint c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Complaint> findByIdWithUser(@Param("id") String id);

    boolean existsByComplaintId(String complaintId);

    @Query("SELECT COUNT(c) FROM Complaint c")
    long countAll();
}
