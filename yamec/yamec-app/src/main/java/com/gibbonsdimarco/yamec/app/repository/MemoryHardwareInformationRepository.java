package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.MemoryHardwareInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for MemoryHardwareInformation entities
 */
@Repository
public interface MemoryHardwareInformationRepository extends JpaRepository<MemoryHardwareInformation, UUID> {
    // Add custom query methods if needed

    /**
     * Queries the database for a MemoryHardwareInformation record which matches the configuration passed by parameter,
     * then returns it if it exists (based on speed, capacity, slots used, slots total, speed is unsigned, and
     * capacity is unsigned)
     * @param memoryHardwareInformation A MemoryHardwareInformation object containing the specified fields
     * @return The MemoryHardwareInformation record stored in the database matching the input object
     */
    @Query("""
              select m from MemoryHardwareInformation m where m.speed = :#{#memoryHardwareInformation.speed} and \
              m.capacity = :#{#memoryHardwareInformation.capacity} \
              and m.slotsUsed = :#{#memoryHardwareInformation.slotsUsed} \
              and m.slotsTotal = :#{#memoryHardwareInformation.slotsTotal} \
              and m.speedIsUnsigned = :#{#memoryHardwareInformation.isSpeedUnsigned} \
              and m.capacityIsUnsigned = :#{#memoryHardwareInformation.isCapacityUnsigned}""")
    MemoryHardwareInformation findMatchingMemoryHardwareInformation(
            @Param("memoryHardwareInformation") MemoryHardwareInformation memoryHardwareInformation
    );



} 