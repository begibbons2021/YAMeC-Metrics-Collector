package com.gibbonsdimarco.yamec.app.repository;

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
 * Repository interface for SystemMemoryMetric entities
 */
@Repository
public interface SystemMemoryMetricRepository extends JpaRepository<SystemMemoryMetric, UUID> {
    // Add custom query methods if needed

    java.util.List<SystemMemoryMetric> getAllByMemoryId(UUID memoryId);

    java.util.List<SystemMemoryMetric> findAllByMemoryId(UUID memoryId);

    @Query("select s from SystemMemoryMetric s order by s.timestamp desc limit 1")
    SystemMemoryMetric getNewest();

    @Query("select s from SystemMemoryMetric s where s.memory.id = :memoryId " +
            "order by s.timestamp desc limit 1")
    SystemMemoryMetric getNewestByMemoryId(@Param("memoryId") UUID memoryId);

    @Query("select s from SystemMemoryMetric s where s.memory.id = :memoryId " +
            "order by s.timestamp desc limit 1")
    SystemMemoryMetric findNewestByMemoryId(@Param("memoryId") UUID memoryId);

    List<SystemMemoryMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore);

    List<SystemMemoryMetric> findAllByTimestampBetweenAndMemory_Id(Timestamp timestampAfter,
                                                                   Timestamp timestampBefore,
                                                                   UUID memoryId);

    List<SystemMemoryMetric> findAllByTimestampBetween(Timestamp timestampAfter, Timestamp timestampBefore,
                                                       Pageable pageable);

    List<SystemMemoryMetric> findAllByTimestampBetweenAndMemory_Id(Timestamp timestampAfter,
                                                                   Timestamp timestampBefore,
                                                                   UUID memoryId,
                                                                   Pageable pageable);


} 