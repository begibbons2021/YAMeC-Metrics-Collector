package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.model.DiskData;
import com.gibbonsdimarco.yamec.app.model.MetricsData;
import com.gibbonsdimarco.yamec.app.model.NicData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter service that converts system metrics from JPA entities to data objects for the view layer
 */
@Service
public class SystemMetricsAdapter {

    private final CpuHardwareInformationService cpuService;
    private final MemoryHardwareInformationService memoryService;
    private final DiskHardwareInformationService diskService;
    private final NicHardwareInformationService nicService;

    @Autowired
    public SystemMetricsAdapter(CpuHardwareInformationService cpuService,
                                MemoryHardwareInformationService memoryService,
                                DiskHardwareInformationService diskService,
                                NicHardwareInformationService nicService) {
        this.cpuService = cpuService;
        this.memoryService = memoryService;
        this.diskService = diskService;
        this.nicService = nicService;
    }

    /**
     * Converts the real system metrics data to the format expected by the views
     * @return MetricsData object with current system metrics
     */
    @Transactional
    public MetricsData getCurrentMetrics() {
        MetricsData metricsData = new MetricsData();

        // Get the latest metrics from the services
        SystemCpuMetric cpuMetric = cpuService.getLatestMetric();
        SystemMemoryMetric memoryMetric = memoryService.getLatestMetric();
        Map<DiskHardwareInformation, SystemDiskMetric> diskMetrics = diskService.getAllDisksWithLatestMetrics();
        Map<NicHardwareInformation, SystemNicMetric> nicMetrics = nicService.getAllNicsWithLatestMetrics();

        // Set CPU usage
        if (cpuMetric != null) {
            metricsData.setCpuUsage(cpuMetric.getAverageUtilization());
        }

        // Set memory metric
        if (memoryMetric != null) {
            MemoryHardwareInformation memoryInfo = memoryMetric.getMemory();

            if (memoryInfo != null) {
                metricsData.setTotalMemory(memoryInfo.getCapacity());
                metricsData.setUsedMemory(memoryMetric.getAveragePhysicalUtilization());
                metricsData.setFreeMemory(metricsData.getTotalMemory() - metricsData.getUsedMemory());
            }
        }

        // Set disk metrics
        if (!diskMetrics.isEmpty()) {
            for (Map.Entry<DiskHardwareInformation, SystemDiskMetric> entry : diskMetrics.entrySet()) {
                DiskHardwareInformation diskInfo = entry.getKey();
                SystemDiskMetric diskMetric = entry.getValue();


                if (diskInfo != null && diskMetric != null) {
                    DiskData disk = new DiskData(
                            diskInfo.getId(),
                            diskInfo.getFriendlyName(),
                            diskInfo.getDiskNumber(),
                            diskInfo.getPartitions(),
                            DiskHardwareInformation.getMediaTypeString(diskInfo.getMediaType()),
                            diskInfo.getCapacity(),
                            diskMetric.getAvgUtilization(),
                            diskMetric.getMaxUtilization(),
                            diskMetric.getMinUtilization(),
                            diskMetric.getAvgReadBandwidth(),
                            diskMetric.getMaxReadBandwidth(),
                            diskMetric.getMinReadBandwidth(),
                            diskMetric.getAvgWriteBandwidth(),
                            diskMetric.getMaxWriteBandwidth(),
                            diskMetric.getMinWriteBandwidth(),
                            diskMetric.getAvgTimeToTransfer(),
                            diskMetric.getMaxTimeToTransfer(),
                            diskMetric.getMinTimeToTransfer());

                    metricsData.addDisk(disk);
                }
            }
        }

        // Set network metrics
        if (!nicMetrics.isEmpty()) {
            for (Map.Entry<NicHardwareInformation, SystemNicMetric> entry : nicMetrics.entrySet()) {
                NicHardwareInformation nicInfo = entry.getKey();
                SystemNicMetric nicMetric = entry.getValue();

                if (nicInfo != null && nicMetric != null) {
                    NicData nic = new NicData(
                            nicInfo.getId(),
                            nicInfo.getFriendlyName(),
                            nicInfo.getLabel(),
                            NicHardwareInformation.getNicTypeString(nicInfo.getNicType()),
                            nicMetric.getAvgSendBandwidth(),
                            nicMetric.getAvgReceiveBandwidth(),
                            nicMetric.getMaxSendBandwidth(),
                            nicMetric.getMaxReceiveBandwidth(),
                            nicMetric.getMinSendBandwidth(),
                            nicMetric.getMinReceiveBandwidth());

                    metricsData.addNic(nic);
                }
            }
//
//            // Convert bandwidth (bits/sec) to bytes over time
//            // This is an approximation - in a real system we'd need to track total bytes over time
//            long duration = nicMetric.getDuration();
//            if (duration <= 0) duration = 1; // Safeguard against division by zero
//
//            // Convert bits to bytes (divide by 8) and multiply by duration in seconds
//            metricsData.setNetworkSent(nicMetric.getAvgSendBandwidth() * duration / 8);
//            metricsData.setNetworkReceived(nicMetric.getAvgReceiveBandwidth() * duration / 8);
        }

        return metricsData;
    }

