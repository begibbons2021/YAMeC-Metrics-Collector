package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.Application;
import com.gibbonsdimarco.yamec.app.data.ApplicationMetric;
import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
}