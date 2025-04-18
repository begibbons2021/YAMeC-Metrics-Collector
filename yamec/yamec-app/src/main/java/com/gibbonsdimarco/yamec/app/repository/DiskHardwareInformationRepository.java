package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.DiskHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for DiskHardwareInformation entities
 */
@Repository
public interface DiskHardwareInformationRepository extends JpaRepository<DiskHardwareInformation, Long> {
    // Add custom query methods if needed
} 