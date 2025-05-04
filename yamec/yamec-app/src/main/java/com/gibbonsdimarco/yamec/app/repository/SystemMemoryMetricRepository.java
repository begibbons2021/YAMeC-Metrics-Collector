package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemMemoryMetric;
import com.gibbonsdimarco.yamec.app.data.SystemNicMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for SystemMemoryMetric entities
 */
@Repository
public interface SystemMemoryMetricRepository extends JpaRepository<SystemMemoryMetric, UUID> {
    // Add custom query methods if needed

    java.util.List<SystemMemoryMetric> getAllByMemoryId(UUID memoryId);

    java.util.List<SystemMemoryMetric> findAllByMemoryId(UUID memoryId);

    @Query("select s from SystemMemoryMetric s where s.memory.id = :memoryId " +
            "order by s.timestamp desc limit 1")
    SystemMemoryMetric getNewestByMemoryId(UUID memoryId);

    @Query("select s from SystemMemoryMetric s where s.memory.id = :memoryId " +
            "order by s.timestamp desc limit 1")
    SystemMemoryMetric findNewestByMemoryId(UUID memoryId);

} 