package com.gibbonsdimarco.yamec.app.data;

import jakarta.persistence.*;

/**
 * Contains collected GPU metrics passed from the SystemMonitorManager
 *
 */
@Entity
@Table(name = "gpu_metrics", indexes = {
    @Index(name = "idx_gpu_metrics_timestamp", columnList = "timestamp")
})
public class SystemGpuMetric extends SystemDeviceMetric {

    /**
     * A locally used String which provides the brand string/friendly name of the GPU
     * this SystemGpuMetric pertains to
     */
    @Transient
    private String deviceName;

    /**
     * The percentage of GPU utilization of this SystemGpuMetric's source GPU
     */
    @Column(name = "usage", nullable = false)
    private double usage;

    /**
     * The temperature of the GPU in Celsius
     */
    @Column(name = "temperature")
    private Double temperature;

    /**
     * The amount of dedicated memory used in bytes
     */
    @Column(name = "dedicated_memory_use")
    private Long dedicatedMemoryUse;

    /**
     * The amount of shared memory used in bytes
     */
    @Column(name = "shared_memory_use")
    private Long sharedMemoryUse;

    /**
     * Creates a SystemGpuMetric object instance defined with the deviceName
     * and usage percentage passed by parameter
     *
     * @param deviceName The name of the hardware device which generated this SystemGpuMetric
     * @param usage The percent of the hardware device's GPU used when this metric
     *              was collected
     */
    public SystemGpuMetric(String deviceName, double usage) {
        this.deviceName = deviceName;
        this.usage = usage;
    }

    /**
     * Creates a SystemGpuMetric object instance defined with the deviceName,
     * usage percentage, temperature, and memory usage passed by parameter
     *
     * @param deviceName The name of the hardware device which generated this SystemGpuMetric
     * @param usage The percent of the hardware device's GPU used when this metric
     *              was collected
     * @param temperature The temperature of the GPU in Celsius
     * @param dedicatedMemoryUse The amount of dedicated memory used in bytes
     * @param sharedMemoryUse The amount of shared memory used in bytes
     */
    public SystemGpuMetric(String deviceName, double usage, double temperature, 
                          long dedicatedMemoryUse, long sharedMemoryUse) {
        this.deviceName = deviceName;
        this.usage = usage;
        this.temperature = temperature;
        this.dedicatedMemoryUse = dedicatedMemoryUse;
        this.sharedMemoryUse = sharedMemoryUse;
    }

    public SystemGpuMetric() {

    }

    /**
     * Returns a double precision float containing the percentage of GPU utilization
     * of this SystemGpuMetric's source hardware device
     *
     * @return A double containing GPU utilization as a percent
     */
    public double getUsage() {
        return usage;
    }

    /**
     * Sets the GPU utilization percentage
     *
     * @param usage The GPU utilization percentage
     */
    public void setUsage(double usage) {
        this.usage = usage;
    }

    /**
     * Returns the temperature of the GPU in Celsius
     *
     * @return A Double containing the GPU temperature in Celsius
     */
    public Double getTemperature() {
        return temperature;
    }

    /**
     * Sets the GPU temperature
     *
     * @param temperature The GPU temperature in Celsius
     */
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    /**
     * Returns the amount of dedicated memory used in bytes
     *
     * @return A Long containing the dedicated memory usage in bytes
     */
    public Long getDedicatedMemoryUse() {
        return dedicatedMemoryUse;
    }

    /**
     * Sets the amount of dedicated memory used
     *
     * @param dedicatedMemoryUse The dedicated memory usage in bytes
     */
    public void setDedicatedMemoryUse(Long dedicatedMemoryUse) {
        this.dedicatedMemoryUse = dedicatedMemoryUse;
    }

    /**
     * Returns the amount of shared memory used in bytes
     *
     * @return A Long containing the shared memory usage in bytes
     */
    public Long getSharedMemoryUse() {
        return sharedMemoryUse;
    }

    /**
     * Sets the amount of shared memory used
     *
     * @param sharedMemoryUse The shared memory usage in bytes
     */
    public void setSharedMemoryUse(Long sharedMemoryUse) {
        this.sharedMemoryUse = sharedMemoryUse;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
