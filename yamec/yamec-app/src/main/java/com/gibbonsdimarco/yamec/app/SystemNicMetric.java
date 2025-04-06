package com.gibbonsdimarco.yamec.app;

/**
 * Contains collected hardware network interface metrics passed from the
 * SystemMonitorManager
 *
 */
public class SystemNicMetric extends SystemDeviceMetric {

    /**
     * The number of bits sent per second currently supported
     * on the device (at the time of metrics collection)
     */
    private long nicBandwidth;

    /**
     * The number of bytes sent per second on the device
     * (at the time of metrics collection)
     */
    private long bytesSent;

    /**
     * The number of bytes received per second on the device
     * (at the time of metrics collection)
     */
    private long bytesReceived;

    /**
     * Whether the NIC bandwidth should be represented using an
     * unsigned value
     */
    private boolean nicBandwidthIsUnsigned = false;

    /**
     * Whether the bytes sent should be represented using an
     * unsigned value
     */
    private boolean bytesSentIsUnsigned = false;

    /**
     * Whether the bytes received should be represented using an
     * unsigned value
     */
    private boolean bytesReceivedIsUnsigned = false;

    public SystemNicMetric(String deviceName,
                           long nicBandwidth,
                           long bytesSent,
                           long bytesReceived) {
        super(deviceName);
        this.nicBandwidth = nicBandwidth;
        this.bytesSent = bytesSent;
        this.bytesReceived = bytesReceived;

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
     * Returns whether the bytesReceived value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether bytesReceived is unsigned
     * (true) or not (false)
     */
    public boolean isBytesReceivedUnsigned() {
        return bytesReceivedIsUnsigned;
    }

}
