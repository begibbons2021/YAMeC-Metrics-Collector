// YAMeC Dashboard JavaScript

// // Get progress bar color based on percentage
// function getProgressBarColor(percentage) {
//     if (percentage < 35) {
//         return 'var(--secondary-color)'; // Green
//     } else if (percentage < 60) {
//         return 'var(--warning-color)';   // Yellow
//     } else if (percentage < 80) {
//         return 'var(--orange-color)';    // Orange
//     } else {
//         return 'var(--danger-color)';    // Red
//     }
// }

// // Format bytes to human-readable format
// function formatBytes(bytes, decimals = 2) {
//     if (bytes === 0) return '0 Bytes';
//
//     const k = 1024;
//     const dm = decimals < 0 ? 0 : decimals;
//     const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
//
//     const i = Math.floor(Math.log(bytes) / Math.log(k));
//
//     return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
// }
//
// // Format percentage
// function formatPercentage(value) {
//     return value.toFixed(1) + '%';
// }

// Update the dashboard with new metrics data
function updateDashboard(metrics) {
    // CPU Usage
    document.getElementById('cpu-usage').textContent = formatPercentage(metrics.cpuUsage);
    const cpuProgressBar = document.getElementById('cpu-progress');
    cpuProgressBar.style.width = metrics.cpuUsage + '%';
    cpuProgressBar.style.backgroundColor = getProgressBarColor(metrics.cpuUsage);

    // Memory Usage
    const memoryUsagePercent = (metrics.usedMemory / metrics.totalMemory) * 100;
    document.getElementById('memory-usage').textContent = formatPercentage(memoryUsagePercent);
    const memoryProgressBar = document.getElementById('memory-progress');
    memoryProgressBar.style.width = memoryUsagePercent + '%';
    memoryProgressBar.style.backgroundColor = getProgressBarColor(memoryUsagePercent);

    // Memory Details
    document.getElementById('total-memory').textContent = formatBytes(metrics.totalMemory);
    document.getElementById('used-memory').textContent = formatBytes(metrics.usedMemory);
    document.getElementById('free-memory').textContent = formatBytes(metrics.freeMemory);

    // Clear existing disk cards
    const dashboardElement = document.querySelector('.dashboard');
    const existingDiskCards = document.querySelectorAll('.card .card-header h2.card-title');
    existingDiskCards.forEach(titleElement => {
        if (titleElement.textContent.includes('Disk')) {
            const cardElement = titleElement.closest('.card');
            if (cardElement) {
                cardElement.remove();
            }
        }
    });

    // Add disk cards
    if (metrics.disks && metrics.disks.length > 0) {
        // Sort disks by diskNumber for consistent display
        metrics.disks.sort((a, b) => a.diskNumber - b.diskNumber);
        metrics.disks.forEach(disk => {
            const diskCard = document.createElement('div');
            diskCard.className = 'card';

            const diskUsagePercent = disk.avgDiskUsage;

            diskCard.innerHTML = `
                <div class="card-header">
                    <h2 class="card-title">${disk.friendlyName} (Disk ${disk.diskNumber})</h2>
                    <i class="fas fa-hdd card-icon"></i>
                </div>
                <div class="metric">
                    <div class="metric-label">Disk Type</div>
                    <div class="metric-value">${disk.diskType}</div>
                </div>
                <div class="metric">
                    <div class="metric-label">Capacity</div>
                    <div class="metric-value">${formatBytes(disk.diskCapacity)}</div>
                </div>
                <div class="metric">
                    <div class="metric-label">Average Usage</div>
                    <div class="metric-value">${formatPercentage(diskUsagePercent)}</div>
                    <div class="progress-container">
                        <div class="progress-bar progress-disk" style="width: ${diskUsagePercent}%; background-color: ${getProgressBarColor(diskUsagePercent)}"></div>
                    </div>
                </div>
                <div class="metric">
                    <div class="metric-label">Average Read Speed</div>
                    <div class="metric-value">${formatBytesTransferred(disk.avgBytesReadPerSecond)}</div>
                </div>
                <div class="metric">
                    <div class="metric-label">Average Write Speed</div>
                    <div class="metric-value">${formatBytesTransferred(disk.avgBytesWrittenPerSecond)}</div>
                </div>
                <div class="metric">
                    <div class="metric-label">Average Time to Transfer</div>
                    <div class="metric-value">${formatSeconds(disk.avgTimeToTransfer, 2)}</div>
                </div>
            `;

            // Find the memory card to insert after
            const memoryCard = Array.from(document.querySelectorAll('.card')).find(card => {
                const title = card.querySelector('.card-title');
                return title && title.textContent.includes('Memory');
            });

            if (memoryCard) {
                memoryCard.after(diskCard);
            } else {
                // If no memory card found, append to dashboard
                dashboardElement.appendChild(diskCard);
            }
        });
    }

    // Clear existing NIC cards
    const existingNicCards = document.querySelectorAll('.card .card-header h2.card-title');
    existingNicCards.forEach(titleElement => {
        if (titleElement.textContent.includes('Network Interface') || 
            (titleElement.closest('.card').querySelector('.fas.fa-network-wired') !== null)) {
            const cardElement = titleElement.closest('.card');
            if (cardElement) {
                cardElement.remove();
            }
        }
    });

    // Add NIC cards
    if (metrics.nics && metrics.nics.length > 0) {
        // Sort NICs by friendlyName for consistent display
        metrics.nics.sort((a, b) => a.friendlyName.localeCompare(b.friendlyName));
        metrics.nics.forEach(nic => {
            const nicCard = document.createElement('div');
            nicCard.className = 'card';

            nicCard.innerHTML = `
                <div class="card-header">
                    <h2 class="card-title">${nic.friendlyName}</h2>
                    <i class="fas fa-network-wired card-icon"></i>
                </div>
                <div class="metric">
                    <div class="metric-label">Label</div>
                    <div class="metric-value">${nic.label}</div>
                </div>
                <div class="metric">
                    <div class="metric-label">NIC Type</div>
                    <div class="metric-value">${nic.nicType}</div>
                </div>
                <div class="metric">
                    <div class="metric-label">Average Data Sent</div>
                    <div class="metric-value">${formatBitsPerSecond(nic.avgNetworkSent)}</div>
                </div>
                <div class="metric">
                    <div class="metric-label">Average Data Received</div>
                    <div class="metric-value">${formatBitsPerSecond(nic.avgNetworkReceived)}</div>
                </div>
            `;

            // Append to dashboard
            dashboardElement.appendChild(nicCard);
        });
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
