package com.gibbonsdimarco.yamec.app.model;

import java.util.ArrayList;

public class MetricsData {
    private double cpuUsage;
    private double maxCpuUsage;
    private double minCpuUsage;
    private long totalMemory;
    private long freeMemory;
    private long usedMemory;
    private long maxPhysicalMemory;
    private long minPhysicalMemory;
    private long virtualMemory;
    private long maxVirtualMemory;
    private long minVirtualMemory;
    private ArrayList<DiskData> disks;
    private ArrayList<NicData> nics;

    public MetricsData() {
        this.cpuUsage = 0;
        this.totalMemory = 0;
        this.freeMemory = 0;
        this.usedMemory = 0;
        this.disks = new ArrayList<>();
        this.nics = new ArrayList<>();
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

    public void addDisk(DiskData disk) {
        this.disks.add(disk);
    }

    public ArrayList<DiskData> getDisks() {
        return disks;
    }

    public void setDisks(ArrayList<DiskData> disks) {
        this.disks = disks;
    }

    public void addNic(NicData nic) {
        this.nics.add(nic);
    }

    public ArrayList<NicData> getNics() {
        return nics;
    }

    public void setNics(ArrayList<NicData> nics) {
        this.nics = nics;
    }

    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public void setMaxCpuUsage(double maxCpuUsage) {
        this.maxCpuUsage = maxCpuUsage;
    }

    public double getMinCpuUsage() {
        return minCpuUsage;
    }

    public void setMinCpuUsage(double minCpuUsage) {
        this.minCpuUsage = minCpuUsage;
    }

    public long getMaxPhysicalMemory() {
        return maxPhysicalMemory;
    }

    public void setMaxPhysicalMemory(long maxPhysicalMemory) {
        this.maxPhysicalMemory = maxPhysicalMemory;
    }

    public long getMinPhysicalMemory() {
        return minPhysicalMemory;
    }

    public void setMinPhysicalMemory(long minPhysicalMemory) {
        this.minPhysicalMemory = minPhysicalMemory;
    }

    public long getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(long virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public long getMaxVirtualMemory() {
        return maxVirtualMemory;
    }

    public void setMaxVirtualMemory(long maxVirtualMemory) {
        this.maxVirtualMemory = maxVirtualMemory;
    }

    public long getMinVirtualMemory() {
        return minVirtualMemory;
    }

    public void setMinVirtualMemory(long minVirtualMemory) {
        this.minVirtualMemory = minVirtualMemory;
    }
}