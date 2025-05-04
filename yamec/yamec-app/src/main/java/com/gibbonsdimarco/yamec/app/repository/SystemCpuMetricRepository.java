package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemCpuMetric;
import com.gibbonsdimarco.yamec.app.data.SystemNicMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    SystemCpuMetric getNewestByCpuId(UUID cpuId);

    @Query("select s from SystemCpuMetric s where s.cpu.id = :cpuId " +
            "order by s.timestamp desc limit 1")
    SystemCpuMetric findNewestByCpuId(UUID cpuId);

    @Query("select s from SystemCpuMetric s where s.cpu.friendlyName = :cpuFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemCpuMetric findNewestByCpu_FriendlyName(String cpuFriendlyName);

    @Query("select s from SystemCpuMetric s where s.cpu.friendlyName = :cpuFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemCpuMetric getNewestByCpu_FriendlyName(String cpuFriendlyName);

} 