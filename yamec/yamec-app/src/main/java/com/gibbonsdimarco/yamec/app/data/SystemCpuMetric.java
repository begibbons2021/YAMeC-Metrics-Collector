package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Contains collected CPU metrics passed from the SystemMonitorManager
 *
 */
@Entity
@Table(name = "system_cpu_metrics", indexes = {
    @Index(name = "idx_system_cpu_metrics_timestamp", columnList = "timestamp")
})
public class SystemCpuMetric extends SystemDeviceMetric implements Serializable {
    /**
     * The average percentage of utilization measured within this SystemDeviceMetric
     */
    @Column(name = "average_utilization")
    private Double averageUtilization;

    /**
     * The maximum percentage of utilization measured within this SystemDeviceMetric
     */
    @Column(name = "max_utilization")
    private Double maxUtilization;

    /**
     * The minimum percentage of utilization measured within this SystemDeviceMetric
     */
    @Column(name = "min_utilization")
    private Double minUtilization;

    protected SystemCpuMetric() {}

    public SystemCpuMetric(Integer duration, UUID granularityId, Double averageUtilization, Double maxUtilization, Double minUtilization) {
        super(duration, granularityId);
        this.averageUtilization = averageUtilization;
        this.maxUtilization = maxUtilization;
        this.minUtilization = minUtilization;
    }
    public SystemCpuMetric(Integer duration, UUID granularityId, Double averageUtilization, Double maxUtilization, Double minUtilization, Timestamp timestamp) {
        super(duration, granularityId, timestamp);
        this.averageUtilization = averageUtilization;
        this.maxUtilization = maxUtilization;
        this.minUtilization = minUtilization;
    }

    public Double getAverageUtilization() {
        return averageUtilization;
    }

    public void setAverageUtilization(Double averageUtilization) {
        this.averageUtilization = averageUtilization;
    }

    public Double getMaxUtilization() {
        return maxUtilization;
    }

    public void setMaxUtilization(Double maxUtilization) {
        this.maxUtilization = maxUtilization;
    }

    public Double getMinUtilization() {
        return minUtilization;
    }

    public void setMinUtilization(Double minUtilization) {
        this.minUtilization = minUtilization;
    }

    @Override
    public String toString() {
        return "SystemCpuMetric{" +
                "id=" + getId() +
                ", timestamp=" + getTimestamp() +
                ", duration=" + getDuration() +
                "averageUtilization=" + averageUtilization +
                ", maxUtilization=" + maxUtilization +
                ", minUtilization=" + minUtilization +
                '}';
    }
}
