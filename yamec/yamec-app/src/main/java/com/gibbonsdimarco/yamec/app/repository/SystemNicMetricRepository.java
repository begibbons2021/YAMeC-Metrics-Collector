package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemNicMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for SystemNicMetric entities
 */
@Repository
public interface SystemNicMetricRepository extends JpaRepository<SystemNicMetric, UUID> {
    // Add custom query methods if needed
} 