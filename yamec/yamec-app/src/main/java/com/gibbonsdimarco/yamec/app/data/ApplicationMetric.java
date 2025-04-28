package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Contains metrics for system resources collected on specific applications on the system
 */
@Entity
@Table(name = "application_metrics", indexes = {
        @Index(name = "idx_application_metrics_timestamp", columnList = "timestamp"),
        @Index(name = "idx_application_metrics_application", columnList = "application_id")
})
public class ApplicationMetric implements Serializable {

    /**
     * A unique identifier for use in the database to represent each application metric on the system
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The Application which this ApplicationMetric refers to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_id", nullable = false, foreignKey = @ForeignKey(name="fk_application"))
    private Application application;


    /**
     * The time at which this ApplicationMetric is collected/logged
     */
    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    /**
     * The amount of time in seconds which this ApplicationMetric represents data from
     */
    @Column(name = "duration", nullable = false)
    private Integer duration;

    /**
     * The percentage of CPU utilization of all processes of this Application
     * at the time this ApplicationMetric is collected
     */
    @Column(name="avg_cpu_usage")
    private Double cpuUsage;

    /**
     * The number of bytes of physical memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="avg_physical_memory_used")
    private Long physicalMemoryUsed;


    /**
     * The number of bytes of virtual memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="avg_virtual_memory_used")
    private Long virtualMemoryUsed;




    public ApplicationMetric(Application application,
                             Timestamp timestamp,
                             int duration,
                             double cpuUsage,
                             long physicalMemoryUsed,
                             long virtualMemoryUsed) {
        this.application = application;
        this.timestamp = timestamp;
        this.duration = duration; // Default duration
        this.cpuUsage = cpuUsage;
        this.physicalMemoryUsed = physicalMemoryUsed;
        this.virtualMemoryUsed = virtualMemoryUsed;
    }

    public ApplicationMetric(Application application,
                             int duration,
                             double cpuUsage,
                             long physicalMemoryUsed,
                             long virtualMemoryUsed) {
        this.application = application;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.duration = duration; // Default duration
        this.cpuUsage = cpuUsage;
        this.physicalMemoryUsed = physicalMemoryUsed;
        this.virtualMemoryUsed = virtualMemoryUsed;
    }

    public ApplicationMetric(Application application,
                             double cpuUsage,
                             long physicalMemoryUsed,
                             long virtualMemoryUsed) {
        this.application = application;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.duration = 0; // Default duration
        this.cpuUsage = cpuUsage;
        this.physicalMemoryUsed = physicalMemoryUsed;
        this.virtualMemoryUsed = virtualMemoryUsed;
    }

    /**
     * No-Args constructor required by JPA to allow reflection
     */
    public ApplicationMetric() {
        this.application = null;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.duration = 0; // Default duration
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Long getPhysicalMemoryUsed() {
        return physicalMemoryUsed;
    }

    public void setPhysicalMemoryUsed(long physicalMemoryUsed) {
        this.physicalMemoryUsed = physicalMemoryUsed;
    }

    public Long getVirtualMemoryUsed() {
        return virtualMemoryUsed;
    }

    public void setVirtualMemoryUsed(long virtualMemoryUsed) {
        this.virtualMemoryUsed = virtualMemoryUsed;
    }

    @Override
    public boolean equals(Object otherApplicationMetric) {
        if ((otherApplicationMetric != null) && (this.getClass() != otherApplicationMetric.getClass())) {
            return false;
        }

        ApplicationMetric other = (ApplicationMetric) otherApplicationMetric;

        return this.getId().equals(other.getId());

    }
}

