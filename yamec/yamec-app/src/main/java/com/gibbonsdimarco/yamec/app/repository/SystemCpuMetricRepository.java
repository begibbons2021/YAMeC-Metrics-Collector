package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemCpuMetric;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for SystemCpuMetric entities
 */
@Repository
public interface SystemCpuMetricRepository extends JpaRepository<SystemCpuMetric, UUID> {
    // Add custom query methods if needed
    java.util.List<SystemCpuMetric> getAllByCpuId(UUID cpuId);

    java.util.List<SystemCpuMetric> findAllByCpuId(UUID cpuId);

    @Query("select s from SystemCpuMetric s where s.cpu.id = :cpuId " +
            "order by s.timestamp desc limit 1")
    SystemCpuMetric getNewestByCpuId(@Param("cpuId") UUID cpuId);

    @Query("select s from SystemCpuMetric s where s.cpu.id = :cpuId " +
            "order by s.timestamp desc limit 1")
    SystemCpuMetric findNewestByCpuId(@Param("cpuId") UUID cpuId);

    @Query("select s from SystemCpuMetric s where s.cpu.friendlyName = :cpuFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemCpuMetric findNewestByCpu_FriendlyName(@Param("cpuFriendlyName") String cpuFriendlyName);

    @Query("select s from SystemCpuMetric s where s.cpu.friendlyName = :cpuFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemCpuMetric getNewestByCpu_FriendlyName(@Param("cpuFriendlyName") String cpuFriendlyName);

    @Query("select s from SystemCpuMetric s order by s.timestamp desc limit 1")
    SystemCpuMetric getNewest();

    List<SystemCpuMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore);

    List<SystemCpuMetric> findAllByTimestampBetweenAndCpu_Id(Timestamp timestampAfter,
                                                               Timestamp timestampBefore,
                                                               UUID cpuId);

    List<SystemCpuMetric> findAllByTimestampBetween(Timestamp timestampAfter,
                                                    Timestamp timestampBefore,
                                                    Pageable pageable);

    List<SystemCpuMetric> findAllByTimestampBetweenAndCpu_Id(Timestamp timestampAfter,
                                                             Timestamp timestampBefore,
                                                             UUID cpuId,
                                                             Pageable pageable);


} 