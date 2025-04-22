package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Base class for all system device metrics
 */
@MappedSuperclass
public abstract class SystemDeviceMetric implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of this SystemDeviceMetric's source hardware device
     */
    @Column(name = "device_name", length = 255, nullable = false)
    private String deviceName;

    @Column(nullable = false)
    private Timestamp timestamp;

    @Column(nullable = false)
    private Integer duration;

    public SystemDeviceMetric(String deviceName) {
        this.deviceName = deviceName;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.duration = 0; // Default duration
    }

    /**
     * No-Args constructor required by JPA to allow reflection
     */
    public SystemDeviceMetric() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
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
}
