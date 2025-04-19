package com.gibbonsdimarco.yamec.app.data;

/**
 *
 */
public class ProcessMetric {

    /**
     * The name of the application/process this ProcessMetric pertains to
     */
    private String processName;

    /**
     * The ID of the process this ProcessMetric pertains to
     */
    private int processId;

    /**
     * The percentage of CPU utilization of the process this ProcessMetric pertains to
     */
    private double cpuUsage;

    /**
     * The number of bytes of physical memory currently in use by the process this ProcessMetric pertains to
     */
    private long physicalMemoryUsage;

    /**
     * The number of bytes of virtual memory currently in use by the process this ProcessMetric pertains to
     */
    private long virtualMemoryUsage;

    /**
     * Creates a ProcessMetric object with the field values passed by parameter
     *
     * @param processName A string containing the name of the process/executable
     * @param processId An integer containing ID of the process/executable
     * @param cpuUsage A double precision decimal containing the percentage of CPU time used on average across
     *                 all cores/logical processors by the process/executable
     * @param physicalMemoryUsage A long integer containing the amount of physical memory in use by the
     *                            process/executable
     * @param virtualMemoryUsage A long integer containing the amount of virtual memory in use by the
     *                            process/executable
     */
    public ProcessMetric(String processName,
                         int processId,
                         double cpuUsage,
                         long physicalMemoryUsage,
                         long virtualMemoryUsage) {
        this.processName = processName;
        this.processId = processId;
        this.cpuUsage = cpuUsage;
        this.physicalMemoryUsage = physicalMemoryUsage;
        this.virtualMemoryUsage = virtualMemoryUsage;
        // TODO: Update with 64 bit unsigned value support
    }

    public long getVirtualMemoryUsage() {
        return virtualMemoryUsage;
    }

    public long getPhysicalMemoryUsage() {
        return physicalMemoryUsage;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public int getProcessId() {
        return processId;
    }

    public String getProcessName() {
        return processName;
    }
}
