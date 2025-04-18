package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

/**
 * Contains collected primary memory metrics passed from the
 * SystemMonitorManager
 *
 */
@Entity
@Table(name = "memory_metrics", indexes = {
    @Index(name = "idx_memory_metrics_timestamp", columnList = "timestamp")
})
public class SystemMemoryMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The number of bytes of physical memory available on the device
     */
    @Column(name = "physical_memory_available", nullable = false)
    private long physicalMemoryAvailable;

    /**
     * The number of bytes of virtual memory committed for use on the device
     */
    @Column(name = "virtual_memory_committed", nullable = false)
    private long virtualMemoryCommitted;

    /**
     * The percentage of virtual memory committed and in use on the device
     */
    @Column(name = "committed_virtual_memory_usage", nullable = false)
    private double committedVirtualMemoryUsage;

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

    @Column(nullable = false)
    private java.sql.Timestamp timestamp;

    @Column(nullable = false)
    private Integer duration;

    public SystemMemoryMetric(long physicalMemoryAvailable,
                              long virtualMemoryCommitted,
                              double committedVirtualMemoryUsage,
                              boolean physicalMemoryAvailableIsUnsigned,
                              boolean virtualMemoryCommittedIsUnsigned) {
        this.physicalMemoryAvailable = physicalMemoryAvailable;
        this.virtualMemoryCommitted = virtualMemoryCommitted;
        this.committedVirtualMemoryUsage = committedVirtualMemoryUsage;
        this.physicalMemoryAvailableIsUnsigned = physicalMemoryAvailableIsUnsigned;
        this.virtualMemoryCommittedIsUnsigned = virtualMemoryCommittedIsUnsigned;
        this.timestamp = new java.sql.Timestamp(System.currentTimeMillis());
        this.duration = 0; // Default duration
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public java.sql.Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.sql.Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
