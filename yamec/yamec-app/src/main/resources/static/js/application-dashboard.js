// YAMeC Application Dashboard JavaScript

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

// Update the application details section with the selected application's metrics
function updateApplicationDetails(appMetrics) {
    // CPU Usage
    document.getElementById('app-cpu-avg').textContent = formatPercentage(appMetrics.avgCpuUsage);
    document.getElementById('app-cpu-max').textContent = formatPercentage(appMetrics.maxCpuUsage);
    document.getElementById('app-cpu-min').textContent = formatPercentage(appMetrics.minCpuUsage);
    document.getElementById('app-cpu-progress').style.width = appMetrics.avgCpuUsage + '%';

    // Physical Memory
    document.getElementById('app-physical-memory-avg').textContent = formatBytes(appMetrics.avgPhysicalMemoryUsed);
    document.getElementById('app-physical-memory-max').textContent = formatBytes(appMetrics.maxPhysicalMemoryUsed);
    document.getElementById('app-physical-memory-min').textContent = formatBytes(appMetrics.minPhysicalMemoryUsed);

    // Virtual Memory
    document.getElementById('app-virtual-memory-avg').textContent = formatBytes(appMetrics.avgVirtualMemoryUsed);
    document.getElementById('app-virtual-memory-max').textContent = formatBytes(appMetrics.maxVirtualMemoryUsed);
    document.getElementById('app-virtual-memory-min').textContent = formatBytes(appMetrics.minVirtualMemoryUsed);

    // Update timestamp
    document.getElementById('last-updated').textContent = new Date().toLocaleTimeString();
}

// Update the application list with the latest metrics
function updateApplicationList(applications) {
    const appList = document.getElementById('app-list');

    // Store the currently selected application ID
    let selectedAppId = null;
    const activeApp = document.querySelector('.app-item.active');
    if (activeApp) {
        selectedAppId = activeApp.getAttribute('data-id');
    }

    // Clear existing list if it's not the initial server-rendered content
    if (!window.initialLoad) {
        appList.innerHTML = '';
    }

    // Add each application to the list
    applications.forEach(app => {
        // Check if the app item already exists (for initial load)
        let appItem = document.querySelector(`.app-item[data-id="${app.id}"]`);

        // If it doesn't exist, create a new one
        if (!appItem) {
            appItem = document.createElement('div');
            appItem.className = 'app-item';
            appItem.setAttribute('data-id', app.id);

            // Add click event to show application details
            appItem.addEventListener('click', function() {
                // Remove active class from all app items
                document.querySelectorAll('.app-item').forEach(item => {
                    item.classList.remove('active');
                });

                // Add active class to clicked item
                this.classList.add('active');

                // Fetch and display application details
                fetchApplicationDetails(app.id);
            });

            appList.appendChild(appItem);
        }

        // Update the app item content
        appItem.innerHTML = `
            <div class="app-name">${app.applicationName}</div>
            <div class="app-metrics">
                <span>CPU: ${formatPercentage(app.avgCpuUsage)}</span>
                <span>Memory: ${formatBytes(app.avgPhysicalMemoryUsed)}</span>
            </div>
        `;

        // Reattach click event after updating content
        appItem.addEventListener('click', function() {
            // Remove active class from all app items
            document.querySelectorAll('.app-item').forEach(item => {
                item.classList.remove('active');
            });

            // Add active class to clicked item
            this.classList.add('active');

            // Fetch and display application details
            fetchApplicationDetails(app.id);
        });

        // Restore the active state if this was the previously selected app
        if (app.id === selectedAppId) {
            appItem.classList.add('active');
        }
    });

    // If this is the initial load, mark it as complete
    window.initialLoad = false;

    // If no application is selected yet, select the first one
    if (!document.querySelector('.app-item.active') && applications.length > 0) {
        const firstApp = document.querySelector('.app-item');
        if (firstApp) {
            firstApp.classList.add('active');
            fetchApplicationDetails(applications[0].id);
        }
    }
}

// Fetch application details for a specific application
function fetchApplicationDetails(appId) {
    fetch(`/api/applications/${appId}`)
        .then(response => response.json())
        .then(data => {
            updateApplicationDetails(data);
        })
        .catch(error => {
            console.error('Error fetching application details:', error);
        });
}

// Fetch all application metrics
function fetchApplicationMetrics() {
    fetch('/api/applications')
        .then(response => response.json())
        .then(data => {
            updateApplicationList(data.applications);
        })
        .catch(error => {
            console.error('Error fetching application metrics:', error);
        });
}

// Initialize the dashboard
document.addEventListener('DOMContentLoaded', function() {
    // Mark this as the initial load
    window.initialLoad = true;

    // Initial fetch
    fetchApplicationMetrics();

    // Set up periodic updates (every second)
    setInterval(fetchApplicationMetrics, 1000);
});
