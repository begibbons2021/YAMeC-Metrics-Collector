package com.gibbonsdimarco.yamec.app;

import com.gibbonsdimarco.yamec.app.data.*;
import com.gibbonsdimarco.yamec.app.jni.SystemMonitorManagerJNI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YamecApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void systemMonitorDataTests() {
        assert(testSystemMonitorManager());
    }

    boolean testSystemMonitorManager() {
        System.err.println("Testing System Monitor Manager...");

        SystemMonitorManagerJNI monitor;

        try {
            System.err.println("Creating System Monitor Manager...");
            monitor = new SystemMonitorManagerJNI();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create System Monitor Manager.");
            return false;
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
            monitor.close();
            return false;
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
            monitor.close();
            return false;
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
            monitor.close();
            return false;
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
            monitor.close();
            return false;
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
            monitor.close();
            return false;
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
            return false;
        }

        System.err.println("Testing complete.");

        return true;
    }

}
