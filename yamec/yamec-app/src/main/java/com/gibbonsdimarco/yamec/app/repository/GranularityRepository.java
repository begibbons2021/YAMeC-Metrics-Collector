package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.config.Granularity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for ApplicationMetric entities
 */
@Repository
public interface GranularityRepository extends JpaRepository<Granularity, Long> {
    // Add custom query methods if needed

    Granularity findByLabel(String label);

    Granularity getByLabel(String label);

} 