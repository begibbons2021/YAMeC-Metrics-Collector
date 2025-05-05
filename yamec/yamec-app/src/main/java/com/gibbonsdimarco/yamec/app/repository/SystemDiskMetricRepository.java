package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import com.gibbonsdimarco.yamec.app.data.SystemNicMetric;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for SystemDiskMetric entities
 */
@Repository
public interface SystemDiskMetricRepository extends JpaRepository<SystemDiskMetric, UUID> {
    // Add custom query methods if needed

    java.util.List<SystemDiskMetric> getAllByDiskId(UUID diskId);

    java.util.List<SystemDiskMetric> findAllByDiskId(UUID diskId);

    @Query("select s from SystemDiskMetric s where s.disk.id = :diskId " +
            "order by s.timestamp desc limit 1")
    SystemDiskMetric getNewestByDiskId(@Param("diskId") UUID diskId);

    @Query("select s from SystemDiskMetric s where s.disk.id = :diskId " +
            "order by s.timestamp desc limit 1")
    SystemDiskMetric findNewestByDiskId(@Param("diskId") UUID diskId);

    @Query("select s from SystemDiskMetric s where s.disk.diskNumber = :diskDiskNumber " +
            "order by s.timestamp desc limit 1")
    SystemDiskMetric findNewestByDisk_DiskNumber(@Param("diskDiskNumber") long diskDiskNumber);

    @Query("select s from SystemDiskMetric s where s.disk.diskNumber = :diskDiskNumber " +
            "order by s.timestamp desc limit 1")
    SystemDiskMetric getNewestByDisk_DiskNumber(@Param("diskDiskNumber") long diskDiskNumber);

    List<SystemDiskMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore);

    List<SystemDiskMetric> findAllByTimestampBetweenAndDisk_Id(Timestamp timestampAfter,
                                                             Timestamp timestampBefore,
                                                             UUID diskId);

    List<SystemDiskMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore,
                                                     Pageable pageable);

    List<SystemDiskMetric> findAllByTimestampBetweenAndDisk_Id(Timestamp timestampAfter,
                                                                Timestamp timestampBefore,
                                                                UUID diskId,
                                                                Pageable pageable);



} 