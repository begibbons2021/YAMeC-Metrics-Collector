package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Contains collected primary memory metrics passed from the
 * SystemMonitorManager
 *
 */
@Entity
@Table(name = "system_memory_metrics", indexes = {
    @Index(name = "idx_system_memory_metrics_timestamp", columnList = "timestamp")
})
public class SystemMemoryMetric extends SystemDeviceMetric implements Serializable {
    /**
     * The average amount of physical memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "average_physical_utilization", nullable = false)
    private long averagePhysicalUtilization;

    /**
     * The maximum amount of physical memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "max_physical_utilization", nullable = false)
    private long maxPhysicalUtilization;

    /**
     * The minimum amount of physical memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "min_physical_utilization", nullable = false)
    private long minPhysicalUtilization;

    /**
     * The average amount of virtual memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "average_virtual_utilization")
    private long averageVirtualUtilization;

    /**
     * The maximum amount of virtual memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "max_virtual_utilization")
    private long maxVirtualUtilization;

    /**
     * The minimum amount of virtual memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "min_virtual_utilization")
    private long minVirtualUtilization;

    /**
     * Whether the <code>physicalUtilization</code> should be represented using an
     * unsigned value
     */
    @Column(name = "physical_utilization_is_unsigned")
    private boolean physicalUtilizationIsUnsigned;

