package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import com.gibbonsdimarco.yamec.app.data.SystemMemoryMetric;
import com.gibbonsdimarco.yamec.app.data.SystemNicMetric;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for SystemNicMetric entities
 */
@Repository
public interface SystemNicMetricRepository extends JpaRepository<SystemNicMetric, UUID> {
    // Add custom query methods if needed


    java.util.List<SystemNicMetric> getAllByNicId(UUID nicId);

    java.util.List<SystemNicMetric> findAllByNicId(UUID nicId);

    @Query("select s from SystemNicMetric s where s.nic.id = :nicId " +
            "order by s.timestamp desc limit 1")
    SystemNicMetric getNewestByNicId(@Param("nicId") UUID nicId);

    @Query("select s from SystemNicMetric s where s.nic.id = :nicId " +
            "order by s.timestamp desc limit 1")
    SystemNicMetric findNewestByNicId(@Param("nicId") UUID nicId);

    @Query("select s from SystemNicMetric s where s.nic.friendlyName = :nicFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemNicMetric findNewestByNic_FriendlyName(@Param("nicFriendlyName") String nicFriendlyName);

    @Query("select s from SystemNicMetric s where s.nic.friendlyName = :nicFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemNicMetric getNewestByNic_FriendlyName(@Param("nicFriendlyName") String nicFriendlyName);

    List<SystemNicMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore);

    List<SystemNicMetric> findAllByTimestampBetweenAndNic_Id(Timestamp timestampAfter,
                                                                   Timestamp timestampBefore,
                                                                   UUID nicId);

    List<SystemNicMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore,
                                                    Pageable pageable);

    List<SystemNicMetric> findAllByTimestampBetweenAndNic_Id(Timestamp timestampAfter,
                                                             Timestamp timestampBefore,
                                                             UUID nicId,
                                                             Pageable pageable);



} 