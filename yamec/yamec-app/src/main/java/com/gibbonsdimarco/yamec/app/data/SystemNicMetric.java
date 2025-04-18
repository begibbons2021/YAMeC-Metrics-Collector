package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

/**
 * Contains collected hardware network interface metrics passed from the
 * SystemMonitorManager
 *
 */
@Entity
@Table(name = "nic_metrics", indexes = {
    @Index(name = "idx_nic_metrics_timestamp", columnList = "timestamp")
})
public class SystemNicMetric extends SystemDeviceMetric {

    /**
     * The number of bits sent per second currently supported
     * on the device (at the time of metrics collection)
     */
    @Column(name = "nic_bandwidth", nullable = false)
    private long nicBandwidth;

    /**
     * The number of bytes sent per second on the device
     * (at the time of metrics collection)
     */
    @Column(name = "bytes_sent", nullable = false)
    private long bytesSent;

    /**
     * The number of bytes received per second on the device
     * (at the time of metrics collection)
     */
    @Column(name = "bytes_received", nullable = false)
    private long bytesReceived;

    /**
     * Whether the NIC bandwidth should be represented using an
     * unsigned value
     */
    @Column(name = "nic_bandwidth_is_unsigned")
    private boolean nicBandwidthIsUnsigned;

    /**
     * Whether the bytes sent should be represented using an
     * unsigned value
     */
    @Column(name = "bytes_sent_is_unsigned")
    private boolean bytesSentIsUnsigned;

    /**
     * Whether the bytes received should be represented using an
     * unsigned value
     */
    @Column(name = "bytes_received_is_unsigned")
    private boolean bytesReceivedIsUnsigned;

    public SystemNicMetric(String deviceName,
                           long nicBandwidth,
                           long bytesSent,
                           long bytesReceived,
                           boolean nicBandwidthIsUnsigned,
                           boolean bytesSentIsUnsigned,
                           boolean bytesReceivedIsUnsigned) {
        super(deviceName);
        this.nicBandwidth = nicBandwidth;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;
        this.nicBandwidthIsUnsigned = nicBandwidthIsUnsigned;
        this.bytesSentIsUnsigned = bytesSentIsUnsigned;
        this.bytesReceivedIsUnsigned = bytesReceivedIsUnsigned;
    }

    /**
     * Returns the number of bits per second of bandwidth passed through
     * the device (at the time of metrics collection) when this SystemNicMetric
     * was collected. This value returns signed
     *
     * @return A long integer containing the NIC bandwidth bytes per second
     */
    public long getNicBandwidth() {
        return nicBandwidth;
    }

    /**
     * Sets the NIC bandwidth in bits per second
     *
     * @param nicBandwidth The NIC bandwidth in bits per second
     */
    public void setNicBandwidth(long nicBandwidth) {
        this.nicBandwidth = nicBandwidth;
    }

    /**
     * Returns the number of bits per second of bandwidth passed through
     * the device (at the time of metrics collection) when this SystemNicMetric
     * was collected as an unsigned String
     *
     * @return A String containing the amount of bandwidth in bytes per second, unsigned
     */
    public String getNicBandwidthUnsigned() {
        return Long.toUnsignedString(nicBandwidth);
    }

    /**
     * Returns the number of bytes sent per second on the device (at the time
     * of metrics collection) when this SystemNicMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the amount of data sent in bytes per second
     */
    public long getBytesSent() {
        return bytesSent;
    }

    /**
     * Sets the number of bytes sent per second
     *
     * @param bytesSent The number of bytes sent per second
     */
    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    /**
     * Returns the number of bytes sent per second on the device (at the time
     * of metrics collection) when this SystemNicMetric was collected as an
     * unsigned String
     *
     * @return A String containing the amount of data sent in bytes per second, unsigned
     */
    public String getBytesSentUnsigned() {
        return Long.toUnsignedString(bytesSent);
    }

    /**
     * Returns the number of bytes received per second on the device (at the time
     * of metrics collection) when this SystemNicMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the amount of data received in bytes per second
     */
    public long getBytesReceived() {
        return bytesReceived;
    }

    /**
     * Sets the number of bytes received per second
     *
     * @param bytesReceived The number of bytes received per second
     */
    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    /**
     * Returns the number of bytes received per second on the device (at the time
     * of metrics collection) when this SystemNicMetric was collected as an
     * unsigned String
     *
     * @return A String containing the amount of data received in bytes per second, unsigned
     */
    public String getBytesReceivedUnsigned() {
        return Long.toUnsignedString(bytesReceived);
    }

    /**
     * Returns whether the nicBandwidth value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether nicBandwidth is unsigned
     * (true) or not (false)
     */
    public boolean isNicBandwidthUnsigned() {
        return nicBandwidthIsUnsigned;
    }

    /**
     * Sets whether the NIC bandwidth value should be treated as unsigned
     *
     * @param nicBandwidthIsUnsigned Whether to treat the value as unsigned
     */
    public void setNicBandwidthUnsigned(boolean nicBandwidthIsUnsigned) {
        this.nicBandwidthIsUnsigned = nicBandwidthIsUnsigned;
    }

    /**
     * Returns whether the bytesSent value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether bytesSent is unsigned
     * (true) or not (false)
     */
    public boolean isBytesSentUnsigned() {
        return bytesSentIsUnsigned;
    }

    /**
     * Sets whether the bytes sent value should be treated as unsigned
     *
     * @param bytesSentIsUnsigned Whether to treat the value as unsigned
     */
    public void setBytesSentUnsigned(boolean bytesSentIsUnsigned) {
        this.bytesSentIsUnsigned = bytesSentIsUnsigned;
    }

    /**
     * Returns whether the bytesReceived value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether bytesReceived is unsigned
     * (true) or not (false)
     */
    public boolean isBytesReceivedUnsigned() {
        return bytesReceivedIsUnsigned;
    }

    /**
     * Sets whether the bytes received value should be treated as unsigned
     *
     * @param bytesReceivedIsUnsigned Whether to treat the value as unsigned
     */
    public void setBytesReceivedUnsigned(boolean bytesReceivedIsUnsigned) {
        this.bytesReceivedIsUnsigned = bytesReceivedIsUnsigned;
    }
}