    /**
     * Converts the real system metrics data to the format expected by the views
     * and aggregates all data for the timespan passed by parameter (startTime and endTime)
     * @return MetricsData object with current system metrics
     */
    @Transactional
    public MetricsData getHistoricalMetrics(Timestamp startTime, Timestamp endTime) {
        MetricsData metricsData = new MetricsData();

        // Get the latest metrics from the services
        List<SystemCpuMetric> cpuMetrics = cpuService.getStoredCpuMetrics(startTime, endTime);
        List<SystemMemoryMetric> memoryMetrics = memoryService.getStoredMemoryMetrics(startTime, endTime);
        List<SystemDiskMetric> diskMetrics = diskService.getStoredDiskMetrics(startTime, endTime);
        List<SystemNicMetric> nicMetrics = nicService.getStoredNicMetrics(startTime, endTime);



        // Set CPU usage
        if (cpuMetrics != null && !cpuMetrics.isEmpty()) {

            int numberOfCpuMetrics = 0;
            // Will hold the sum of CPU utilization in the time specified and then get averaged
            double totalCpuUsage = 0;

            double maxCpuUsage = 0.0;
            double minCpuUsage = Double.MAX_VALUE;

            for (SystemCpuMetric cpuMetric : cpuMetrics) {
                totalCpuUsage += cpuMetric.getAverageUtilization();

                if (maxCpuUsage < cpuMetric.getMaxUtilization()) {
                    maxCpuUsage = cpuMetric.getMaxUtilization();
                }

                if (minCpuUsage > cpuMetric.getMinUtilization()) {
                    minCpuUsage = cpuMetric.getMinUtilization();
                }

                numberOfCpuMetrics++;
            }

            if (numberOfCpuMetrics != 0) {
                metricsData.setCpuUsage(totalCpuUsage / numberOfCpuMetrics);
                metricsData.setMaxCpuUsage(maxCpuUsage);
                metricsData.setMinCpuUsage(minCpuUsage);
            }

        }

        // Set memory metric
        if (memoryMetrics != null && !memoryMetrics.isEmpty()) {
            int numberOfMemoryMetrics = 0;
            // Will hold the sum of physical memory use in the time specified and then get averaged
            long totalPhysicalMemory = 0;
            long maxPhysicalMemory = Long.MIN_VALUE;
            long minPhysicalMemory = Long.MAX_VALUE;

            long totalVirtualMemory = 0;
            long maxVirtualMemory = Long.MIN_VALUE;
            long minVirtualMemory = Long.MAX_VALUE;

            for (SystemMemoryMetric memoryMetric : memoryMetrics) {
                totalPhysicalMemory += memoryMetric.getAveragePhysicalUtilization();
                totalVirtualMemory += memoryMetric.getAverageVirtualUtilization();

                if (maxPhysicalMemory < memoryMetric.getMaxPhysicalUtilization()) {
                    maxPhysicalMemory = memoryMetric.getMaxPhysicalUtilization();
                }

                if (maxVirtualMemory < memoryMetric.getMaxVirtualUtilization()) {
                    maxVirtualMemory = memoryMetric.getMaxVirtualUtilization();
                }

                if (minPhysicalMemory > memoryMetric.getMinPhysicalUtilization()) {
                    minPhysicalMemory = memoryMetric.getMinPhysicalUtilization();
                }
                if (minVirtualMemory > memoryMetric.getMinVirtualUtilization()) {
                    minVirtualMemory = memoryMetric.getMinVirtualUtilization();
                }

                numberOfMemoryMetrics++;
            }

            if (numberOfMemoryMetrics != 0) {
                metricsData.setUsedMemory(totalPhysicalMemory / numberOfMemoryMetrics);
                metricsData.setMaxPhysicalMemory(maxPhysicalMemory);
                metricsData.setMinPhysicalMemory(minPhysicalMemory);
                metricsData.setVirtualMemory(totalVirtualMemory / numberOfMemoryMetrics);
                metricsData.setMaxVirtualMemory(maxVirtualMemory);
                metricsData.setMinVirtualMemory(minVirtualMemory);

            }
        }

        // Set disk metrics
        if (diskMetrics != null && !diskMetrics.isEmpty()) {
            // Keep all disk UUIDs in a hash map and store disk data for a period together
            Map<UUID, DiskData> diskDataMap = new HashMap<>();
            Map<UUID, Integer> diskCountMap = new HashMap<>();

            for (SystemDiskMetric diskMetric : diskMetrics) {
                DiskHardwareInformation diskInfo = diskMetric.getDisk();

                // Add disk info to map if it hasn't already been recorded
                if (!diskDataMap.containsKey(diskInfo.getId())) {
                    DiskData disk = new DiskData();
                    disk.setDeviceId(diskInfo.getId());
                    disk.setFriendlyName(diskInfo.getFriendlyName());
                    disk.setDiskNumber(diskInfo.getDiskNumber());
                    disk.setPartitions(diskInfo.getPartitions());
                    disk.setDiskType(DiskHardwareInformation.getMediaTypeString(diskInfo.getMediaType()));
                    disk.setDiskCapacity(diskInfo.getCapacity());
                    disk.setMaxDiskUsage(0.0);
                    disk.setMinDiskUsage(Double.MAX_VALUE);
                    disk.setMaxBytesReadPerSecond(Long.MIN_VALUE);
                    disk.setMinBytesReadPerSecond(Long.MAX_VALUE);
                    disk.setMaxBytesWrittenPerSecond(Long.MIN_VALUE);
                    disk.setMinBytesWrittenPerSecond(Long.MAX_VALUE);
                    disk.setMaxTimeToTransfer(0.0);
                    disk.setMinTimeToTransfer(Double.MAX_VALUE);

                    diskDataMap.put(diskInfo.getId(), disk);
                    diskCountMap.put(diskInfo.getId(), 0);
                }

                // Get current metrics
                DiskData diskData = diskDataMap.get(diskInfo.getId());

                diskData.setAvgDiskUsage(diskMetric.getAvgUtilization() + diskData.getAvgDiskUsage());
                if (diskMetric.getMaxUtilization() > diskData.getMaxDiskUsage()) {
                    diskData.setMaxDiskUsage(diskMetric.getMaxUtilization());
                }
                if (diskMetric.getMinUtilization() < diskData.getMinDiskUsage()) {
                    diskData.setMinDiskUsage(diskMetric.getMinUtilization());
                }

                diskData.setAvgBytesReadPerSecond(diskMetric.getAvgReadBandwidth()
                                                    + diskData.getAvgBytesReadPerSecond());
                if (diskMetric.getMaxReadBandwidth() > diskData.getMaxBytesReadPerSecond()) {
                    diskData.setMaxBytesReadPerSecond(diskMetric.getMaxReadBandwidth());
                }
                if (diskMetric.getMinReadBandwidth() < diskData.getMinBytesReadPerSecond()) {
                    diskData.setMinBytesReadPerSecond(diskMetric.getMinReadBandwidth());
                }


                diskData.setAvgBytesWrittenPerSecond(diskMetric.getAvgWriteBandwidth()
                                                    + diskData.getAvgBytesWrittenPerSecond());
                if (diskMetric.getMaxWriteBandwidth() > diskData.getMaxBytesWrittenPerSecond()) {
                    diskData.setMaxBytesWrittenPerSecond(diskMetric.getMaxWriteBandwidth());
                }
                if (diskMetric.getMinWriteBandwidth() < diskData.getMinBytesWrittenPerSecond()) {
                    diskData.setMinBytesWrittenPerSecond(diskMetric.getMinWriteBandwidth());
                }

                diskData.setAvgTimeToTransfer(diskMetric.getAvgTimeToTransfer()
                                                + diskData.getAvgTimeToTransfer());
                if (diskMetric.getMaxTimeToTransfer() > diskData.getMaxTimeToTransfer()) {
                    diskData.setMaxTimeToTransfer(diskMetric.getMaxTimeToTransfer());
                }
                if (diskMetric.getMinTimeToTransfer() < diskData.getMinTimeToTransfer()) {
                    diskData.setMinTimeToTransfer(diskMetric.getMinTimeToTransfer());
                }

                // Save updated metrics data
                diskDataMap.put(diskInfo.getId(), diskData);
                diskCountMap.put(diskInfo.getId(), diskCountMap.get(diskInfo.getId()) + 1);

            }

            // Add disks to output object with properly averaged metrics
            for (DiskData diskData : diskDataMap.values()) {
                diskData.setAvgDiskUsage(diskData.getAvgDiskUsage()
                                            / diskCountMap.get(diskData.getDeviceId()));
                diskData.setAvgBytesReadPerSecond(diskData.getAvgBytesReadPerSecond()
                                            / diskCountMap.get(diskData.getDeviceId()));
                diskData.setAvgBytesWrittenPerSecond(diskData.getAvgBytesWrittenPerSecond()
                                            / diskCountMap.get(diskData.getDeviceId()));
                diskData.setAvgTimeToTransfer(diskData.getAvgTimeToTransfer()
                                            / diskCountMap.get(diskData.getDeviceId()));
                metricsData.addDisk(diskData);
            }

        }


        // Set network metrics
        if (nicMetrics != null && !nicMetrics.isEmpty()) {
            // Keep all disk UUIDs in a hash map and store disk data for a period together
            Map<UUID, NicData> nicDataMap = new HashMap<>();
            Map<UUID, Integer> nicCountMap = new HashMap<>();

            for (SystemNicMetric nicMetric : nicMetrics) {
                NicHardwareInformation nicInfo = nicMetric.getNic();

                // Add disk info to map if it hasn't already been recorded
                if (!nicDataMap.containsKey(nicInfo.getId())) {
                    NicData nic = new NicData();
                    nic.setNicId(nicInfo.getId());
                    nic.setFriendlyName(nicInfo.getFriendlyName());
                    nic.setLabel(nicInfo.getLabel());
                    nic.setNicType(NicHardwareInformation.getNicTypeString(nicInfo.getNicType()));
                    nic.setMaxNetworkSent(Long.MIN_VALUE);
                    nic.setMinNetworkSent(Long.MAX_VALUE);
                    nic.setMaxNetworkReceived(Long.MIN_VALUE);
                    nic.setMinNetworkReceived(Long.MAX_VALUE);

                    nicDataMap.put(nicInfo.getId(), nic);
                    nicCountMap.put(nicInfo.getId(), 0);
                }

                // Get current metrics
                NicData nicData = nicDataMap.get(nicInfo.getId());

                nicData.setAvgNetworkSent(nicMetric.getAvgSendBandwidth()
                        + nicData.getAvgNetworkSent());
                if (nicMetric.getMaxSendBandwidth() > nicData.getMaxNetworkSent()) {
                    nicData.setMaxNetworkSent(nicMetric.getMaxSendBandwidth());
                }
                if (nicMetric.getMinSendBandwidth() < nicData.getMinNetworkSent()) {
                    nicData.setMinNetworkSent(nicMetric.getMinSendBandwidth());
                }

                nicData.setAvgNetworkReceived(nicMetric.getAvgReceiveBandwidth()
                        + nicData.getAvgNetworkReceived());
                if (nicMetric.getMaxReceiveBandwidth() > nicData.getMaxNetworkReceived()) {
                    nicData.setMaxNetworkReceived(nicMetric.getMaxReceiveBandwidth());
                }
                if (nicMetric.getMinReceiveBandwidth() < nicData.getMinNetworkReceived()) {
                    nicData.setMinNetworkReceived(nicMetric.getMinReceiveBandwidth());
                }


                // Save updated metrics data
                nicDataMap.put(nicInfo.getId(), nicData);
                nicCountMap.put(nicInfo.getId(), nicCountMap.get(nicInfo.getId()) + 1);

            }

            // Add disks to output object
            for (NicData nicData : nicDataMap.values()) {
                nicData.setAvgNetworkSent(nicData.getAvgNetworkSent()
                        / nicCountMap.get(nicData.getNicId()));
                nicData.setAvgNetworkReceived(nicData.getAvgNetworkReceived()
                        / nicCountMap.get(nicData.getNicId()));

                metricsData.addNic(nicData);
            }

        }

        return metricsData;
    }
}
