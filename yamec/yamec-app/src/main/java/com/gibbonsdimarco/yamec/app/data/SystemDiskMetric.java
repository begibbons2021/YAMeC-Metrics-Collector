package com.gibbonsdimarco.yamec.app.data;

/**
 * Contains collected secondary storage device metrics passed from the
 * SystemMonitorManager
 *
 */
public class SystemDiskMetric extends SystemDeviceMetric {

    /**
     * The percentage of total disk utilization for the device,
     * or the percentage of time the disk spends active
     */
    private double usage;

    /**
     * The number of bytes read per second on the device
     * (at the time of metrics collection)
     */
    private long readBandwidth;

    /**
     * The number of bytes written per second on the device
     * (at the time of metrics collection)
     */
    private long writeBandwidth;

    /**
     * The amount of time in seconds the typical data transfer operation
     * between the system and the Disk which this SystemDiskMetric relates
     * to, on average (at the time of metrics collection)
     */
    private double averageTimeToTransfer;

    /**
     * Whether the read bandwidth should be represented using an
     * unsigned value
     */
    private boolean readBandwidthIsUnsigned;

    /**
     * Whether the write bandwidth should be represented using an
     * unsigned value
     */
    private boolean writeBandwidthIsUnsigned;

    public SystemDiskMetric(String deviceName,
                            double usage,
                            long readBandwidth,
                            long writeBandwidth,
                            double averageTimeToTransfer,
                            boolean readBandwidthIsUnsigned,
                            boolean writeBandwidthIsUnsigned) {
        super(deviceName);
        this.usage = usage;
        this.readBandwidth = readBandwidth;
        this.writeBandwidth = writeBandwidth;
        this.averageTimeToTransfer = averageTimeToTransfer;
        this.readBandwidthIsUnsigned = readBandwidthIsUnsigned;
        this.writeBandwidthIsUnsigned = writeBandwidthIsUnsigned;
    }

    /**
     * Returns a double precision float containing the percentage of disk utilization
     * of this SystemDiskMetric's source hardware device
     *
     * @return A double containing disk utilization as a percent
     */
    public double getUsage() {
        return usage;
    }

    /**
     * Returns the number of bytes read per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the read bandwidth in bytes per second
     */
    public long getReadBandwidth() {
        return readBandwidth;
    }

    /**
     * Returns the number of bytes read per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected as an
     * unsigned String
     *
     * @return A String containing the read bandwidth in bytes per second, unsigned
     */
    public String getReadBandwidthUnsigned() {
        return Long.toUnsignedString(readBandwidth);
    }

    /**
     * Returns the number of bytes written per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the write bandwidth in bytes per second
     */
    public long getWriteBandwidth() {
        return writeBandwidth;
    }

    /**
     * Returns the number of bytes written per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected as an
     * unsigned String
     *
     * @return A String containing the write bandwidth in bytes per second, unsigned
     */
    public String getWriteBandwidthUnsigned() {
        return Long.toUnsignedString(writeBandwidth);
    }

    /**
     * Returns a double precision float containing the average time to transfer
     * data to/from SystemDiskMetric's source hardware device in bytes per second
     *
     * @return A double containing disk average time to transfer in bytes per second
     */
    public double getAverageTimeToTransfer() {
        return averageTimeToTransfer;
    }

    /**
     * Returns whether the readBandwidthIsUnsigned value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether readBandwidthIsUnsigned is unsigned
     * (true) or not (false)
     */
    public boolean isReadBandwidthUnsigned() {
        return readBandwidthIsUnsigned;
    }

    /**
     * Returns whether the writeBandwidthIsUnsigned value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether writeBandwidthIsUnsigned is unsigned
     * (true) or not (false)
     */
    public boolean isWriteBandwidthUnsigned() {
        return writeBandwidthIsUnsigned;
    }

}
