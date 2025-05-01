package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.config.Granularity;
import com.gibbonsdimarco.yamec.app.config.GranularityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for ApplicationMetric entities
 */
@Repository
public interface GranularityConfigRepository extends JpaRepository<GranularityConfig, Long> {
    // Add custom query methods if needed

    GranularityConfig findByGranularity(Granularity granularity);

    GranularityConfig findByGranularityId(Long granularityId);

    GranularityConfig findByGranularityLabel(String label);

    GranularityConfig getByGranularity(Granularity granularity);

    GranularityConfig getByGranularityId(Long granularityId);

    GranularityConfig getByGranularityLabel(String label);

} 