package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

import java.util.UUID;

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
     * or the percentage of time the disk spends active, on average
     */
    @Column(name = "avg_utilization", nullable = false)
    private double avgUtilization;

    /**
     * The number of bytes read per second on the device
     * (at the time of metrics collection), on average
     */
    @Column(name = "avg_read_bandwidth", nullable = false)
    private long avgReadBandwidth;

    /**
     * The number of bytes written per second on the device
     * (at the time of metrics collection), on average
     */
    @Column(name = "avg_write_bandwidth", nullable = false)
    private long avgWriteBandwidth;

    /**
     * <p>The amount of time in seconds the typical data transfer operation
     * between the system and the Disk which this SystemDiskMetric relates
     * to, on average at the time of metrics collection, and on average
     * throughout the duration of time this SystemDiskMetric relates to.</p>
     *
     * <p>Note that this current implementation relies on an average of an
     * average, meaning that it will not reflect spikes of transfer time
     * accurately, as of now. In a future release, this calculation and
     * column may benefit from a change. But, due to time constraints,
     * this is a known shortcoming with the data collection.</p>
     *
     */
    @Column(name = "avg_time_to_transfer", nullable = false)
    private double avgTimeToTransfer;

    /**
     * The maximum percentage of total disk utilization for the device,
     * or the percentage of time the disk spends active, throughout the
     * duration of this SystemDiskMetric
     */
    @Column(name = "max_utilization", nullable = false)
    private double maxUtilization;

    /**
     * The maximum number of bytes read per second on the device
     * (at the time of metrics collection), throughout the
     * duration of this SystemDiskMetric
     */
    @Column(name = "max_read_bandwidth", nullable = false)
    private long maxReadBandwidth;

    /**
     * The maximum number of bytes written per second on the device
     * (at the time of metrics collection), throughout the
     * duration of this SystemDiskMetric
     */
    @Column(name = "max_write_bandwidth", nullable = false)
    private long maxWriteBandwidth;

    /**
     * <p>The maximum average amount of time in seconds the typical data
     * transfer operation between the system and the Disk which this
     * SystemDiskMetric relates to throughout the duration of time
     * data was aggregated from.</p>
     *
     * <p>Note that this current implementation relies on a maximum of an
     * average, meaning that it will not reflect spikes of transfer time
     * accurately, as of now. In a future release, this calculation and
     * column may benefit from a change. But, due to time constraints,
     * this is a known shortcoming with the data collection.</p>
     *
     */
    @Column(name = "max_time_to_transfer", nullable = false)
    private double maxTimeToTransfer;

    /**
     * The minimum percentage of total disk utilization for the device,
     * or the percentage of time the disk spends active, throughout the
     * duration of this SystemDiskMetric
     */
    @Column(name = "min_utilization", nullable = false)
    private double minUtilization;

    /**
     * The minimum number of bytes read per second on the device
     * (at the time of metrics collection), throughout the
     * duration of this SystemDiskMetric
     */
    @Column(name = "min_read_bandwidth", nullable = false)
    private long minReadBandwidth;

    /**
     * The minimum number of bytes written per second on the device
     * (at the time of metrics collection), throughout the
     * duration of this SystemDiskMetric
     */
    @Column(name = "min_write_bandwidth", nullable = false)
    private long minWriteBandwidth;

    /**
     * <p>The minimum average amount of time in seconds the typical data
     * transfer operation between the system and the Disk which this
     * SystemDiskMetric relates to throughout the duration of time
     * data was aggregated from.</p>
     *
     * <p>Note that this current implementation relies on a minimum of an
     * average, meaning that it will not reflect spikes of transfer time
     * accurately, as of now. In a future release, this calculation and
     * column may benefit from a change. But, due to time constraints,
     * this is a known shortcoming with the data collection.</p>
     *
     */
    @Column(name = "min_time_to_transfer", nullable = false)
    private double minTimeToTransfer;

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
                            double utilization,
                            long readBandwidth,
                            long writeBandwidth,
                            double avgTimeToTransfer,
                            boolean readBandwidthIsUnsigned,
                            boolean writeBandwidthIsUnsigned) {
        this.deviceName = deviceName;
        this.setDuration(1);
        this.avgUtilization = utilization;
        this.maxUtilization = utilization;
        this.minUtilization = utilization;

        this.avgReadBandwidth = readBandwidth;
        this.maxReadBandwidth = readBandwidth;
        this.minReadBandwidth = readBandwidth;

        this.avgWriteBandwidth = writeBandwidth;
        this.maxWriteBandwidth = writeBandwidth;
        this.minWriteBandwidth = writeBandwidth;

        this.avgTimeToTransfer = avgTimeToTransfer;
        this.maxTimeToTransfer = avgTimeToTransfer;
        this.minTimeToTransfer = avgTimeToTransfer;

        this.readBandwidthIsUnsigned = readBandwidthIsUnsigned;
        this.writeBandwidthIsUnsigned = writeBandwidthIsUnsigned;
    }

    public SystemDiskMetric(Integer duration, UUID granularityId,
                            String deviceName,
                            double avgUtilization,
                            double maxUtilization,
                            double minUtilization,
                            long avgReadBandwidth,
                            long maxReadBandwidth,
                            long minReadBandwidth,
                            long avgWriteBandwidth,
                            long maxWriteBandwidth,
                            long minWriteBandwidth,
                            long avgTimeToTransfer,
                            long maxTimeToTransfer,
                            long minTimeToTransfer) {
        super(duration, granularityId);
        this.deviceName = deviceName;

        this.avgUtilization = avgUtilization;
        this.maxUtilization = maxUtilization;
        this.minUtilization = minUtilization;

        this.avgReadBandwidth = avgReadBandwidth;
        this.maxReadBandwidth = maxReadBandwidth;
        this.minReadBandwidth = minReadBandwidth;

        this.avgWriteBandwidth = avgWriteBandwidth;
        this.maxWriteBandwidth = maxWriteBandwidth;
        this.minWriteBandwidth = minWriteBandwidth;

        this.avgTimeToTransfer = avgTimeToTransfer;
        this.maxTimeToTransfer = maxTimeToTransfer;
        this.minTimeToTransfer = minTimeToTransfer;
    }

    public SystemDiskMetric() {

    }

    /**
     * Returns a double precision float containing the percentage of disk utilization
     * of this SystemDiskMetric's source hardware device
     *
     * @return A double containing disk utilization as a percent
     */
    public double getAvgUtilization() {
        return avgUtilization;
    }

    /**
     * Sets the disk utilization percentage
     *
     * @param utilization The disk utilization percentage
     */
    public void setAvgUtilization(double utilization) {
        this.avgUtilization = utilization;
    }

    /**
     * Returns the number of bytes read per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the read bandwidth in bytes per second
     */
    public long getAvgReadBandwidth() {
        return avgReadBandwidth;
    }

    /**
     * Sets the read bandwidth in bytes per second
     *
     * @param avgReadBandwidth The read bandwidth in bytes per second
     */
    public void setAvgReadBandwidth(long avgReadBandwidth) {
        this.avgReadBandwidth = avgReadBandwidth;
    }

    /**
     * Returns the number of bytes read per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected as an
     * unsigned String
     *
     * @return A String containing the read bandwidth in bytes per second, unsigned
     */
    public String getReadBandwidthUnsigned() {
        return Long.toUnsignedString(avgReadBandwidth);
    }

    /**
     * Returns the number of bytes written per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the write bandwidth in bytes per second
     */
    public long getAvgWriteBandwidth() {
        return avgWriteBandwidth;
    }

    /**
     * Sets the write bandwidth in bytes per second
     *
     * @param avgWriteBandwidth The write bandwidth in bytes per second
     */
    public void setAvgWriteBandwidth(long avgWriteBandwidth) {
        this.avgWriteBandwidth = avgWriteBandwidth;
    }

    /**
     * Returns the number of bytes written per second on the device (at the time
     * of metrics collection) when this SystemDiskMetric was collected as an
     * unsigned String
     *
     * @return A String containing the write bandwidth in bytes per second, unsigned
     */
    public String getAvgWriteBandwidthUnsigned() {
        return Long.toUnsignedString(avgWriteBandwidth);
    }

    /**
     * Returns a double precision float containing the average time to transfer
     * data to/from SystemDiskMetric's source hardware device in bytes per second
     *
     * @return A double containing disk average time to transfer in bytes per second
     */
    public double getAvgTimeToTransfer() {
        return avgTimeToTransfer;
    }

    /**
     * Sets the average time to transfer data in seconds
     *
     * @param avgTimeToTransfer The average time to transfer in seconds
     */
    public void setAvgTimeToTransfer(double avgTimeToTransfer) {
        this.avgTimeToTransfer = avgTimeToTransfer;
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

    public double getMaxUtilization() {
        return maxUtilization;
    }

    public void setMaxUtilization(double maxUtilization) {
        this.maxUtilization = maxUtilization;
    }

    public long getMaxReadBandwidth() {
        return maxReadBandwidth;
    }

    public void setMaxReadBandwidth(long maxReadBandwidth) {
        this.maxReadBandwidth = maxReadBandwidth;
    }

    public long getMaxWriteBandwidth() {
        return maxWriteBandwidth;
    }

    public void setMaxWriteBandwidth(long maxWriteBandwidth) {
        this.maxWriteBandwidth = maxWriteBandwidth;
    }

    public double getMaxTimeToTransfer() {
        return maxTimeToTransfer;
    }

    public void setMaxTimeToTransfer(double maxTimeToTransfer) {
        this.maxTimeToTransfer = maxTimeToTransfer;
    }

    public double getMinUtilization() {
        return minUtilization;
    }

    public void setMinUtilization(double minUtilization) {
        this.minUtilization = minUtilization;
    }

    public long getMinReadBandwidth() {
        return minReadBandwidth;
    }

    public void setMinReadBandwidth(long minReadBandwidth) {
        this.minReadBandwidth = minReadBandwidth;
    }

    public long getMinWriteBandwidth() {
        return minWriteBandwidth;
    }

    public void setMinWriteBandwidth(long minWriteBandwidth) {
        this.minWriteBandwidth = minWriteBandwidth;
    }

    public double getMinTimeToTransfer() {
        return minTimeToTransfer;
    }

    public void setMinTimeToTransfer(double minTimeToTransfer) {
        this.minTimeToTransfer = minTimeToTransfer;
    }
}
