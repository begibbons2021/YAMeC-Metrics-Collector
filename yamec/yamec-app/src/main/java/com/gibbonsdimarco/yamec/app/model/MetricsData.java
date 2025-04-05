package com.gibbonsdimarco.yamec.app.model;

public class MetricsData {
    private double cpuUsage;
    private long totalMemory;
    private long freeMemory;
    private long usedMemory;
    private long diskTotal;
    private long diskFree;
    private long diskUsed;
    private long networkSent;
    private long networkReceived;

    public MetricsData() {
        this.cpuUsage = 0;
        this.totalMemory = 0;
        this.freeMemory = 0;
        this.usedMemory = 0;
        this.diskTotal = 0;
        this.diskFree = 0;
        this.diskUsed = 0;
        this.networkSent = 0;
        this.networkReceived = 0;
    }

    public MetricsData(double cpuUsage, long totalMemory, long freeMemory, long usedMemory, long diskTotal, long diskFree, long diskUsed, long networkSent, long networkReceived) {
        this.cpuUsage = cpuUsage;
        this.totalMemory = totalMemory;
        this.freeMemory = freeMemory;
        this.usedMemory = usedMemory;
        this.diskTotal = diskTotal;
        this.diskFree = diskFree;
        this.diskUsed = diskUsed;
        this.networkSent = networkSent;
        this.networkReceived = networkReceived;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public long getDiskTotal() {
        return diskTotal;
    }

    public void setDiskTotal(long diskTotal) {
        this.diskTotal = diskTotal;
    }

    public long getDiskFree() {
        return diskFree;
    }

    public void setDiskFree(long diskFree) {
        this.diskFree = diskFree;
    }

    public long getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(long diskUsed) {
        this.diskUsed = diskUsed;
    }

    public long getNetworkSent() {
        return networkSent;
    }

    public void setNetworkSent(long networkSent) {
        this.networkSent = networkSent;
    }

    public long getNetworkReceived() {
        return networkReceived;
    }

    public void setNetworkReceived(long networkReceived) {
        this.networkReceived = networkReceived;
    }
}