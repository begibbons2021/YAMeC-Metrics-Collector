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
    @Index(name = "idx_memory_metrics_timestamp", columnList = "timestamp")
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
    private double averageVirtualUtilization;

    /**
     * The maximum amount of virtual memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "max_virtual_utilization")
    private double maxVirtualUtilization;

    /**
     * The minimum amount of virtual memory in bytes used by the entire system within the measured timespan
     */
    @Column(name = "min_virtual_utilization")
    private double minVirtualUtilization;

    /**
     * Whether the physical memory available should be represented using an
     * unsigned value
     */
    @Column(name = "physical_memory_available_is_unsigned")
    private boolean physicalMemoryAvailableIsUnsigned;

    /**
     * Whether the virtual memory committed should be represented using an
     * unsigned value
     */
    @Column(name = "virtual_memory_committed_is_unsigned")
    private boolean virtualMemoryCommittedIsUnsigned;

    protected SystemMemoryMetric() {}

    public SystemMemoryMetric(Integer duration, UUID granularityId, Long averagePhysicalUtilization, Long maxPhysicalUtilization, Long minPhysicalUtilization) {
        super(duration, granularityId);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.physicalMemoryAvailableIsUnsigned = true;
        this.virtualMemoryCommittedIsUnsigned = true;
    }

    public SystemMemoryMetric(Integer duration, UUID granularityId, Long averagePhysicalUtilization, Long maxPhysicalUtilization, Long minPhysicalUtilization, Timestamp timestamp) {
        super(duration, granularityId, timestamp);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.physicalMemoryAvailableIsUnsigned = true;
        this.virtualMemoryCommittedIsUnsigned = true;
    }

    public SystemMemoryMetric(Integer duration, UUID granularityId, Long averagePhysicalUtilization, Long maxPhysicalUtilization, Long minPhysicalUtilization, Double averageVirtualUtilization, Double maxVirtualUtilization, Double minVirtualUtilization) {
        super(duration, granularityId);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.averageVirtualUtilization = averageVirtualUtilization;
        this.maxVirtualUtilization = maxVirtualUtilization;
        this.minVirtualUtilization = minVirtualUtilization;
        this.physicalMemoryAvailableIsUnsigned = true;
        this.virtualMemoryCommittedIsUnsigned = true;
    }

    public SystemMemoryMetric(Integer duration, UUID granularityId, Long averagePhysicalUtilization, Long maxPhysicalUtilization, Long minPhysicalUtilization, Double averageVirtualUtilization, Double maxVirtualUtilization, Double minVirtualUtilization, Timestamp timestamp) {
        super(duration, granularityId, timestamp);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.averageVirtualUtilization = averageVirtualUtilization;
        this.maxVirtualUtilization = maxVirtualUtilization;
        this.minVirtualUtilization = minVirtualUtilization;
        this.physicalMemoryAvailableIsUnsigned = true;
        this.virtualMemoryCommittedIsUnsigned = true;
    }

    /* TODO: Add setters/getters and refactor below */

    /**
     * Returns the number of bytes of physical memory available on the device
     * when this SystemMemoryMetric was collected. This value returns signed
     *
     * @return A long integer containing the amount of physical memory available
     */
    public long getPhysicalMemoryAvailable() {
        return physicalMemoryAvailable;
    }

    /**
     * Sets the number of bytes of physical memory available
     *
     * @param physicalMemoryAvailable The amount of physical memory available in bytes
     */
    public void setPhysicalMemoryAvailable(long physicalMemoryAvailable) {
        this.physicalMemoryAvailable = physicalMemoryAvailable;
    }

    /**
     * Returns the number of bytes of physical memory available on the device
     * when this SystemMemoryMetric was collected as an unsigned String
     *
     * @return A String containing the amount of physical memory available,
     * unsigned
     */
    public String getPhysicalMemoryAvailableUnsigned() {
        return Long.toUnsignedString(physicalMemoryAvailable);
    }

    /**
     * Returns the number of bytes of virtual memory committed on the device
     * when this SystemMemoryMetric was collected. This value returns signed
     *
     * @return A long integer containing the amount of virtual memory committed
     */
    public long getVirtualMemoryCommitted() {
        return virtualMemoryCommitted;
    }

    /**
     * Sets the number of bytes of virtual memory committed
     *
     * @param virtualMemoryCommitted The amount of virtual memory committed in bytes
     */
    public void setVirtualMemoryCommitted(long virtualMemoryCommitted) {
        this.virtualMemoryCommitted = virtualMemoryCommitted;
    }

    /**
     * Returns the number of bytes of virtual memory committed on the device
     * when this SystemMemoryMetric was collected as an unsigned String
     *
     * @return A String containing the amount of virtual memory committed,
     * unsigned
     */
    public String getVirtualMemoryCommittedUnsigned() {
        return Long.toUnsignedString(virtualMemoryCommitted);
    }

    /**
     * Returns the percentage of committed virtual memory which is actively
     * in use when this SystemMemoryMetric was collected as a double
     *
     * @return A double containing the percentage of virtual memory committed
     */
    public double getCommittedVirtualMemoryUsage() {
        return committedVirtualMemoryUsage;
    }

    /**
     * Sets the percentage of committed virtual memory in use
     *
     * @param committedVirtualMemoryUsage The percentage of virtual memory in use
     */
    public void setCommittedVirtualMemoryUsage(double committedVirtualMemoryUsage) {
        this.committedVirtualMemoryUsage = committedVirtualMemoryUsage;
    }

    /**
     * Calculates the number of bytes of virtual memory in use (treating the numbers as signed)
     * by multiplying the amount of memory committed to the page file/virtual memory by the
     * percent of that memory in use. It is then returned as a double rounded up to the next
     * full number of bytes
     *
     * @return A double containing the number of bytes of virtual memory in use
     */
    public double getCommittedVirtualMemoryBytes() {
        // Calculate the actual virtual memory use from the amount of committed memory used.
        return Math.ceil(this.getVirtualMemoryCommitted()
                * this.getCommittedVirtualMemoryUsage());
    }

    /**
     * Returns whether the physicalMemoryAvailable value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether physicalMemoryAvailable is unsigned
     * (true) or not (false)
     */
    public boolean isPhysicalMemoryAvailableUnsigned() {
        return physicalMemoryAvailableIsUnsigned;
    }

    /**
     * Sets whether the physical memory available value should be treated as unsigned
     *
     * @param physicalMemoryAvailableIsUnsigned Whether to treat the value as unsigned
     */
    public void setPhysicalMemoryAvailableUnsigned(boolean physicalMemoryAvailableIsUnsigned) {
        this.physicalMemoryAvailableIsUnsigned = physicalMemoryAvailableIsUnsigned;
    }

    /**
     * Returns whether the virtualMemoryCommitted value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether virtualMemoryCommitted is unsigned
     * (true) or not (false)
     */
    public boolean isVirtualMemoryCommittedUnsigned() {
        return virtualMemoryCommittedIsUnsigned;
    }

    /**
     * Sets whether the virtual memory committed value should be treated as unsigned
     *
     * @param virtualMemoryCommittedIsUnsigned Whether to treat the value as unsigned
     */
    public void setVirtualMemoryCommittedIsUnsigned(boolean virtualMemoryCommittedIsUnsigned) {
        this.virtualMemoryCommittedIsUnsigned = virtualMemoryCommittedIsUnsigned;
    }
}
