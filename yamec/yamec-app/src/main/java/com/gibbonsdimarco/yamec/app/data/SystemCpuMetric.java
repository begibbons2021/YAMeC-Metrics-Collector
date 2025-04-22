package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

/**
 * Contains collected CPU metrics passed from the SystemMonitorManager
 *
 */
@Entity
@Table(name = "cpu_metrics", indexes = {
    @Index(name = "idx_cpu_metrics_timestamp", columnList = "timestamp")
})
public class SystemCpuMetric extends SystemDeviceMetric {

    /**
     * The percentage of CPU utilization of this SystemCpuMetric's source CPU
     */
    @Column(name = "usage", nullable = false)
    private double usage;

    /**
     * The temperature of the CPU in Celsius
     */
    @Column(name = "temperature")
    private Double temperature;

    /**
     * Creates a SystemCpuMetric object instance defined with the deviceName
     * and usage percentage passed by parameter
     *
     * @param deviceName The name of the hardware device which generated this SystemCpuMetric
     * @param usage The percent of the hardware device's CPU used when this metric
     *              was collected
     */
    public SystemCpuMetric(String deviceName, double usage) {
        super(deviceName);
        this.usage = usage;
    }

    /**
     * Creates a SystemCpuMetric object instance defined with the deviceName,
     * usage percentage, and temperature passed by parameter
     *
     * @param deviceName The name of the hardware device which generated this SystemCpuMetric
     * @param usage The percent of the hardware device's CPU used when this metric
     *              was collected
     * @param temperature The temperature of the CPU in Celsius
     */
    public SystemCpuMetric(String deviceName, double usage, double temperature) {
        super(deviceName);
        this.usage = usage;
        this.temperature = temperature;
    }

    /**
     * No-Args constructor required by JPA to allow reflection
     */
    public SystemCpuMetric() {

    }

    /**
     * Returns a double precision float containing the percentage of CPU utilization
     * of this SystemCpuMetric's source hardware device
     *
     * @return A double containing CPU utilization as a percent
     */
    public double getUsage() {
        return usage;
    }

    /**
     * Sets the CPU utilization percentage
     *
     * @param usage The CPU utilization percentage
     */
    public void setUsage(double usage) {
        this.usage = usage;
    }

    /**
     * Returns the temperature of the CPU in Celsius
     *
     * @return A Double containing the CPU temperature in Celsius
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * Sets the CPU temperature
     *
     * @param temperature The CPU temperature in Celsius
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
