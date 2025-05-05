package com.gibbonsdimarco.yamec.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiskData {
    private UUID deviceId;
    private String friendlyName;
    private long diskNumber;
    private List<String> partitions;
    private String diskType;
    private long diskCapacity;
    private double avgDiskUsage;
    private double maxDiskUsage;
    private double minDiskUsage;
    private long avgBytesReadPerSecond;
    private long maxBytesReadPerSecond;
    private long minBytesReadPerSecond;
    private long avgBytesWrittenPerSecond;
    private long maxBytesWrittenPerSecond;
    private long minBytesWrittenPerSecond;
    private double avgTimeToTransfer;
    private double maxTimeToTransfer;
    private double minTimeToTransfer;

    public DiskData(UUID deviceId, String friendlyName, long diskNumber, List<String> partitions, String diskType, long diskCapacity, double avgDiskUsage, double maxDiskUsage, double minDiskUsage, long avgBytesReadPerSecond, long maxBytesReadPerSecond, long minBytesReadPerSecond, long avgBytesWrittenPerSecond, long maxBytesWrittenPerSecond, long minBytesWrittenPerSecond, double avgTimeToTransfer, double maxTimeToTransfer, double minTimeToTransfer) {
        this.deviceId = deviceId;
        this.friendlyName = friendlyName;
        this.diskNumber = diskNumber;
        this.partitions = partitions;
        this.diskType = diskType;
        this.diskCapacity = diskCapacity;
        this.avgDiskUsage = avgDiskUsage;
        this.maxDiskUsage = maxDiskUsage;
        this.minDiskUsage = minDiskUsage;
        this.avgBytesReadPerSecond = avgBytesReadPerSecond;
        this.maxBytesReadPerSecond = maxBytesReadPerSecond;
        this.minBytesReadPerSecond = minBytesReadPerSecond;
        this.avgBytesWrittenPerSecond = avgBytesWrittenPerSecond;
        this.maxBytesWrittenPerSecond = maxBytesWrittenPerSecond;
        this.minBytesWrittenPerSecond = minBytesWrittenPerSecond;
        this.avgTimeToTransfer = avgTimeToTransfer;
        this.maxTimeToTransfer = maxTimeToTransfer;
        this.minTimeToTransfer = minTimeToTransfer;
    }

    public DiskData() {
        this.deviceId = null;
        this.friendlyName = "";
        this.partitions = new ArrayList<>();
        this.diskType = "";
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public long getDiskNumber() {
        return diskNumber;
    }

    public void setDiskNumber(long diskNumber) {
        this.diskNumber = diskNumber;
    }

    public List<String> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<String> partitions) {
        this.partitions = partitions;
    }

    public String getDiskType() {
        return diskType;
    }

    public void setDiskType(String diskType) {
        this.diskType = diskType;
    }

    public long getDiskCapacity() {
        return diskCapacity;
    }

    public void setDiskCapacity(long diskCapacity) {
        this.diskCapacity = diskCapacity;
    }

    public double getAvgDiskUsage() {
        return avgDiskUsage;
    }

    public void setAvgDiskUsage(double avgDiskUsage) {
        this.avgDiskUsage = avgDiskUsage;
    }

    public double getMaxDiskUsage() {
        return maxDiskUsage;
    }

    public void setMaxDiskUsage(double maxDiskUsage) {
        this.maxDiskUsage = maxDiskUsage;
    }

    public double getMinDiskUsage() {
        return minDiskUsage;
    }

    public void setMinDiskUsage(double minDiskUsage) {
        this.minDiskUsage = minDiskUsage;
    }

    public long getAvgBytesReadPerSecond() {
        return avgBytesReadPerSecond;
    }

    public void setAvgBytesReadPerSecond(long avgBytesReadPerSecond) {
        this.avgBytesReadPerSecond = avgBytesReadPerSecond;
    }

    public long getMaxBytesReadPerSecond() {
        return maxBytesReadPerSecond;
    }

    public void setMaxBytesReadPerSecond(long maxBytesReadPerSecond) {
        this.maxBytesReadPerSecond = maxBytesReadPerSecond;
    }

    public long getMinBytesReadPerSecond() {
        return minBytesReadPerSecond;
    }

    public void setMinBytesReadPerSecond(long minBytesReadPerSecond) {
        this.minBytesReadPerSecond = minBytesReadPerSecond;
    }

    public long getAvgBytesWrittenPerSecond() {
        return avgBytesWrittenPerSecond;
    }

    public void setAvgBytesWrittenPerSecond(long avgBytesWrittenPerSecond) {
        this.avgBytesWrittenPerSecond = avgBytesWrittenPerSecond;
    }

    public long getMaxBytesWrittenPerSecond() {
        return maxBytesWrittenPerSecond;
    }

    public void setMaxBytesWrittenPerSecond(long maxBytesWrittenPerSecond) {
        this.maxBytesWrittenPerSecond = maxBytesWrittenPerSecond;
    }

    public long getMinBytesWrittenPerSecond() {
        return minBytesWrittenPerSecond;
    }

    public void setMinBytesWrittenPerSecond(long minBytesWrittenPerSecond) {
        this.minBytesWrittenPerSecond = minBytesWrittenPerSecond;
    }

    public double getAvgTimeToTransfer() {
        return avgTimeToTransfer;
    }

    public void setAvgTimeToTransfer(double avgTimeToTransfer) {
        this.avgTimeToTransfer = avgTimeToTransfer;
    }

    public double getMaxTimeToTransfer() {
        return maxTimeToTransfer;
    }

    public void setMaxTimeToTransfer(double maxTimeToTransfer) {
        this.maxTimeToTransfer = maxTimeToTransfer;
    }

    public double getMinTimeToTransfer() {
        return minTimeToTransfer;
    }

    public void setMinTimeToTransfer(double minTimeToTransfer) {
        this.minTimeToTransfer = minTimeToTransfer;
    }
}

