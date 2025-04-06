package com.gibbonsdimarco.yamec.app.data;

/**
 * Contains collected GPU metrics passed from the SystemMonitorManager
 *
 */
public class SystemGpuMetric extends SystemDeviceMetric {

    /**
     * The percentage of GPU utilization of this SystemGpuMetric's source GPU
     */
    private double usage;

    /**
     * Creates a SystemGpuMetric object instance defined with the deviceName
     * and usage percentage passed by parameter
     *
     * @param deviceName The name of the hardware device which generated this SystemGpuMetric
     * @param usage The percent of the hardware device's GPU used when this metric
     *              was collected
     */
    public SystemGpuMetric(String deviceName, double usage) {
        super(deviceName);
        this.usage = usage;
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

}
