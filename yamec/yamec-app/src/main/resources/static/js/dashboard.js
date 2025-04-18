// YAMeC Dashboard JavaScript

// Format bytes to human-readable format
function formatBytes(bytes, decimals = 2) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

// Format percentage
function formatPercentage(value) {
    return value.toFixed(1) + '%';
}

// Update the dashboard with new metrics data
function updateDashboard(metrics) {
    // CPU Usage
    document.getElementById('cpu-usage').textContent = formatPercentage(metrics.cpuUsage);
    document.getElementById('cpu-progress').style.width = metrics.cpuUsage + '%';
    
    // Memory Usage
    const memoryUsagePercent = (metrics.usedMemory / metrics.totalMemory) * 100;
    document.getElementById('memory-usage').textContent = formatPercentage(memoryUsagePercent);
    document.getElementById('memory-progress').style.width = memoryUsagePercent + '%';
    
    // Memory Details
    document.getElementById('total-memory').textContent = formatBytes(metrics.totalMemory);
    document.getElementById('used-memory').textContent = formatBytes(metrics.usedMemory);
    document.getElementById('free-memory').textContent = formatBytes(metrics.freeMemory);
    
    // Disk Usage (if available)
    if (metrics.diskTotal > 0) {
        const diskUsagePercent = (metrics.diskUsed / metrics.diskTotal) * 100;
        document.getElementById('disk-usage').textContent = formatPercentage(diskUsagePercent);
        document.getElementById('disk-progress').style.width = diskUsagePercent + '%';
        
        document.getElementById('total-disk').textContent = formatBytes(metrics.diskTotal);
        document.getElementById('used-disk').textContent = formatBytes(metrics.diskUsed);
        document.getElementById('free-disk').textContent = formatBytes(metrics.diskFree);
    }
    
    // Network Usage (if available)
    if (metrics.networkSent > 0 || metrics.networkReceived > 0) {
        document.getElementById('network-sent').textContent = formatBytes(metrics.networkSent);
        document.getElementById('network-received').textContent = formatBytes(metrics.networkReceived);
    }
    
    // Update timestamp
    document.getElementById('last-updated').textContent = new Date().toLocaleTimeString();
}

// Fetch metrics data from the API
function fetchMetrics() {
    fetch('/api/metrics')
        .then(response => response.json())
        .then(data => {
            updateDashboard(data);
        })
        .catch(error => {
            console.error('Error fetching metrics:', error);
        });
}

// Initialize the dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Initial fetch
    fetchMetrics();
    
    // Set up periodic updates (every second)
    setInterval(fetchMetrics, 1000);
}); 