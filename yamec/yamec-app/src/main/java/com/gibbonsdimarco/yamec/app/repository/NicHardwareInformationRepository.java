package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.NicHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for NicHardwareInformation entities
 */
@Repository
public interface NicHardwareInformationRepository extends JpaRepository<NicHardwareInformation, Long> {
    // Add custom query methods if needed
} 