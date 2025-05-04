package com.gibbonsdimarco.yamec.app.service;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Service responsible for collecting system metrics at regular intervals
 * and saving them to the database
 */
@Service
public class SystemMetricsCollector {

    private static final Logger logger = LoggerFactory.getLogger(SystemMetricsCollector.class);
    private final ApplicationDataService applicationDataService;
    private final CpuHardwareInformationService cpuHardwareService;
    private final MemoryHardwareInformationService memoryHardwareService;
    private final DiskHardwareInformationService diskHardwareService;
    private final NicHardwareInformationService nicHardwareService;

    private final SystemMonitorManagerJNI monitor;
    private CpuHardwareInformation cpuInfo;
    private MemoryHardwareInformation memoryInfo;
    private java.util.List<DiskHardwareInformation> diskInfo;
    private java.util.List<NicHardwareInformation> nicInfo;

    @Autowired
    public SystemMetricsCollector(
            ApplicationDataService applicationDataService,
            CpuHardwareInformationService cpuHardwareService,
            MemoryHardwareInformationService memoryHardwareService,
            DiskHardwareInformationService diskHardwareService,
            NicHardwareInformationService nicHardwareService,
            SystemMonitorManagerJNI monitor) {
        this.applicationDataService = applicationDataService;
        this.cpuHardwareService = cpuHardwareService;
        this.memoryHardwareService = memoryHardwareService;
        this.diskHardwareService = diskHardwareService;
        this.nicHardwareService = nicHardwareService;

        this.monitor = monitor;
    }

    @PostConstruct
    public void initialize() {
        logger.info("Initializing SystemMetricsCollector");
        try {
            // Don't create a new monitor instance, use the injected one
            if (monitor == null) {
                logger.warn("No SystemMonitorManagerJNI instance available - metrics collection will be disabled");
                return;
            }

            // Get hardware information once during initialization
            cpuInfo = monitor.getCpuHardwareInformation();
            memoryInfo = monitor.getMemoryHardwareInformation();
            diskInfo = monitor.getDiskHardwareInformation();
            nicInfo = monitor.getNicHardwareInformation();

            // Save the hardware information to the database
            if (cpuInfo != null) {
                logger.info("CPU Hardware Information: {}", cpuInfo);
                cpuInfo = cpuHardwareService.saveCpuInformation(cpuInfo);
            }

            if (memoryInfo != null) {
                logger.info("Memory Hardware Information: {}", memoryInfo);
                memoryInfo = memoryHardwareService.saveMemoryInformation(memoryInfo);
            }

            if (diskInfo != null && !diskInfo.isEmpty()) {
                logger.info("Disk Hardware Information:");
                for (DiskHardwareInformation disk : diskInfo) {
                    logger.info(disk.toString());
                }

                diskInfo = diskHardwareService.saveDiskInformation(diskInfo);
            }

            if (nicInfo != null && !nicInfo.isEmpty()) {
                logger.info("NIC Hardware Information:");
                for (NicHardwareInformation nicDevice : nicInfo) {
                    logger.info(nicDevice.toString());
                }

                nicInfo = nicHardwareService.saveNicInformation(nicInfo);
            }


        } catch (Exception e) {
            logger.error("Failed to initialize SystemMetricsCollector", e);
        }
    }


