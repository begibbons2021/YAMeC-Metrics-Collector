package com.gibbonsdimarco.yamec.app;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import com.gibbonsdimarco.yamec.app.service.RealMetricsDataService;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
public class YamecApplication {
    private static final Logger logger = LoggerFactory.getLogger(YamecApplication.class);

    private static java.io.File yamecHome;

    private static SystemMonitorManagerJNI monitor = null;

    /**
     * <p>Prepares the application to store all necessary files in the application home directory.</p>
     * <p>This is a hidden folder in the user's home directory. If the hidden folder already exists,
     * the folder is loaded. If not, it is created. </p>
     * <p>If the folder cannot be loaded or cannot be created, the program displays an error message
     * and exits gracefully.</p>
     */
    private static void setupApplicationFileSystem() {
        yamecHome = new java.io.File(System.getProperty("user.home") + "/.yamec-home");

        logger.info("SETUP - Attempting to access YAMeC Home directory: {}", yamecHome);

        boolean homeDirectoryExists = yamecHome.exists();
        if (homeDirectoryExists) {
            if (!yamecHome.isDirectory()) {
                logger.error("SETUP - Cannot create the YAMeC Home directory because "
                        + "a file exists with the name 'yamec'.");
                logger.error("SETUP - Exiting due to application setup failure (cannot load home directory).");
                javax.swing.JOptionPane.showMessageDialog(null,
                        """
                                YAMeC cannot be loaded because there is a file with the name '.yamec-home' \
                                in your home directory.
                                The file conflicts with the home directory of the application.
                                Please delete or rename the file, then try running YAMeC again."""
                        ,
                        "YAMeC - Error Starting Up",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }

        if (!homeDirectoryExists) {
            // Create the '.yamec-home' folder
            logger.info("SETUP - No YAMeC Home directory found. Creating the YAMeC Home directory in: {}",
                    yamecHome);
            boolean directoryCreationSuccess = false;
            try {
                directoryCreationSuccess = yamecHome.mkdir();
            }
            catch (Exception e) {
                logger.error("SETUP - Creation of the YAMeC Home directory failed because "
                        + "of an exception: {} - {}",
                        e.getCause(), e.getMessage());
                // Log stack trace contents
                StackTraceElement[] stackTraceElements = e.getStackTrace();
                logger.error("SETUP - Directory Creation Stack Trace [0]: ");
                for (int i = 0; i < stackTraceElements.length; i++) {
                    logger.error("SETUP - Directory Creation Stack Trace [{}]: {}", i+1, stackTraceElements[i]);
                }
            }

            if (!directoryCreationSuccess) {
                logger.error("SETUP - Exiting due to application setup failure (cannot create home directory).");
                javax.swing.JOptionPane.showMessageDialog(null,
                        """
                                YAMeC cannot be loaded because the '.yamec-home' directory could not be created \
                                during first-time application setup.
                                Please try running YAMeC again and ensure your anti-virus is not blocking \
                                YAMeC from running and creating files."""
                        ,
                        "YAMeC - Error Starting Up",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(-2);
            }
        }




        logger.info("SETUP - YAMeC Home directory found and loaded successfully: {}",
                yamecHome);

    }

    /**
     * Attempts to initialize the System Monitor component of YAMeC. If it cannot be initialized or appears
     * not to be initialized properly, the application will be exited.
     */
    private static void initializeSystemMonitorManager() {
        try {
            System.err.println("Starting System Monitor Manager...");
            monitor = new SystemMonitorManagerJNI();
        }
        catch (Exception e) {
            logger.error("SETUP - Cannot load the SystemMonitorManager because of an exception: {} - {}",
                            e.getCause(), e.getMessage());
            // Log stack trace contents
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error("SETUP - Monitor Creation Stack Trace [0]: ");
            for (int i = 0; i < stackTraceElements.length; i++) {
                logger.error("SETUP - Monitor Creation Stack Trace [{}]: {}", i+1, stackTraceElements[i]);
            }
        }

        if (monitor == null) {
            logger.error("SETUP - Exiting due to application setup failure (cannot initialize system monitor).");
            javax.swing.JOptionPane.showMessageDialog(null,
                    """
                            YAMeC cannot be loaded because it could not load the system monitoring\
                            components.
                            Please try running YAMeC again and ensure your anti-virus is not blocking\
                            YAMeC from running and creating files."""
                    ,
                    "YAMeC - Error Starting Up",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-3);
        }

        // Manually collect counter data (acts as a bit of a health check)
        // It's okay if this gets interrupted this once
        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            logger.error("""
                            SETUP - System Monitor - The timer to wait to collect data \
                            from the SystemMonitorManager was interrupted.""");
        }

        try
        {
            int dataCollectionSuccess = monitor.collectCounterData();
            if (dataCollectionSuccess != 0)
            {
                logger.error("SETUP - System Monitor - Data collection failed with status code: {}",
                                dataCollectionSuccess);
            }
        }
        catch (Exception e) {
            logger.error("SETUP - System Monitor - Cannot collect data because of an exception: {} - {}",
                            e.getCause(), e.getMessage());
            // Log stack trace contents
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error("SETUP - System Monitor Test Stack Trace [0]: ");
            for (int i = 0; i < stackTraceElements.length; i++) {
                logger.error("SETUP - System Monitor Test Stack Trace [{}]: {}", i+1, stackTraceElements[i]);
            }
            // Exit with error message
            javax.swing.JOptionPane.showMessageDialog(null,
                    """
                            YAMeC cannot be loaded because the system monitoring components\
                            are not working as expected.
                            Please try running YAMeC again and ensure your anti-virus is not blocking\
                            YAMeC from running and creating files."""
                    ,
                    "YAMeC - Error Starting Up",
                    JOptionPane.ERROR_MESSAGE);

            System.exit(-4);
        }

    }

    /**
     * <p>Closes all resources and ends all background processes pertaining to YAMeC to prepare
     * the system for shutdown/exit.</p>
     * <p>This should be run whenever YAMeC is to be closed. It should never throw an
     * exception and should ensure a graceful exit of the application with minimal possibility
     * of a resource leak (within our [YAMeC team's] control).</p>
     */
    @PreDestroy
    private static void cleanUp() {
        if (monitor != null) {
            try {
                logger.info("CLEANUP - Closing the System Monitor... ");
                monitor.close();
            }
            catch (Exception e) {
                logger.error("CLEANUP - Closure of the System Monitor Component failed because "
                                + "of an exception: {} - {}",
                        e.getCause(), e.getMessage());
                // Log stack trace contents
                StackTraceElement[] stackTraceElements = e.getStackTrace();
                logger.error("CLEANUP - System Monitor Closure Stack Trace [0]: ");
                for (int i = 0; i < stackTraceElements.length; i++) {
                    logger.error("CLEANUP - System Monitor Closure Stack Trace [{}]: {}", i+1, stackTraceElements[i]);
                }
            }

            if (monitor.isOpen()) {
                logger.warn("CLEANUP - The System Monitor component did not close properly.");
                logger.warn("CLEANUP - This could be due to an unexpected error. ");
                logger.warn("CLEANUP - There is a chance of a resource leak as a result.");
            }
        }
    }

//    @Scheduled(fixedRate = 1000, initialDelay = 1000) // Happens every second
    public static void fetchMetrics() {
        System.out.println("Monitor: " + monitor);
        // Short circuit in case the monitor is not yet set
        if (monitor == null) {
            return;
        }

        try {
            int counterDataCollectionStatus = monitor.collectCounterData();

            if (counterDataCollectionStatus == 0) {

                // Storage buffers for data
                final SystemCpuMetric[] cpuMetrics = new SystemCpuMetric[1];
                final SystemMemoryMetric[] memoryMetrics = new SystemMemoryMetric[1];
                java.util.List<SystemDiskMetric> diskMetricList = new java.util.ArrayList<SystemDiskMetric>();
                java.util.List<SystemNicMetric> nicMetricList = new java.util.ArrayList<SystemNicMetric>();
                java.util.List<ProcessMetric> processMetricList = new java.util.ArrayList<ProcessMetric>();


                Thread[] metricsThreads = new Thread[5];
                metricsThreads[0] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cpuMetrics[0] = monitor.getCpuMetrics();
                    }
                });

                metricsThreads[1] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        memoryMetrics[0] = monitor.getMemoryMetrics();
                    }
                });

                metricsThreads[2] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        diskMetricList.clear();
                        diskMetricList.addAll(monitor.getDiskMetrics());
                    }
                });

                metricsThreads[3] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        nicMetricList.clear();
                        nicMetricList.addAll(monitor.getNicMetrics());
                    }
                });


                metricsThreads[4] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        processMetricList.clear();
                        processMetricList.addAll(monitor.getProcessMetrics());
                    }
                });

                // Run threads
                metricsThreads[0].start();
                metricsThreads[1].start();
                metricsThreads[2].start();
                metricsThreads[3].start();
                metricsThreads[4].start();

                // Join threads together
                metricsThreads[0].join();
                metricsThreads[1].join();
                metricsThreads[2].join();
                metricsThreads[3].join();
                metricsThreads[4].join();

                if (cpuMetrics[0] != null) {
                    System.err.printf("CPU Information: \n\t%s\n\t\tUsage: %.1f%%\n",
                            cpuMetrics[0].getDeviceName(), cpuMetrics[0].getUsage());
                } else {
                    System.err.println("CPU Information: \n\tNo CPU Metrics Found");
                }

                if (memoryMetrics[0] != null) {
                    // Calculate the actual virtual memory use from the amount of committed memory used.
                    double bytesVirtualMemoryInUse = memoryMetrics[0].getCommittedVirtualMemoryBytes();
                    System.err.println("Memory Information:");
                    System.err.printf("\tAvailable Memory (Physical Memory): %s bytes\n",
                            memoryMetrics[0].getPhysicalMemoryAvailableUnsigned());
                    System.err.printf("\tVirtual Memory Committed: %s\n",
                            memoryMetrics[0].getVirtualMemoryCommitted());
                    System.err.printf("\tVirtual Memory In-Use: %f%% (~%.0f bytes)\n",
                            memoryMetrics[0].getCommittedVirtualMemoryUsage(),
                            bytesVirtualMemoryInUse);
                } else {
                    System.err.println("Memory Information: \n\tNo Memory Metrics Found");
                }

                if (!diskMetricList.isEmpty()) {
                    System.err.println("Disk Information:");
                    System.err.printf("\tDisk Instances Present (including _Total): %d\n", diskMetricList.size());
                    for (SystemDiskMetric diskMetric : diskMetricList) {
                        // Skip total system disk use
                        if (diskMetric.getDeviceName().compareTo("_Total") == 0) {
                            continue;
                        }
                        System.err.printf("\t%s\n", diskMetric.getDeviceName());
                        System.err.printf("\t\tUsage: %f%%\n", diskMetric.getUsage());
                        System.err.printf("\t\tRead Bandwidth: %s bytes/sec\n", diskMetric.getReadBandwidthUnsigned());
                        System.err.printf("\t\tWrite Bandwidth: %s bytes/sec\n", diskMetric.getWriteBandwidthUnsigned());
                        System.err.printf("\t\tAverage Transfer Rate: %f bytes/sec\n", diskMetric.getAverageTimeToTransfer());
                    }
                } else {
                    System.err.println("Disk Information: \n\tNo Disk Metrics Found");
                }


                if (!nicMetricList.isEmpty()) {
                    System.err.println("NIC Information:");
                    System.err.printf("\tNIC Instances Present (including _Total): %d\n", nicMetricList.size());
                    for (SystemNicMetric nicMetric : nicMetricList) {
                        // Skip total system NIC use
                        if (nicMetric.getDeviceName().compareTo("_Total") == 0) {
                            continue;
                        }
                        System.err.printf("\t%s\n", nicMetric.getDeviceName());
                        System.err.printf("\t\tCurrent Operation Bandwidth: %s bps\n", nicMetric.getNicBandwidthUnsigned());
                        System.err.printf("\t\tBytes Sent: %s bytes/sec\n", nicMetric.getBytesSentUnsigned());
                        System.err.printf("\t\tWrite Bandwidth: %s bytes/sec\n", nicMetric.getBytesReceivedUnsigned());
                    }
                } else {
                    System.err.println("NIC Information: \n\tNo NIC Metrics Found");
                }

                if (!processMetricList.isEmpty()) {
                    for (int i = 0; i < 5; i++) {
                        java.util.Random random = new java.util.Random();
                        int selectedProcessIndex = random.nextInt(processMetricList.size());

                        System.err.printf("%s (pid: %d)\n", processMetricList.get(selectedProcessIndex).getProcessName(),
                                processMetricList.get(selectedProcessIndex).getProcessId());
                        System.err.printf("\tCPU Usage: %f\n", processMetricList.get(selectedProcessIndex).getCpuUsage());
                        System.err.printf("\tPhysical Memory Usage: %d bytes\n",
                                processMetricList.get(selectedProcessIndex).getPhysicalMemoryUsage());
                        System.err.printf("\tVirtual Memory Usage: %d bytes\n",
                                processMetricList.get(selectedProcessIndex).getVirtualMemoryUsage());
                        System.err.println();
                    }
                }

            }
        }
        catch (Exception e) {
            logger.error("System Monitor - Cannot fetch data because of an exception: {} - {}",
                    e.getCause(), e.getMessage());
            // Log stack trace contents
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            logger.error("System Monitor Fetch Stack Trace [0]: ");
            for (int i = 0; i < stackTraceElements.length; i++) {
                logger.error("System Monitor Fetch Stack Trace [{}]: {}", i+1, stackTraceElements[i]);
            }
        }
    }

    /**
     * A function which
     */
    private static void testSystemMonitorManager() {
        System.err.println("Testing System Monitor Manager...");

        try {
            System.err.println("Testing CPU Metrics Retrieval...");
            SystemCpuMetric cpuMetrics = monitor.getCpuMetrics();
            if (cpuMetrics != null) {
               System.err.printf("CPU Information: \n\t%s\n\t\tUsage: %.1f%%\n",
                                    cpuMetrics.getDeviceName(), cpuMetrics.getUsage());
            }
            else {
                System.err.println("CPU Information: \n\tNo CPU Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve CPU Metrics.");
            return;
        }

        try {
            System.err.println("Testing GPU Metrics Retrieval...");
            SystemGpuMetric gpuMetrics = monitor.getGpuMetrics();
            if (gpuMetrics != null) {
               System.err.printf("GPU Information: \n\t%s\n\t\tUsage: %.1f%%\n",
                                    gpuMetrics.getDeviceName(), gpuMetrics.getUsage());
            }
            else {
                System.err.println("GPU Information: \n\tNo GPU Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve GPU Metrics.");
            return;
        }

        try {
            System.err.println("Testing Memory Metrics Retrieval...");
            SystemMemoryMetric memoryMetrics = monitor.getMemoryMetrics();
            if (memoryMetrics != null) {
                // Calculate the actual virtual memory use from the amount of committed memory used.
                double bytesVirtualMemoryInUse = memoryMetrics.getCommittedVirtualMemoryBytes();

                System.err.println("Memory Information:");
                System.err.printf("\tAvailable Memory (Physical Memory): %s bytes\n",
                                    memoryMetrics.getPhysicalMemoryAvailableUnsigned());
                System.err.printf("\tVirtual Memory Committed: %s\n",
                                    memoryMetrics.getVirtualMemoryCommitted());
                System.err.printf("\tVirtual Memory In-Use: %f%% (~%.0f bytes)\n",
                                    memoryMetrics.getCommittedVirtualMemoryUsage(),
                                    bytesVirtualMemoryInUse);
            }
            else {
                System.err.println("Memory Information: \n\tNo Memory Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve Memory Metrics.");
            return;
        }

        try {
            System.err.println("Testing Disk Metrics Retrieval...");
            java.util.ArrayList<SystemDiskMetric> diskMetrics = monitor.getDiskMetrics();
            if (diskMetrics != null) {

                System.err.println("Disk Information:");
                System.err.printf("\tDisk Instances Present (including _Total): %d\n", diskMetrics.size());

                for (SystemDiskMetric diskMetric : diskMetrics) {
                    // Skip total system disk use
                    if (diskMetric.getDeviceName().compareTo("_Total") == 0) {
                        continue;
                    }

                    System.err.printf("\t%s\n", diskMetric.getDeviceName());
                    System.err.printf("\t\tUsage: %f%%\n", diskMetric.getUsage());
                    System.err.printf("\t\tRead Bandwidth: %s bytes/sec\n", diskMetric.getReadBandwidthUnsigned());
                    System.err.printf("\t\tWrite Bandwidth: %s bytes/sec\n", diskMetric.getWriteBandwidthUnsigned());
                    System.err.printf("\t\tAverage Transfer Rate: %f bytes/sec\n", diskMetric.getAverageTimeToTransfer());
                }
            }
            else {
                System.err.println("Disk Information: \n\tNo Disk Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve Disk Metrics.");
            return;
        }

        try {
            System.err.println("Testing NIC Metrics Retrieval...");
            java.util.ArrayList<SystemNicMetric> nicMetrics = monitor.getNicMetrics();
            if (nicMetrics != null) {

                System.err.println("NIC Information:");
                System.err.printf("\tNIC Instances Present (including _Total): %d\n", nicMetrics.size());

                for (SystemNicMetric nicMetric : nicMetrics) {
                    // Skip total system NIC use
                    if (nicMetric.getDeviceName().compareTo("_Total") == 0) {
                        continue;
                    }

                    System.err.printf("\t%s\n", nicMetric.getDeviceName());
                    System.err.printf("\t\tCurrent Operation Bandwidth: %s bps\n", nicMetric.getNicBandwidthUnsigned());
                    System.err.printf("\t\tBytes Sent: %s bytes/sec\n", nicMetric.getBytesSentUnsigned());
                    System.err.printf("\t\tWrite Bandwidth: %s bytes/sec\n", nicMetric.getBytesReceivedUnsigned());
                }
            }
            else {
                System.err.println("NIC Information: \n\tNo NIC Metrics Found");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to retrieve NIC Metrics.");
            return;
        }


        System.err.println("Testing complete.");
    }


    public static void main(String[] args) {
        setupApplicationFileSystem();
        initializeSystemMonitorManager();

        logger.info("User home directory: {}", System.getProperty("user.home"));
//        testSystemMonitorManager();

        SpringApplication.run(YamecApplication.class, args);

        logger.info("Yamec Application Started");

        for (int i = 0; i < 5; i++) {
            fetchMetrics();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while sleeping", e);
            }
        }

    }

}
