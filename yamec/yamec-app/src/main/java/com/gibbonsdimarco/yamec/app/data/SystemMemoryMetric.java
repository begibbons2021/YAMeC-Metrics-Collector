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
        long virtualMemoryUsed = (long)(java.lang.Math.ceil(virtualCommitted * virtualCommittedUtilization));

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
                              Long averagePhysicalUtilization,
                              Long maxPhysicalUtilization,
                              Long minPhysicalUtilization) {
        super(duration, granularityId);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.physicalUtilizationIsUnsigned = true;
        this.virtualUtilizationIsUnsigned = true;
    }

    public SystemMemoryMetric(Integer duration, UUID granularityId,
                              Long averagePhysicalUtilization,
                              Long maxPhysicalUtilization,
                              Long minPhysicalUtilization, Timestamp timestamp) {
        super(duration, granularityId, timestamp);
        this.averagePhysicalUtilization = averagePhysicalUtilization;
        this.maxPhysicalUtilization = maxPhysicalUtilization;
        this.minPhysicalUtilization = minPhysicalUtilization;
        this.physicalUtilizationIsUnsigned = true;
        this.virtualUtilizationIsUnsigned = true;
    }

    public SystemMemoryMetric(Integer duration, UUID granularityId,
                              Long averagePhysicalUtilization,
                              Long maxPhysicalUtilization,
                              Long minPhysicalUtilization,
                              Double averageVirtualUtilization,
                              Double maxVirtualUtilization,
                              Double minVirtualUtilization) {
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

    public SystemMemoryMetric(Integer duration, UUID granularityId,
                              Long averagePhysicalUtilization,
                              Long maxPhysicalUtilization,
                              Long minPhysicalUtilization,
                              Double averageVirtualUtilization,
                              Double maxVirtualUtilization,
                              Double minVirtualUtilization,
                              Timestamp timestamp) {
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

    /* TODO: Add setters/getters and refactor below */

//    /**
//     * Returns the number of bytes of physical memory available on the device
//     * when this SystemMemoryMetric was collected. This value returns signed
//     *
//     * @return A long integer containing the amount of physical memory available
//     */
//    public long getPhysicalMemoryAvailable() {
//        return physicalMemoryAvailable;
//    }
//
//    /**
//     * Sets the number of bytes of physical memory available
//     *
//     * @param physicalMemoryAvailable The amount of physical memory available in bytes
//     */
//    public void setPhysicalMemoryAvailable(long physicalMemoryAvailable) {
//        this.physicalMemoryAvailable = physicalMemoryAvailable;
//    }
//
//    /**
//     * Returns the number of bytes of physical memory available on the device
//     * when this SystemMemoryMetric was collected as an unsigned String
//     *
//     * @return A String containing the amount of physical memory available,
//     * unsigned
//     */
//    public String getPhysicalMemoryAvailableUnsigned() {
//        return Long.toUnsignedString(physicalMemoryAvailable);
//    }
//
//    /**
//     * Returns the number of bytes of virtual memory committed on the device
//     * when this SystemMemoryMetric was collected. This value returns signed
//     *
//     * @return A long integer containing the amount of virtual memory committed
//     */
//    public long getVirtualMemoryCommitted() {
//        return virtualMemoryCommitted;
//    }
//
//    /**
//     * Sets the number of bytes of virtual memory committed
//     *
//     * @param virtualMemoryCommitted The amount of virtual memory committed in bytes
//     */
//    public void setVirtualMemoryCommitted(long virtualMemoryCommitted) {
//        this.virtualMemoryCommitted = virtualMemoryCommitted;
//    }
//
//    /**
//     * Returns the number of bytes of virtual memory committed on the device
//     * when this SystemMemoryMetric was collected as an unsigned String
//     *
//     * @return A String containing the amount of virtual memory committed,
//     * unsigned
//     */
//    public String getVirtualMemoryCommittedUnsigned() {
//        return Long.toUnsignedString(virtualMemoryCommitted);
//    }
//
//    /**
//     * Returns the percentage of committed virtual memory which is actively
//     * in use when this SystemMemoryMetric was collected as a double
//     *
//     * @return A double containing the percentage of virtual memory committed
//     */
//    public double getCommittedVirtualMemoryUsage() {
//        return committedVirtualMemoryUsage;
//    }
//
//    /**
//     * Sets the percentage of committed virtual memory in use
//     *
//     * @param committedVirtualMemoryUsage The percentage of virtual memory in use
//     */
//    public void setCommittedVirtualMemoryUsage(double committedVirtualMemoryUsage) {
//        this.committedVirtualMemoryUsage = committedVirtualMemoryUsage;
//    }
//
//    /**
//     * Calculates the number of bytes of virtual memory in use (treating the numbers as signed)
//     * by multiplying the amount of memory committed to the page file/virtual memory by the
//     * percent of that memory in use. It is then returned as a double rounded up to the next
//     * full number of bytes
//     *
//     * @return A double containing the number of bytes of virtual memory in use
//     */
//    public double getCommittedVirtualMemoryBytes() {
//        // Calculate the actual virtual memory use from the amount of committed memory used.
//        return Math.ceil(this.getVirtualMemoryCommitted()
//                * this.getCommittedVirtualMemoryUsage());
//    }

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

    public double getAverageVirtualUtilization() {
        return averageVirtualUtilization;
    }

    public void setAverageVirtualUtilization(double averageVirtualUtilization) {
        this.averageVirtualUtilization = averageVirtualUtilization;
    }

    public double getMaxVirtualUtilization() {
        return maxVirtualUtilization;
    }

    public void setMaxVirtualUtilization(double maxVirtualUtilization) {
        this.maxVirtualUtilization = maxVirtualUtilization;
    }

    public double getMinVirtualUtilization() {
        return minVirtualUtilization;
    }

    public void setMinVirtualUtilization(double minVirtualUtilization) {
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
