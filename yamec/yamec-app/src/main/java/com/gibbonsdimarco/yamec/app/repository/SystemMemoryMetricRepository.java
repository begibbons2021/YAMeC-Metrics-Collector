package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemMemoryMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for SystemMemoryMetric entities
 */
@Repository
public interface SystemMemoryMetricRepository extends JpaRepository<SystemMemoryMetric, Long> {
    // Add custom query methods if needed
} 