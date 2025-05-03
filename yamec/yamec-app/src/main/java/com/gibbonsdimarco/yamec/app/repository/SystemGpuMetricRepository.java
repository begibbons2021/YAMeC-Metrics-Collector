package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemGpuMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for SystemGpuMetric entities
 */
@Repository
public interface SystemGpuMetricRepository extends JpaRepository<SystemGpuMetric, UUID> {
    // Add custom query methods if needed
} 