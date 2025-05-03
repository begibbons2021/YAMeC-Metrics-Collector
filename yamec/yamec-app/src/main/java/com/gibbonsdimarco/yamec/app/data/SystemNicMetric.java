package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

/**
 * Contains collected hardware network interface metrics passed from the
 * SystemMonitorManager
 *
 */
@Entity
@Table(name = "system_nic_metrics", indexes = {
    @Index(name = "idx_system_nic_metrics_timestamp", columnList = "timestamp")
})
public class SystemNicMetric extends SystemDeviceMetric {

    /**
     * A locally used String which provides the human-readable name of the NIC device
     * this SystemNicMetric pertains to
     */
    @Transient
    private String deviceName;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nic_id", nullable = false,
            foreignKey = @ForeignKey(name="fk_nic_hardware_information"))
    private NicHardwareInformation nic;


    /**
     * The number of bits sent per second currently supported
     * on the device (at the time of metrics collection)
     */
//    @Column(name = "nic_bandwidth", nullable = false)
    @Transient
    private long operatingBandwidth;

    /**
     * The number of bits sent per second by the NIC device this
     * SystemNicMetric pertains to, on average, during its timespan
     */
    @Column(name = "avg_send_bandwidth", nullable = false)
    private long avgSendBandwidth;

    /**
     * The number of bits received per second by the NIC device this
     * SystemNicMetric pertains to, on average, during its timespan
     */
    @Column(name = "avg_receive_bandwidth", nullable = false)
    private long avgReceiveBandwidth;

    /**
     * The maximum number of bits sent per second by the NIC device this
     * SystemNicMetric pertains to during its timespan
     */
    @Column(name = "max_send_bandwidth", nullable = false)
    private long maxSendBandwidth;

    /**
     * The maximum number of bits received per second by the NIC device this
     * SystemNicMetric pertains to during its timespan
     */
    @Column(name = "max_receive_bandwidth", nullable = false)
    private long maxReceiveBandwidth;

    /**
     * The minimum number of bits sent per second by the NIC device this
     * SystemNicMetric pertains to during its timespan
     */
    @Column(name = "min_send_bandwidth", nullable = false)
    private long minSendBandwidth;

    /**
     * The minimum number of bits received per second by the NIC device this
     * SystemNicMetric pertains to during its timespan
     */
    @Column(name = "min_receive_bandwidth", nullable = false)
    private long minReceiveBandwidth;

    /**
     * Whether the NIC bandwidth should be represented using an
     * unsigned value
     */
    @Transient
    private boolean operatingBandwidthIsUnsigned;

    /**
     * Whether the bytes sent should be represented using an
     * unsigned value
     */
    @Column(name = "send_bandwidth_is_unsigned")
    private boolean sendBandwidthIsUnsigned;

    /**
     * Whether the bytes received should be represented using an
     * unsigned value
     */
    @Column(name = "receive_bandwidth_is_unsigned")
    private boolean receiveBandwidthIsUnsigned;

    /**
     * Constructs a SystemNicMetric object instance with the default duration of 1 second and
     * average, maximum, and minimum fields being set to the same value for all fields reported
     * to the database
     *
     * @param deviceName The friendly name of the NIC device this metric pertains to
     * @param operatingBandwidth The current operating bandwidth (in bits per second) of the NIC device this metric
     *                     pertains to. The device may not be using the full bandwidth which this device is
     *                     operating at this moment of time
     * @param sendBandwidth The number of bits per second sent from the NIC device this metric pertains to
     *                      at the time of collection
     * @param receiveBandwidth The number of bits per second sent from the NIC device this metric pertains to
     *                      at the time of collection
     * @param operatingBandwidthIsUnsigned Whether operatingBandwidth is unsigned (true) or not (false)
     * @param sendBandwidthIsUnsigned Whether sendBandwidth is unsigned (true) or not (false)
     * @param receiveBandwidthIsUnsigned Whether receiveBandwidth is unsigned (true) or not (false)
     */
    public SystemNicMetric(String deviceName,
                           long operatingBandwidth,
                           long sendBandwidth,
                           long  receiveBandwidth,
                           boolean operatingBandwidthIsUnsigned,
                           boolean sendBandwidthIsUnsigned,
                           boolean receiveBandwidthIsUnsigned) {
        this.setDuration(1);
        this.deviceName = deviceName;
        this.operatingBandwidth = operatingBandwidth;
        this.avgSendBandwidth = sendBandwidth;
        this.avgReceiveBandwidth = receiveBandwidth;
        this.operatingBandwidthIsUnsigned = operatingBandwidthIsUnsigned;
        this.sendBandwidthIsUnsigned = sendBandwidthIsUnsigned;
        this.receiveBandwidthIsUnsigned = receiveBandwidthIsUnsigned;
    }

    public SystemNicMetric() {

    }

    /**
     * Returns the number of bits per second of bandwidth passed through
     * the device (at the time of metrics collection) when this SystemNicMetric
     * was collected. This value returns signed
     *
     * @return A long integer containing the NIC bandwidth bytes per second
     */
    public long getOperatingBandwidth() {
        return operatingBandwidth;
    }

    /**
     * Sets the NIC bandwidth in bits per second
     *
     * @param operatingBandwidth The NIC bandwidth in bits per second
     */
    public void setOperatingBandwidth(long operatingBandwidth) {
        this.operatingBandwidth = operatingBandwidth;
    }

    /**
     * Returns the number of bits per second of bandwidth passed through
     * the device (at the time of metrics collection) when this SystemNicMetric
     * was collected as an unsigned String
     *
     * @return A String containing the amount of bandwidth in bytes per second, unsigned
     */
    public String getOperatingBandwidthUnsigned() {
        return Long.toUnsignedString(operatingBandwidth);
    }

    /**
     * Returns the number of bytes sent per second on the device (at the time
     * of metrics collection) when this SystemNicMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the amount of data sent in bits per second
     */
    public long getAvgSendBandwidth() {
        return avgSendBandwidth;
    }

    /**
     * Sets the number of bits sent per second
     *
     * @param avgSendBandwidth The number of bits sent per second
     */
    public void setAvgSendBandwidth(long avgSendBandwidth) {
        this.avgSendBandwidth = avgSendBandwidth;
    }

    /**
     * Returns the number of bits received per second on the device, on average,
     * when this SystemNicMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the amount of data received in bits per second
     */
    public long getAvgReceiveBandwidth() {
        return avgReceiveBandwidth;
    }

    /**
     * Sets the number of bits received per second, on average, of this SystemNicMetric
     *
     * @param avgReceiveBandwidth The average number of bits received per second
     */
    public void setAvgReceiveBandwidth(long avgReceiveBandwidth) {
        this.avgReceiveBandwidth = avgReceiveBandwidth;
    }

    /**
     * Returns the number of bits received per second on the device, on average,
     * when this SystemNicMetric was collected.
     * This value returns signed
     *
     * @return A long integer containing the amount of data received in bits per second
     */
    public long getMaxSendBandwidth() {
        return maxSendBandwidth;
    }

    public void setMaxSendBandwidth(long maxSendBandwidth) {
        this.maxSendBandwidth = maxSendBandwidth;
    }

    public long getMaxReceiveBandwidth() {
        return maxReceiveBandwidth;
    }

    public void setMaxReceiveBandwidth(long maxReceiveBandwidth) {
        this.maxReceiveBandwidth = maxReceiveBandwidth;
    }

    public long getMinSendBandwidth() {
        return minSendBandwidth;
    }

    public void setMinSendBandwidth(long minSendBandwidth) {
        this.minSendBandwidth = minSendBandwidth;
    }

    public long getMinReceiveBandwidth() {
        return minReceiveBandwidth;
    }

    public void setMinReceiveBandwidth(long minReceiveBandwidth) {
        this.minReceiveBandwidth = minReceiveBandwidth;
    }

    /**
     * Returns the number of bits sent per second on the device (at the time
     * of metrics collection) when this SystemNicMetric was collected as an
     * unsigned String
     *
     * @return A String containing the amount of data sent in bits per second, unsigned
     */
    public String getSendBandwidthUnsigned() {
        return Long.toUnsignedString(avgSendBandwidth);
    }


    /**
     * Returns the number of bits received per second on the device (at the time
     * of metrics collection) when this SystemNicMetric was collected as an
     * unsigned String
     *
     * @return A String containing the amount of data received in bits per second, unsigned
     */
    public String getReceiveBandwidthUnsigned() {
        return Long.toUnsignedString(avgReceiveBandwidth);
    }

    /**
     * Returns whether the operatingBandwidth value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether operatingBandwidth is unsigned
     * (true) or not (false)
     */
    public boolean isOperatingBandwidthUnsigned() {
        return operatingBandwidthIsUnsigned;
    }

    /**
     * Sets whether the NIC bandwidth value should be treated as unsigned
     *
     * @param operatingBandwidthIsUnsigned Whether to treat the value as unsigned
     */
    public void setOperatingBandwidthUnsigned(boolean operatingBandwidthIsUnsigned) {
        this.operatingBandwidthIsUnsigned = operatingBandwidthIsUnsigned;
    }

    /**
     * Returns whether the SendBandwidth value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether SendBandwidth is unsigned
     * (true) or not (false)
     */
    public boolean isSendBandwidthUnsigned() {
        return sendBandwidthIsUnsigned;
    }

    /**
     * Sets whether the bits sent value should be treated as unsigned
     *
     * @param SendBandwidthIsUnsigned Whether to treat the value as unsigned
     */
    public void setSendBandwidthUnsigned(boolean SendBandwidthIsUnsigned) {
        this.sendBandwidthIsUnsigned = SendBandwidthIsUnsigned;
    }

    /**
     * Returns whether the ReceiveBandwidth value is supposed to be signed
     * or unsigned as a boolean value
     *
     * @return A boolean specifying whether ReceiveBandwidth is unsigned
     * (true) or not (false)
     */
    public boolean isReceiveBandwidthUnsigned() {
        return receiveBandwidthIsUnsigned;
    }

    /**
     * Sets whether the bits received value should be treated as unsigned
     *
     * @param receiveBandwidthIsUnsigned Whether to treat the value as unsigned
     */
    public void setReceiveBandwidthUnsigned(boolean receiveBandwidthIsUnsigned) {
        this.receiveBandwidthIsUnsigned = receiveBandwidthIsUnsigned;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public NicHardwareInformation getNic() {
        return nic;
    }

    public void setNic(NicHardwareInformation nic) {
        this.nic = nic;
    }
}
