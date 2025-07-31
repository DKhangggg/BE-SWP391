package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Booking;
import com.example.gender_healthcare_service.entity.SampleCollectionProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SampleCollectionProfileRepository extends JpaRepository<SampleCollectionProfile, Integer> {
    
    // Find by booking
    Optional<SampleCollectionProfile> findByBooking(Booking booking);
    
    Optional<SampleCollectionProfile> findByBookingId(Integer bookingId);
    
    // Check if booking has sample collection profile
    boolean existsByBooking(Booking booking);
    
    boolean existsByBookingId(Integer bookingId);
    
    // Find by collector information
    List<SampleCollectionProfile> findByCollectorIdCard(String collectorIdCard);
    
    List<SampleCollectionProfile> findByCollectorFullNameContainingIgnoreCase(String collectorName);
    
    // Find by relationship type
    List<SampleCollectionProfile> findByRelationshipToBooker(String relationshipToBooker);
    
    // Find by collection date range
    List<SampleCollectionProfile> findBySampleCollectionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find by staff who collected
    List<SampleCollectionProfile> findByCollectedBy(String collectedBy);
    
    // Statistics queries
    @Query("SELECT COUNT(scp) FROM SampleCollectionProfile scp WHERE scp.relationshipToBooker = :relationship")
    long countByRelationshipToBooker(@Param("relationship") String relationship);
    
    @Query("SELECT COUNT(scp) FROM SampleCollectionProfile scp WHERE scp.sampleCollectionDate >= :startDate")
    long countCollectedSince(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT scp.relationshipToBooker, COUNT(scp) FROM SampleCollectionProfile scp GROUP BY scp.relationshipToBooker")
    List<Object[]> getRelationshipStatistics();
    
    // Find profiles with notes
    @Query("SELECT scp FROM SampleCollectionProfile scp WHERE scp.notes IS NOT NULL AND scp.notes != ''")
    List<SampleCollectionProfile> findProfilesWithNotes();
    
    // Find recent collections
    @Query("SELECT scp FROM SampleCollectionProfile scp WHERE scp.sampleCollectionDate >= :since ORDER BY scp.sampleCollectionDate DESC")
    List<SampleCollectionProfile> findRecentCollections(@Param("since") LocalDateTime since);
    
    // Find by booking status
    @Query("SELECT scp FROM SampleCollectionProfile scp WHERE scp.booking.status = :status")
    List<SampleCollectionProfile> findByBookingStatus(@Param("status") String status);
}
