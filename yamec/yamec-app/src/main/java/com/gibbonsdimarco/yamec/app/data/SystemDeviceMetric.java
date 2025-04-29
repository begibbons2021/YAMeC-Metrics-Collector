package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Base class for all system device metrics
 */
@MappedSuperclass
public abstract class SystemDeviceMetric implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The starting time at which this SystemDeviceMetric was collected
     */
    @Column(nullable = false)
    private Timestamp timestamp;

    /**
     * The amount of time in seconds which this SystemDeviceMetric represents data from
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * The granularity of the data collected by this SystemDeviceMetric.
     */
    @Column(name = "granularity_id", nullable = false)
    private UUID granularityId;

    protected SystemDeviceMetric() {
    }

    public SystemDeviceMetric(Integer duration, UUID granularityId) {
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.duration = duration;
        this.granularityId = granularityId;
    }

    public SystemDeviceMetric(Integer duration, UUID granularityId, Timestamp timestamp) {
        this.duration = duration;
        this.granularityId = granularityId;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public UUID getGranularityId() {
        return granularityId;
    }

    public void setGranularityId(UUID granularityId) {
        this.granularityId = granularityId;
    }

    @Override
    public String toString() {
        return "SystemDeviceMetric{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                '}';
    }
}