    /**
     * Whether the <code>virtualUtilization</code> should be represented using an
     * unsigned value
     */
    @Column(name = "virtual_utilization_is_unsigned")
    private boolean virtualUtilizationIsUnsigned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", nullable = false,
            foreignKey = @ForeignKey(name="fk_memory_hardware_information"))
    private MemoryHardwareInformation memory;


    protected SystemMemoryMetric() {}

    /**
     * Constructs a SystemMemoryMetric object from the number of bytes of physical memory in use and the
     * amount of committed virtual memory in use, calculated from the amount committed times the
     * percent utilization.
     * @param physicalUtilization The number of bytes of physical memory in use
     * @param virtualCommitted The number of bytes of virtual memory committed on disk
     * @param virtualCommittedUtilization The percentage of committed virtual memory in use
     * @param physicalUtilizationIsUnsigned Whether <code>physicalUtilization</code> should be treated
     *                                          as an unsigned value (true) or not (false)
     * @param virtualUtilizationIsUnsigned Whether <code>virtualCommitted</code>/<code>virtualUtilization</code>
     *                                         should be treated as an unsigned value (true) or not (false)
     */
    public SystemMemoryMetric(long physicalUtilization, long virtualCommitted, double virtualCommittedUtilization,
                              boolean physicalUtilizationIsUnsigned, boolean virtualUtilizationIsUnsigned) {
        super();
        this.setDuration(1);
        // Calculate the actual amount of virtual memory in use out of the committed virtual memory size
        long virtualMemoryUsed = (long)(java.lang.Math.ceil(virtualCommitted * (virtualCommittedUtilization/100.0)));

        this.averagePhysicalUtilization = physicalUtilization;
        this.maxPhysicalUtilization = physicalUtilization;
        this.minPhysicalUtilization = physicalUtilization;

        this.averageVirtualUtilization = virtualMemoryUsed;
        this.maxVirtualUtilization = virtualMemoryUsed;
        this.minVirtualUtilization = virtualMemoryUsed;

        this.physicalUtilizationIsUnsigned = physicalUtilizationIsUnsigned;
        this.virtualUtilizationIsUnsigned = virtualUtilizationIsUnsigned;

    }

    public SystemMemoryMetric(Integer duration, UUID granularityId,
                              long averagePhysicalUtilization,
                              long maxPhysicalUtilization,
                              long minPhysicalUtilization) {
        super(duration, granularityId);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.physicalUtilizationIsUnsigned = true;
        this.virtualUtilizationIsUnsigned = true;
    }

    public SystemMemoryMetric(Timestamp timestamp, Integer duration,
                              UUID granularityId,
                              long averagePhysicalUtilization,
                              long maxPhysicalUtilization,
                              long minPhysicalUtilization) {
        super(duration, granularityId, timestamp);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.physicalUtilizationIsUnsigned = true;
        this.virtualUtilizationIsUnsigned = true;
    }

    public SystemMemoryMetric(Integer duration, UUID granularityId,
                              long averagePhysicalUtilization,
                              long maxPhysicalUtilization,
                              long minPhysicalUtilization,
                              long averageVirtualUtilization,
                              long maxVirtualUtilization,
                              long minVirtualUtilization) {
        super(duration, granularityId);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.averageVirtualUtilization = averageVirtualUtilization;
        this.maxVirtualUtilization = maxVirtualUtilization;
        this.minVirtualUtilization = minVirtualUtilization;
        this.physicalUtilizationIsUnsigned = true;
        this.virtualUtilizationIsUnsigned = true;
    }

    public SystemMemoryMetric(Timestamp timestamp,
                              Integer duration, UUID granularityId,
                              long averagePhysicalUtilization,
                              long maxPhysicalUtilization,
                              long minPhysicalUtilization,
                              long averageVirtualUtilization,
                              long maxVirtualUtilization,
                              long minVirtualUtilization) {
        super(duration, granularityId, timestamp);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.averageVirtualUtilization = averageVirtualUtilization;
        this.maxVirtualUtilization = maxVirtualUtilization;
        this.minVirtualUtilization = minVirtualUtilization;
        this.physicalUtilizationIsUnsigned = true;
        this.virtualUtilizationIsUnsigned = true;
    }

    public SystemMemoryMetric(Timestamp timestamp,
                              Integer duration, UUID granularityId,
                              long averagePhysicalUtilization,
                              long maxPhysicalUtilization,
                              long minPhysicalUtilization,
                              long averageVirtualUtilization,
                              long maxVirtualUtilization,
                              long minVirtualUtilization,
                              boolean physicalUtilizationIsUnsigned,
                              boolean virtualUtilizationIsUnsigned) {
        super(duration, granularityId, timestamp);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.averageVirtualUtilization = averageVirtualUtilization;
        this.maxVirtualUtilization = maxVirtualUtilization;
        this.minVirtualUtilization = minVirtualUtilization;
        this.physicalUtilizationIsUnsigned = physicalUtilizationIsUnsigned;
        this.virtualUtilizationIsUnsigned = virtualUtilizationIsUnsigned;
    }

    public long getAveragePhysicalUtilization() {
        return averagePhysicalUtilization;
    }

    public void setAveragePhysicalUtilization(long averagePhysicalUtilization) {
        this.averagePhysicalUtilization = averagePhysicalUtilization;
    }

    public long getMaxPhysicalUtilization() {
        return maxPhysicalUtilization;
    }

    public void setMaxPhysicalUtilization(long maxPhysicalUtilization) {
        this.maxPhysicalUtilization = maxPhysicalUtilization;
    }

    public long getMinPhysicalUtilization() {
        return minPhysicalUtilization;
    }

    public void setMinPhysicalUtilization(long minPhysicalUtilization) {
        this.minPhysicalUtilization = minPhysicalUtilization;
    }

    public long getAverageVirtualUtilization() {
        return averageVirtualUtilization;
    }

    public void setAverageVirtualUtilization(long averageVirtualUtilization) {
        this.averageVirtualUtilization = averageVirtualUtilization;
    }

    public long getMaxVirtualUtilization() {
        return maxVirtualUtilization;
    }

    public void setMaxVirtualUtilization(long maxVirtualUtilization) {
        this.maxVirtualUtilization = maxVirtualUtilization;
    }

    public long getMinVirtualUtilization() {
        return minVirtualUtilization;
    }

    public void setMinVirtualUtilization(long minVirtualUtilization) {
        this.minVirtualUtilization = minVirtualUtilization;
    }

    public MemoryHardwareInformation getMemory() {
        return memory;
    }

    public void setMemory(MemoryHardwareInformation memoryHardware) {
        this.memory = memoryHardware;
    }

    /**
     * Returns whether the <code>physicalUtilization</code> value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether <code>physicalUtilization</code> is unsigned
     * (true) or not (false)
     */
    public boolean isPhysicalUtilizationUnsigned() {
        return physicalUtilizationIsUnsigned;
    }

    /**
     * Sets whether the <code>physicalUtilization</code> value should be treated as unsigned
     *
     * @param physicalUtilizationIsUnsigned Whether to treat the value as unsigned
     */
    public void setPhysicalUtilizationIsUnsigned(boolean physicalUtilizationIsUnsigned) {
        this.physicalUtilizationIsUnsigned = physicalUtilizationIsUnsigned;
    }

    /**
     * Returns whether the <code>virtualUtilization</code> value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether <code>virtualUtilization</code> is unsigned
     * (true) or not (false)
     */
    public boolean isVirtualUtilizationUnsigned() {
        return virtualUtilizationIsUnsigned;
    }

    /**
     * Sets whether the <code>virtualUtilization</code> value should be treated as unsigned
     *
     * @param virtualUtilizationIsUnsigned Whether to treat the value as unsigned
     */
    public void setVirtualUtilizationIsUnsigned(boolean virtualUtilizationIsUnsigned) {
        this.virtualUtilizationIsUnsigned = virtualUtilizationIsUnsigned;
    }
}
