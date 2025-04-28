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
     * The average percentage of CPU utilization of all processes of this Application
     * at the time this ApplicationMetric is collected
     */
    @Column(name="avg_cpu_usage")
    private Double avgCpuUsage;

    /**
     * The average number of bytes of physical memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="avg_physical_memory_used")
    private Long avgPhysicalMemoryUsed;


    /**
     * The average number of bytes of virtual memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="avg_virtual_memory_used")
    private Long avgVirtualMemoryUsed;

    /**
     * The maximum percentage of CPU utilization of all processes of this Application
     * at the time this ApplicationMetric is collected
     */
    @Column(name="max_cpu_usage")
    private Double maxCpuUsage;

    /**
     * The maximum number of bytes of physical memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="max_physical_memory_used")
    private Long maxPhysicalMemoryUsed;


    /**
     * The maximum number of bytes of virtual memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="max_virtual_memory_used")
    private Long maxVirtualMemoryUsed;

    /**
     * The minimum percentage of CPU utilization of all processes of this Application
     * at the time this ApplicationMetric is collected
     */
    @Column(name="min_cpu_usage")
    private Double minCpuUsage;

    /**
     * The minimum number of bytes of physical memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="min_physical_memory_used")
    private Long minPhysicalMemoryUsed;


    /**
     * The minimum number of bytes of virtual memory used by all processes of this
     * Application at the time this ApplicationMetric is collected
     */
    @Column(name="min_virtual_memory_used")
    private Long minVirtualMemoryUsed;




    public ApplicationMetric(Application application,
                             Timestamp timestamp,
                             int duration,
                             double avgCpuUsage,
                             long avgPhysicalMemoryUsed,
                             long avgVirtualMemoryUsed,
                             double maxCpuUsage,
                             long maxPhysicalMemoryUsed,
                             long maxVirtualMemoryUsed,
                             double minCpuUsage,
                             long minPhysicalMemoryUsed,
                             long minVirtualMemoryUsed) {
        this.application = application;
        this.timestamp = timestamp;
        this.duration = duration; // Default duration
        this.avgCpuUsage = avgCpuUsage;
        this.avgPhysicalMemoryUsed = avgPhysicalMemoryUsed;
        this.avgVirtualMemoryUsed = avgVirtualMemoryUsed;
        this.maxCpuUsage = maxCpuUsage;
        this.maxPhysicalMemoryUsed = maxPhysicalMemoryUsed;
        this.maxVirtualMemoryUsed = maxVirtualMemoryUsed;
        this.minCpuUsage = minCpuUsage;
        this.minPhysicalMemoryUsed = minPhysicalMemoryUsed;
        this.minVirtualMemoryUsed = minVirtualMemoryUsed;
    }

    public ApplicationMetric(Application application,
                             int duration,
                             double avgCpuUsage,
                             long avgPhysicalMemoryUsed,
                             long avgVirtualMemoryUsed,
                             double maxCpuUsage,
                             long maxPhysicalMemoryUsed,
                             long maxVirtualMemoryUsed,
                             double minCpuUsage,
                             long minPhysicalMemoryUsed,
                             long minVirtualMemoryUsed) {
        this.application = application;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.duration = duration; // Default duration
        this.avgCpuUsage = avgCpuUsage;
        this.avgPhysicalMemoryUsed = avgPhysicalMemoryUsed;
        this.avgVirtualMemoryUsed = avgVirtualMemoryUsed;
        this.maxCpuUsage = maxCpuUsage;
        this.maxPhysicalMemoryUsed = maxPhysicalMemoryUsed;
        this.maxVirtualMemoryUsed = maxVirtualMemoryUsed;
        this.minCpuUsage = minCpuUsage;
        this.minPhysicalMemoryUsed = minPhysicalMemoryUsed;
        this.minVirtualMemoryUsed = minVirtualMemoryUsed;
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

    public Double getAvgCpuUsage() {
        return avgCpuUsage;
    }

    public void setAvgCpuUsage(double avgCpuUsage) {
        this.avgCpuUsage = avgCpuUsage;
    }

    public Long getAvgPhysicalMemoryUsed() {
        return avgPhysicalMemoryUsed;
    }

    public void setAvgPhysicalMemoryUsed(long avgPhysicalMemoryUsed) {
        this.avgPhysicalMemoryUsed = avgPhysicalMemoryUsed;
    }

    public Long getAvgVirtualMemoryUsed() {
        return avgVirtualMemoryUsed;
    }

    public void setAvgVirtualMemoryUsed(long avgVirtualMemoryUsed) {
        this.avgVirtualMemoryUsed = avgVirtualMemoryUsed;
    }

    public Double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public void setMaxCpuUsage(Double maxCpuUsage) {
        this.maxCpuUsage = maxCpuUsage;
    }

    public Long getMaxPhysicalMemoryUsed() {
        return maxPhysicalMemoryUsed;
    }

    public void setMaxPhysicalMemoryUsed(Long maxPhysicalMemoryUsed) {
        this.maxPhysicalMemoryUsed = maxPhysicalMemoryUsed;
    }

    public Long getMaxVirtualMemoryUsed() {
        return maxVirtualMemoryUsed;
    }

    public void setMaxVirtualMemoryUsed(Long maxVirtualMemoryUsed) {
        this.maxVirtualMemoryUsed = maxVirtualMemoryUsed;
    }

    public Double getMinCpuUsage() {
        return minCpuUsage;
    }

    public void setMinCpuUsage(Double minCpuUsage) {
        this.minCpuUsage = minCpuUsage;
    }

    public Long getMinPhysicalMemoryUsed() {
        return minPhysicalMemoryUsed;
    }

    public void setMinPhysicalMemoryUsed(Long minPhysicalMemoryUsed) {
        this.minPhysicalMemoryUsed = minPhysicalMemoryUsed;
    }

    public Long getMinVirtualMemoryUsed() {
        return minVirtualMemoryUsed;
    }

    public void setMinVirtualMemoryUsed(Long minVirtualMemoryUsed) {
        this.minVirtualMemoryUsed = minVirtualMemoryUsed;
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

