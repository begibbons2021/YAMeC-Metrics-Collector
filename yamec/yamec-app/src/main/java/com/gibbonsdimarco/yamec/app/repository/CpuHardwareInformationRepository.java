package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.CpuHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CpuHardwareInformation entities
 */
@Repository
public interface CpuHardwareInformationRepository extends JpaRepository<CpuHardwareInformation, Long> {
    // Add custom query methods if needed
}