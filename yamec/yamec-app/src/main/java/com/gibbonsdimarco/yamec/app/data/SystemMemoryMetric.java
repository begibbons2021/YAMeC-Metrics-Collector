package com.gibbonsdimarco.yamec.app.data;

/**
 * Contains collected primary memory metrics passed from the
 * SystemMonitorManager
 *
 */
public class SystemMemoryMetric {

    /**
     * The number of bytes of physical memory available on the device
     */
    private long physicalMemoryAvailable;

    /**
     * The number of bytes of virtual memory committed for use on the device
     */
    private long virtualMemoryCommitted;

    /**
     * The percentage of virtual memory committed and in use on the device
     */
    private double committedVirtualMemoryUsage;

    /**
     * Whether the physical memory available should be represented using an
     * unsigned value
     */
    private boolean physicalMemoryAvailableIsUnsigned;

    /**
     * Whether the virtual memory committed should be represented using an
     * unsigned value
     */
    private boolean virtualMemoryCommittedIsUnsigned;

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

    // Remove use of unsigned values?

    /**
     * Calculates the number of bytes of virtual memory in use (treating the numbers as signed)
     * by multiplying the amount of memory committed to the page file/virtual memory by the
     * percent of that memory in use. It is then returned as a double rounded up to the next
     * full number of bytes
     *
     * @return A double containing the number of bytes of virtual memory in use
     */
    public double getCommittedVirtualMemoryBytes()
    {
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
     * Returns whether the virtualMemoryCommitted value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether virtualMemoryCommitted is unsigned
     * (true) or not (false)
     */
    public boolean isVirtualMemoryCommittedUnsigned() {
        return virtualMemoryCommittedIsUnsigned;
    }

}
