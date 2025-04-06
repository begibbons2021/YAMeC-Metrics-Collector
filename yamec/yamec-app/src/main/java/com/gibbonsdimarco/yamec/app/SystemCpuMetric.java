package com.gibbonsdimarco.yamec.app;

/**
 * Contains collected CPU metrics passed from the SystemMonitorManager
 *
 */
public class SystemCpuMetric extends SystemDeviceMetric {

    /**
     * The percentage of CPU utilization of this SystemCpuMetric's source CPU
     */
    private double usage;

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
     * Returns a double precision float containing the percentage of CPU utilization
     * of this SystemCpuMetric's source hardware device
     *
     * @return A double containing CPU utilization as a percent
     */
    public double getUsage() {
        return usage;
    }

}
