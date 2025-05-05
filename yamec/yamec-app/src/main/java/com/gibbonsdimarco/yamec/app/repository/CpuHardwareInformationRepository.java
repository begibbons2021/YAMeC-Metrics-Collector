package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.CpuHardwareInformation;
import com.gibbonsdimarco.yamec.app.data.MemoryHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for CpuHardwareInformation entities
 */
@Repository
public interface CpuHardwareInformationRepository extends JpaRepository<CpuHardwareInformation, UUID> {
    // Add custom query methods if needed

    CpuHardwareInformation findByFriendlyName(String friendlyName);

    /**
     * Queries the database for a CpuHardwareInformation record which matches the configuration passed by parameter,
     * then returns it if it exists (based on whether there is a CPU with the exact same configuraton as one
     * already recorded, including brand strings, cache sizes, cores and threads, and virtualization being on or off)
     * @param cpu A CpuHardwareInformation object containing the specified fields
     * @return The CpuHardwareInformation record stored in the database matching the input object
     */
    @Query("""
              select c from CpuHardwareInformation c where \
              c.friendlyName = :#{#cpuHardwareInformation.friendlyName} \
              and c.coreCount = :#{#cpuHardwareInformation.coreCount} \
              and c.logicalProcessorCount = :#{#cpuHardwareInformation.logicalProcessorCount} \
              and c.l1CacheSize = :#{#cpuHardwareInformation.l1CacheSize} \
              and c.l2CacheSize = :#{#cpuHardwareInformation.l2CacheSize} \
              and c.l3CacheSize = :#{#cpuHardwareInformation.l3CacheSize} \
              and c.virtualizationEnabled = :#{#cpuHardwareInformation.virtualizationEnabled}""")
    CpuHardwareInformation findMatchingCpuHardwareInformation(
            @Param("cpuHardwareInformation") CpuHardwareInformation cpu
    );


}