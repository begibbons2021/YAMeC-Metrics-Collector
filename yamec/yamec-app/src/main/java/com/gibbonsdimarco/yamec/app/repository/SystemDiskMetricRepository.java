package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SystemDiskMetric entities
 */
@Repository
public interface SystemDiskMetricRepository extends JpaRepository<SystemDiskMetric, Long> {
    // Add custom query methods if needed
} 