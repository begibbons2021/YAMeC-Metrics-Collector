package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    SystemDiskMetric getNewestByDiskId(UUID diskId);

    @Query("select s from SystemDiskMetric s where s.disk.id = :diskId " +
            "order by s.timestamp desc limit 1")
    SystemDiskMetric findNewestByDiskId(UUID diskId);

    @Query("select s from SystemDiskMetric s where s.disk.diskNumber = :diskDiskNumber " +
            "order by s.timestamp desc limit 1")
    SystemDiskMetric findNewestByDisk_DiskNumber(long diskDiskNumber);

    @Query("select s from SystemDiskMetric s where s.disk.diskNumber = :diskDiskNumber " +
            "order by s.timestamp desc limit 1")
    SystemDiskMetric getNewestByDisk_DiskNumber(long diskDiskNumber);



} 