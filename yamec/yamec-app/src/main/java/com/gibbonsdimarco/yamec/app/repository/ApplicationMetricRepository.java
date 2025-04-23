package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.ApplicationMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ApplicationMetric entities
 */
@Repository
public interface ApplicationMetricRepository extends JpaRepository<ApplicationMetric, Long> {
    // Add custom query methods if needed
} 