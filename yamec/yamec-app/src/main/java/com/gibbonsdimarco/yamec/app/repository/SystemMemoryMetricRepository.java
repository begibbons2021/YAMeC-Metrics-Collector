package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemMemoryMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for SystemMemoryMetric entities
 */
@Repository
public interface SystemMemoryMetricRepository extends JpaRepository<SystemMemoryMetric, UUID> {
    // Add custom query methods if needed
} 