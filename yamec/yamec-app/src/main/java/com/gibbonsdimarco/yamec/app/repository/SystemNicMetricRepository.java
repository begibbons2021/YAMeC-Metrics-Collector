package com.gibbonsdimarco.yamec.app.repository;

import com.gibbonsdimarco.yamec.app.data.SystemDiskMetric;
import com.gibbonsdimarco.yamec.app.data.SystemNicMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
    SystemNicMetric getNewestByNicId(UUID nicId);

    @Query("select s from SystemNicMetric s where s.nic.id = :nicId " +
            "order by s.timestamp desc limit 1")
    SystemNicMetric findNewestByNicId(UUID nicId);

    @Query("select s from SystemNicMetric s where s.nic.friendlyName = :nicFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemNicMetric findNewestByNic_FriendlyName(String nicFriendlyName);

    @Query("select s from SystemNicMetric s where s.nic.friendlyName = :nicFriendlyName " +
            "order by s.timestamp desc limit 1")
    SystemNicMetric getNewestByNic_FriendlyName(String nicFriendlyName);


} 