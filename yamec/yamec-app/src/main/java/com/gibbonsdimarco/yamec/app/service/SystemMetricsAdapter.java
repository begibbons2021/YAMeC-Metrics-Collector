package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.model.DiskData;
import com.gibbonsdimarco.yamec.app.model.MetricsData;
import com.gibbonsdimarco.yamec.app.model.NicData;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
}
