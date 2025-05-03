package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.DiskHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for DiskHardwareInformation entities
 */
@Repository
public interface DiskHardwareInformationRepository extends JpaRepository<DiskHardwareInformation, UUID> {
    // Add custom query methods if needed

    DiskHardwareInformation findByUniqueId(String uniqueId);

    DiskHardwareInformation findByDiskNumber(Long diskNumber);

    DiskHardwareInformation getByDiskNumber(Long diskNumber);

    DiskHardwareInformation getByUniqueId(String uniqueId);

} 