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

    // Services
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
    @Scheduled(fixedRate = 1000) // Run every 1 second
    public void collectAndSaveMetrics() {
        if (monitor == null) {
            logger.debug("SystemMonitorManagerJNI not available - skipping metrics collection");
            return;
        }

        try {
            logger.debug("Collecting system metrics...");
            long lastCollectionTime = monitor.getLastCollectionTime().getTime();

            ArrayList<SystemCpuMetric> cpuMetrics = new ArrayList<>();
            ArrayList<SystemMemoryMetric> memoryMetrics = new ArrayList<>();

            // Data collection point
            monitor.collectCounterData();
            ArrayList<ProcessMetric> processMetrics = monitor.getProcessMetrics();
            SystemCpuMetric cpuMetric = monitor.getCpuMetrics();
            SystemMemoryMetric memoryMetric = monitor.getMemoryMetrics();
            ArrayList<SystemDiskMetric> diskMetrics = monitor.getDiskMetrics();
            ArrayList<SystemNicMetric> nicMetrics = monitor.getNicMetrics();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Calculate duration in seconds
            int duration = (int)((now.getTime() - lastCollectionTime) / 1000);

            // Avoid divide by zero error
            if (duration == 0) {
                duration = 1;
            }

            // Set timestamps on all metrics
            if (processMetrics != null) {
                for (ProcessMetric processMetric : processMetrics) {
                    processMetric.setTimestamp(now);
                }
                applicationDataService.saveApplicationMetrics(processMetrics, now, duration);
            }

            if (diskMetrics != null) {
                for (SystemDiskMetric diskMetric : diskMetrics) {
                    diskMetric.setTimestamp(now);
                }
                diskHardwareService.saveDiskMetrics(diskMetrics, now, duration, diskInfo);
            }

            if (nicMetrics != null) {
                for (SystemNicMetric nicMetric : nicMetrics) {
                    nicMetric.setTimestamp(now);
                }
                nicHardwareService.saveNicMetrics(nicMetrics, now, duration, nicInfo);
            }

            if (cpuMetric != null) {
                cpuMetric.setTimestamp(now);
                if (cpuInfo != null) {
                    cpuMetric.setCpu(cpuInfo);
                    cpuMetrics.add(cpuMetric);
                    cpuHardwareService.saveCpuMetrics(cpuMetrics, now, duration);
                }
            }

            if (memoryMetric != null) {
                memoryMetric.setTimestamp(now);
                if (memoryInfo != null) {
                    memoryMetric.setMemory(memoryInfo);
                    memoryMetrics.add(memoryMetric);
                    memoryHardwareService.saveMemoryMetrics(memoryMetrics, now, duration);
                }

            }
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