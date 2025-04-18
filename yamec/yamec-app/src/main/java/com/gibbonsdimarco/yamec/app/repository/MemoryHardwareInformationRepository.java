package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.MemoryHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for MemoryHardwareInformation entities
 */
@Repository
public interface MemoryHardwareInformationRepository extends JpaRepository<MemoryHardwareInformation, Long> {
    // Add custom query methods if needed
} 