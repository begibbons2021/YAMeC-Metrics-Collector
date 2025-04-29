package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

/**
 * Contains collected secondary storage device metrics passed from the
 * SystemMonitorManager
 *
 */
@Entity
@Table(name = "system_disk_metrics", indexes = {
    @Index(name = "idx_system_disk_metrics_timestamp", columnList = "timestamp")
})
public class SystemDiskMetric extends SystemDeviceMetric {

    /**
     * A locally used String which describes the Disk device and partition which
     * this SystemDiskMetric pertains to
     */
    @Transient
    private String deviceName;

    /**
     * The percentage of total disk utilization for the device,
     * or the percentage of time the disk spends active
     */
    @Column(name = "usage", nullable = false)
    private double usage;

    /**
     * The number of bytes read per second on the device
     * (at the time of metrics collection)
     */
    @Column(name = "read_bandwidth", nullable = false)
    private long readBandwidth;

    /**
     * The number of bytes written per second on the device
     * (at the time of metrics collection)
     */
    @Column(name = "write_bandwidth", nullable = false)
    private long writeBandwidth;

    /**
     * The amount of time in seconds the typical data transfer operation
     * between the system and the Disk which this SystemDiskMetric relates
     * to, on average (at the time of metrics collection)
     */
    @Column(name = "average_time_to_transfer", nullable = false)
    private double averageTimeToTransfer;

    /**
     * Whether the read bandwidth should be represented using an
     * unsigned value
     */
    @Column(name = "read_bandwidth_is_unsigned")
    private boolean readBandwidthIsUnsigned;

    /**
     * Whether the write bandwidth should be represented using an
     * unsigned value
     */
    @Column(name = "write_bandwidth_is_unsigned")
    private boolean writeBandwidthIsUnsigned;

    public SystemDiskMetric(String deviceName,
                            double usage,
                            long readBandwidth,
                            long writeBandwidth,
                            double averageTimeToTransfer,
                            boolean readBandwidthIsUnsigned,
                            boolean writeBandwidthIsUnsigned) {
        this.deviceName = deviceName;
        this.usage = usage;
        this.readBandwidth = readBandwidth;
        this.writeBandwidth = writeBandwidth;
        this.averageTimeToTransfer = averageTimeToTransfer;
        this.readBandwidthIsUnsigned = readBandwidthIsUnsigned;
        this.writeBandwidthIsUnsigned = writeBandwidthIsUnsigned;
    }

    public SystemDiskMetric() {

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
     * Sets the disk utilization percentage
     *
     * @param usage The disk utilization percentage
     */
    public void setUsage(double usage) {
        this.usage = usage;
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
     * Sets the read bandwidth in bytes per second
     *
     * @param readBandwidth The read bandwidth in bytes per second
     */
    public void setReadBandwidth(long readBandwidth) {
        this.readBandwidth = readBandwidth;
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
     * Sets the write bandwidth in bytes per second
     *
     * @param writeBandwidth The write bandwidth in bytes per second
     */
    public void setWriteBandwidth(long writeBandwidth) {
        this.writeBandwidth = writeBandwidth;
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
     * Sets the average time to transfer data in seconds
     *
     * @param averageTimeToTransfer The average time to transfer in seconds
     */
    public void setAverageTimeToTransfer(double averageTimeToTransfer) {
        this.averageTimeToTransfer = averageTimeToTransfer;
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
     * Sets whether the read bandwidth value should be treated as unsigned
     *
     * @param readBandwidthIsUnsigned Whether to treat the value as unsigned
     */
    public void setReadBandwidthUnsigned(boolean readBandwidthIsUnsigned) {
        this.readBandwidthIsUnsigned = readBandwidthIsUnsigned;
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

    /**
     * Sets whether the write bandwidth value should be treated as unsigned
     *
     * @param writeBandwidthIsUnsigned Whether to treat the value as unsigned
     */
    public void setWriteBandwidthUnsigned(boolean writeBandwidthIsUnsigned) {
        this.writeBandwidthIsUnsigned = writeBandwidthIsUnsigned;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
