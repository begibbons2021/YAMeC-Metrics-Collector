package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.Application;
import com.gibbonsdimarco.yamec.app.data.ApplicationMetric;
import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for ApplicationMetric entities
 */
@Repository
public interface ApplicationMetricRepository extends JpaRepository<ApplicationMetric, UUID> {
    // Add custom query methods if needed

    List<ApplicationMetric> findByApplication(Application application);

    List<ApplicationMetric> findByApplicationId(UUID applicationId);

    List<ApplicationMetric> findByApplicationIdOrderByTimestampDesc(UUID applicationId);

    List<ApplicationMetric> findAllByApplicationApplicationName(String name);

    List<ApplicationMetric> getByApplicationApplicationName(String name, Sort sort);

//    List<ApplicationMetric> getByGranularityLabel(String granularityLabel);

    List<ApplicationMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore);

    List<ApplicationMetric> findAllByTimestampBetweenAndApplication_Id(Timestamp timestampAfter,
                                                                        Timestamp timestampBefore,
                                                                        UUID applicationId);

    List<ApplicationMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore, Pageable pageable);

    List<ApplicationMetric> findAllByTimestampBetweenAndApplication_Id(Timestamp timestampAfter,
                                                                        Timestamp timestampBefore,
                                                                        UUID applicationId,
                                                                        Pageable pageable);

    @Query("SELECT a, m FROM Application a " +
            "INNER JOIN ApplicationMetric m ON m.application.id = a.id " +
            "INNER JOIN (SELECT metrics.innerId AS appId, MAX(metrics.innerTimestamp) as maxTimestamp " +
            "      FROM (" +
            "SELECT m2.application.id AS innerId, m2.timestamp AS innerTimestamp FROM ApplicationMetric m2 ORDER BY m2.timestamp DESC LIMIT 10000" +
            ") metrics " +
            "      WHERE metrics.innerTimestamp > :thresholdTime " +
            "      GROUP BY appId) latest " +
            "ON latest.appId = a.id AND (m.timestamp = latest.maxTimestamp) " +
            "ORDER BY m.timestamp DESC")
    List<Object[]> findLatestMetricsForAllApplications(
            @Param("thresholdTime") Timestamp thresholdTime,
            Pageable pageable
    );
}