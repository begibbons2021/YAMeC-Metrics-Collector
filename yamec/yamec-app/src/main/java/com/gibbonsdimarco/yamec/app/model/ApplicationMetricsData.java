package com.gibbonsdimarco.yamec.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApplicationMetricsData {
    private UUID id;
    private String applicationName;
    private double avgCpuUsage;
    private long avgPhysicalMemoryUsed;
    private long avgVirtualMemoryUsed;
    private double maxCpuUsage;
    private long maxPhysicalMemoryUsed;
    private long maxVirtualMemoryUsed;
    private double minCpuUsage;
    private long minPhysicalMemoryUsed;
    private long minVirtualMemoryUsed;

    public ApplicationMetricsData() {
        this.id = null;
        this.applicationName = "";
        this.avgCpuUsage = 0;
        this.avgPhysicalMemoryUsed = 0;
        this.avgVirtualMemoryUsed = 0;
        this.maxCpuUsage = 0;
        this.maxPhysicalMemoryUsed = 0;
        this.maxVirtualMemoryUsed = 0;
        this.minCpuUsage = 0;
        this.minPhysicalMemoryUsed = 0;
        this.minVirtualMemoryUsed = 0;
    }

    public ApplicationMetricsData(UUID id, String applicationName, double avgCpuUsage, long avgPhysicalMemoryUsed, 
                                 long avgVirtualMemoryUsed, double maxCpuUsage, long maxPhysicalMemoryUsed, 
                                 long maxVirtualMemoryUsed, double minCpuUsage, long minPhysicalMemoryUsed, 
                                 long minVirtualMemoryUsed) {
        this.id = id;
        this.applicationName = applicationName;
        this.avgCpuUsage = avgCpuUsage;
        this.avgPhysicalMemoryUsed = avgPhysicalMemoryUsed;
        this.avgVirtualMemoryUsed = avgVirtualMemoryUsed;
        this.maxCpuUsage = maxCpuUsage;
        this.maxPhysicalMemoryUsed = maxPhysicalMemoryUsed;
        this.maxVirtualMemoryUsed = maxVirtualMemoryUsed;
        this.minCpuUsage = minCpuUsage;
        this.minPhysicalMemoryUsed = minPhysicalMemoryUsed;
        this.minVirtualMemoryUsed = minVirtualMemoryUsed;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public double getAvgCpuUsage() {
        return avgCpuUsage;
    }

    public void setAvgCpuUsage(double avgCpuUsage) {
        this.avgCpuUsage = avgCpuUsage;
    }

    public long getAvgPhysicalMemoryUsed() {
        return avgPhysicalMemoryUsed;
    }

    public void setAvgPhysicalMemoryUsed(long avgPhysicalMemoryUsed) {
        this.avgPhysicalMemoryUsed = avgPhysicalMemoryUsed;
    }

    public long getAvgVirtualMemoryUsed() {
        return avgVirtualMemoryUsed;
    }

    public void setAvgVirtualMemoryUsed(long avgVirtualMemoryUsed) {
        this.avgVirtualMemoryUsed = avgVirtualMemoryUsed;
    }

    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public void setMaxCpuUsage(double maxCpuUsage) {
        this.maxCpuUsage = maxCpuUsage;
    }

    public long getMaxPhysicalMemoryUsed() {
        return maxPhysicalMemoryUsed;
    }

    public void setMaxPhysicalMemoryUsed(long maxPhysicalMemoryUsed) {
        this.maxPhysicalMemoryUsed = maxPhysicalMemoryUsed;
    }

    public long getMaxVirtualMemoryUsed() {
        return maxVirtualMemoryUsed;
    }

    public void setMaxVirtualMemoryUsed(long maxVirtualMemoryUsed) {
        this.maxVirtualMemoryUsed = maxVirtualMemoryUsed;
    }

    public double getMinCpuUsage() {
        return minCpuUsage;
    }

    public void setMinCpuUsage(double minCpuUsage) {
        this.minCpuUsage = minCpuUsage;
    }

    public long getMinPhysicalMemoryUsed() {
        return minPhysicalMemoryUsed;
    }

    public void setMinPhysicalMemoryUsed(long minPhysicalMemoryUsed) {
        this.minPhysicalMemoryUsed = minPhysicalMemoryUsed;
    }

    public long getMinVirtualMemoryUsed() {
        return minVirtualMemoryUsed;
    }

    public void setMinVirtualMemoryUsed(long minVirtualMemoryUsed) {
        this.minVirtualMemoryUsed = minVirtualMemoryUsed;
    }

    public static class ApplicationMetricsDataList {
        private List<ApplicationMetricsData> applications;

        public ApplicationMetricsDataList() {
            this.applications = new ArrayList<>();
        }

        public List<ApplicationMetricsData> getApplications() {
            return applications;
        }

        public void setApplications(List<ApplicationMetricsData> applications) {
            this.applications = applications;
        }

        public void addApplication(ApplicationMetricsData application) {
            this.applications.add(application);
        }
    }
}