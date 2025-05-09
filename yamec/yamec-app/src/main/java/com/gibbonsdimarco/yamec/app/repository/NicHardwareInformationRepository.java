package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.NicHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for NicHardwareInformation entities
 */
@Repository
public interface NicHardwareInformationRepository extends JpaRepository<NicHardwareInformation, UUID> {
    // Add custom query methods if needed


    NicHardwareInformation findByUniqueId(String uniqueId);

    NicHardwareInformation findByFriendlyName(String friendlyName);

    NicHardwareInformation getByFriendlyName(String friendlyName);

    NicHardwareInformation getByUniqueId(String uniqueId);

} 