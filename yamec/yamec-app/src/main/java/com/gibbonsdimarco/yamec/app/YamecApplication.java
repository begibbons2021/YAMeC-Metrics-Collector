package com.gibbonsdimarco.yamec.app;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YamecApplication {
//    public static String getExecutableLocation()
//    {
//        try {
//            java.net.URL execURL = YamecApplication.class.getProtectionDomain().getCodeSource().getLocation();
//
//            if (execURL != null) {
//                return Paths.get(execURL.toURI()).toString();
//            }
//            else {
//                System.err.println("Failed to get executable directory: A URL could not be resolved.");
//                return null;
//            }
//
//        }
//        catch(SecurityException | URISyntaxException e)
//        {
//            System.err.println("Failed to get executable directory: " + e.getMessage());
//
//            return null;
//        }
//    }
//

//    private static void setupApplicationFileSystem() {
//        java.io.File yamecHome = new java.io.File(System.getProperty("user.home") + "/.yamec-home");
//
//        if (yamecHome.exists()) {
//            if (!yamecHome.isDirectory()) {
//                System.err.println("Cannot create the YAMEC Home directory because a file exists with the name 'yamec'.");
//                System.exit(1);
//            }
//            else {
//                return;
//            }
//        }
//
//        try {
//            yamecHome.mkdir();
//        }
//        catch (Exception e) {
//
//        }
//
//    }

// Test finite components
//

    private static SystemMonitorManagerJNI monitor;

    private static void testSystemMonitorManager() {
        System.err.println("Testing System Monitor Manager...");

        try {
            System.err.println("Creating System Monitor Manager...");
            monitor = new SystemMonitorManagerJNI();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create System Monitor Manager.");
            return;
        }

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
            System.err.println("Closing System Monitor Manager... ");
            monitor.close();

            if (!monitor.isClosed()) {
                throw new Exception("The status of the System Monitor Manager did not change to closed.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to close System Monitor Manager.");
            return;
        }

        System.err.println("Testing complete.");
    }

    public static void main(String[] args) {
        testSystemMonitorManager();

        System.out.println(System.getProperty("user.home"));

        SpringApplication.run(YamecApplication.class, args);
        System.out.println("Yamec Application Started");

    }

}
