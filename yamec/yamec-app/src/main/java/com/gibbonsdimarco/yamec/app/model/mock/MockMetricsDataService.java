package com.gibbonsdimarco.yamec.app.model.mock;

import com.gibbonsdimarco.yamec.app.model.DiskData;
import com.gibbonsdimarco.yamec.app.model.MetricsData;
import com.gibbonsdimarco.yamec.app.model.NicData;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Arrays;

@Service
public class MockMetricsDataService {
    private final Random random = new Random();
    
    // Disk type options
    private final List<String> diskTypes = Arrays.asList("SSD", "HDD", "NVMe");
    
    // Network interface type options
    private final List<String> nicTypes = Arrays.asList("Ethernet", "WiFi", "Bluetooth", "Fiber");

    public MetricsData getCurrentMetrics() {
        MetricsData metrics = new MetricsData();

        // Set mock CPU and memory values
        metrics.setCpuUsage(random.nextDouble() * 100);
        metrics.setTotalMemory(16 * 1024 * 1024 * 1024L); // 16GB
        metrics.setUsedMemory((long) (metrics.getTotalMemory() * random.nextDouble()));
        metrics.setFreeMemory(metrics.getTotalMemory() - metrics.getUsedMemory());

        // Add two disk drives to metrics
        addMockDiskData(metrics, 0, "System Drive", 500);
        addMockDiskData(metrics, 1, "Data Drive", 2000);

        // Add two network interfaces to metrics
        addMockNicData(metrics, "Primary Network", "eth0", nicTypes.get(0));
        addMockNicData(metrics, "Wireless Network", "wlan0", nicTypes.get(1));

        return metrics;
    }

    private void addMockDiskData(MetricsData metrics, long diskNumber, String name, int sizeInGB) {
        DiskData disk = new DiskData();
        
        // Set basic disk properties
        disk.setDeviceId(UUID.randomUUID());
        disk.setFriendlyName(name);
        disk.setDiskNumber(diskNumber);
        disk.setDiskType(diskTypes.get(random.nextInt(diskTypes.size())));
        
        // Set disk capacity (in bytes)
        long diskCapacity = sizeInGB * 1024L * 1024L * 1024L;
        disk.setDiskCapacity(diskCapacity);
        
        // Set usage percentages
        double usagePercent = 30 + random.nextDouble() * 60; // 30% to 90% usage
        disk.setAvgDiskUsage(usagePercent);
        disk.setMaxDiskUsage(usagePercent + (random.nextDouble() * 10));
        disk.setMinDiskUsage(Math.max(0, usagePercent - (random.nextDouble() * 10)));
        
        // Set read/write speeds (in bytes per second)
        long readSpeed = random.nextInt(500) * 1024L * 1024L; // 0-500 MB/s
        disk.setAvgBytesReadPerSecond(readSpeed);
        disk.setMaxBytesReadPerSecond((long)(readSpeed * (1.2 + random.nextDouble() * 0.3)));
        disk.setMinBytesReadPerSecond((long)(readSpeed * (0.7 + random.nextDouble() * 0.2)));
        
        long writeSpeed = random.nextInt(400) * 1024L * 1024L; // 0-400 MB/s
        disk.setAvgBytesWrittenPerSecond(writeSpeed);
        disk.setMaxBytesWrittenPerSecond((long)(writeSpeed * (1.2 + random.nextDouble() * 0.3)));
        disk.setMinBytesWrittenPerSecond((long)(writeSpeed * (0.7 + random.nextDouble() * 0.2)));
        
        // Set transfer times
        double transferTime = 2.0 + random.nextDouble() * 8.0; // 2-10 ms
        disk.setAvgTimeToTransfer(transferTime);
        disk.setMaxTimeToTransfer(transferTime + random.nextDouble() * 5.0);
        disk.setMinTimeToTransfer(Math.max(0, transferTime - random.nextDouble() * 1.5));
        
        // Add partitions (if needed)
        disk.setPartitions(Arrays.asList("C:", "D:"));
        
        metrics.addDisk(disk);
    }

    private void addMockNicData(MetricsData metrics, String friendlyName, String label, String nicType) {
        NicData nic = new NicData();
        
        // Set basic NIC properties
        nic.setNicId(UUID.randomUUID());
        nic.setFriendlyName(friendlyName);
        nic.setLabel(label);
        nic.setNicType(nicType);
        
        // Set network traffic data
        long sent = random.nextLong(200 * 1024 * 1024); // Up to 200MB sent
        nic.setAvgNetworkSent(sent);
        nic.setMaxNetworkSent((long)(sent * (1.2 + random.nextDouble() * 0.3)));
        nic.setMinNetworkSent((long)(sent * (0.7 + random.nextDouble() * 0.2)));
        
        long received = random.nextLong(400 * 1024 * 1024); // Up to 400MB received
        nic.setAvgNetworkReceived(received);
        nic.setMaxNetworkReceived((long)(received * (1.2 + random.nextDouble() * 0.3)));
        nic.setMinNetworkReceived((long)(received * (0.7 + random.nextDouble() * 0.2)));
        
        metrics.addNic(nic);
    }
}