    /**
     * Scheduled task that runs every 10 seconds to collect the latest system metrics
     * and save them to the database
     */
    @Scheduled(fixedRate = 10000) // Run every 10 seconds
    public void collectAndSaveMetrics() {
        if (monitor == null) {
            logger.debug("SystemMonitorManagerJNI not available - skipping metrics collection");
            return;
        }

        try {
            logger.debug("Collecting system metrics...");



            // Collect metrics at the start
            ArrayList<ProcessMetric> processMetricList = new ArrayList<>();
            ArrayList<SystemCpuMetric> cpuMetrics = new ArrayList<>();
            ArrayList<SystemMemoryMetric> memoryMetrics = new ArrayList<>();
            ArrayList<SystemDiskMetric> diskMetrics = new ArrayList<>();
            ArrayList<SystemNicMetric> nicMetrics = new ArrayList<>();

            // First data collection point
            monitor.collectCounterData();
            ArrayList<ProcessMetric> initialProcessMetrics = monitor.getProcessMetrics();
            SystemCpuMetric initialCpuMetric = monitor.getCpuMetrics();
            SystemMemoryMetric initialMemoryMetric = monitor.getMemoryMetrics();
            java.util.ArrayList<SystemDiskMetric> initialDiskMetrics = monitor.getDiskMetrics();
            java.util.ArrayList<SystemNicMetric> initialNicMetrics = monitor.getNicMetrics();
            Timestamp startTimestamp = new Timestamp(System.currentTimeMillis());

            // Set timestamps on all metrics
            for (ProcessMetric processMetric : initialProcessMetrics) {
                processMetric.setTimestamp(startTimestamp);
            }
            for (SystemDiskMetric diskMetric : initialDiskMetrics) {
                diskMetric.setTimestamp(startTimestamp);
            }
            for (SystemNicMetric nicMetric : initialNicMetrics) {
                nicMetric.setTimestamp(startTimestamp);
            }

            if (initialCpuMetric != null) {
                initialCpuMetric.setTimestamp(startTimestamp);
                if (cpuInfo != null) {
                    initialCpuMetric.setCpu(cpuInfo);
                }
                cpuMetrics.add(initialCpuMetric);
            }

            if (initialMemoryMetric != null) {
                initialMemoryMetric.setTimestamp(startTimestamp);
                if (memoryInfo != null) {
                    initialMemoryMetric.setMemory(memoryInfo);
                }
                memoryMetrics.add(initialMemoryMetric);
            }

            // Wait a bit before collecting again to calculate rates
            Thread.sleep(5000); // 5-second interval

            // Second data collection point
            monitor.collectCounterData();
            ArrayList<ProcessMetric> finalProcessMetrics = monitor.getProcessMetrics();
            SystemCpuMetric finalCpuMetric = monitor.getCpuMetrics();
            SystemMemoryMetric finalMemoryMetric = monitor.getMemoryMetrics();
            java.util.ArrayList<SystemDiskMetric> finalDiskMetrics = monitor.getDiskMetrics();
            java.util.ArrayList<SystemNicMetric> finalNicMetrics = monitor.getNicMetrics();
            Timestamp endTimestamp = new Timestamp(System.currentTimeMillis());

            // Set timestamps on all metrics
            for (ProcessMetric processMetric : finalProcessMetrics) {
                processMetric.setTimestamp(endTimestamp);
            }
            for (SystemDiskMetric diskMetric : finalDiskMetrics) {
                diskMetric.setTimestamp(startTimestamp);
            }
            for (SystemNicMetric nicMetric : finalNicMetrics) {
                nicMetric.setTimestamp(startTimestamp);
            }

            if (finalCpuMetric != null) {
                finalCpuMetric.setTimestamp(endTimestamp);
                if (cpuInfo != null) {
                    finalCpuMetric.setCpu(cpuInfo);
                }
                cpuMetrics.add(finalCpuMetric);
            }

            if (finalMemoryMetric != null) {
                finalMemoryMetric.setTimestamp(endTimestamp);
                if (memoryInfo != null) {
                    finalMemoryMetric.setMemory(memoryInfo);
                }
                memoryMetrics.add(finalMemoryMetric);
            }

            // Combine metrics
            processMetricList.addAll(initialProcessMetrics);
            processMetricList.addAll(finalProcessMetrics);
            diskMetrics.addAll(initialDiskMetrics);
            diskMetrics.addAll(finalDiskMetrics);
            nicMetrics.addAll(initialNicMetrics);
            nicMetrics.addAll(finalNicMetrics);

            // Calculate duration in seconds
            int duration = (int)((endTimestamp.getTime() - startTimestamp.getTime()) / 1000);

            // Save metrics to database
            applicationDataService.saveApplicationMetrics(processMetricList, startTimestamp, duration);
            cpuHardwareService.saveCpuMetrics(cpuMetrics, startTimestamp, duration);
            memoryHardwareService.saveMemoryMetrics(memoryMetrics, startTimestamp, duration);
            diskHardwareService.saveDiskMetrics(diskMetrics, startTimestamp, duration, diskInfo);
            nicHardwareService.saveNicMetrics(nicMetrics, startTimestamp, duration, nicInfo);

            logger.debug("System metrics collected and saved successfully");

        } catch (Exception e) {
            logger.error("Failed to collect and save metrics", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("SystemMetricsCollector cleanup - monitor will be closed by Spring context");
    }
}