package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemCpuMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SystemCpuMetric entities
 */
@Repository
public interface SystemCpuMetricRepository extends JpaRepository<SystemCpuMetric, Long> {
    // Add custom query methods if needed
} 