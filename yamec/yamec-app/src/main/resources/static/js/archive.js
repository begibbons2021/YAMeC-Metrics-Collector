

document.addEventListener('DOMContentLoaded', function() {
    window.initialLoad = true;

    // Convert metrics to human-readable format
    convertMetrics();

    // Initialize table sorting
    initTableSorting();

    // Initialize date/time picker
    initDateTimePicker();

    // Add event listener for the apply button
    document.getElementById('applyDateRange').addEventListener('click', applyDateTimeRange);
});

/**
 * Initialize table sorting functionality
 */
function initTableSorting() {
    const table = document.querySelector('.archive-apps-table');
    const headers = table.querySelectorAll('th');
    const tableBody = table.querySelector('tbody');
    const rows = tableBody.querySelectorAll('tr');

    // Default sort direction
    const defaultSortColumn = 2; // AVG CPU Usage column (0-based index)
    const defaultSortDirection = 'desc';

    // Store sort state
    const sortState = {
        column: defaultSortColumn,
        direction: defaultSortDirection
    };

    // Add click event listeners to headers
    headers.forEach((header, index) => {
        header.addEventListener('click', () => {
            // Toggle sort direction if clicking the same column
            if (sortState.column === index) {
                sortState.direction = sortState.direction === 'asc' ? 'desc' : 'asc';
            } else {
                sortState.column = index;
                sortState.direction = 'desc'; // Default to descending when switching columns
            }

            // Sort the table
            sortTable(tableBody, rows, index, sortState.direction);

            // Update visual indicators (optional)
            updateSortIndicators(headers, index, sortState.direction);
        });
    });

    // Initial sort by AVG CPU Usage (descending)
    sortTable(tableBody, rows, defaultSortColumn, defaultSortDirection);
    updateSortIndicators(headers, defaultSortColumn, defaultSortDirection);
}

/**
 * Sort table rows by a specific column
 */
function sortTable(tableBody, rows, columnIndex, direction) {
    const sortedRows = Array.from(rows).sort((rowA, rowB) => {
        const cellA = rowA.querySelectorAll('td, th')[columnIndex];
        const cellB = rowB.querySelectorAll('td, th')[columnIndex];

        let valueA = cellA.textContent.trim();
        let valueB = cellB.textContent.trim();

        // Handle numeric values (with % or MB suffix)
        if (valueA.includes('%') || valueB.includes('%')) {
            // Extract numeric part from percentage
            valueA = parseFloat(valueA.replace('%', ''));
            valueB = parseFloat(valueB.replace('%', ''));
        } else if (valueA.includes('MB') || valueB.includes('MB')) {
            // Extract numeric part from MB values
            valueA = parseFloat(valueA.replace('MB', ''));
            valueB = parseFloat(valueB.replace('MB', ''));
        } else if (!isNaN(parseFloat(valueA)) && !isNaN(parseFloat(valueB))) {
            valueA = parseFloat(valueA);
            valueB = parseFloat(valueB);
        }

        // Compare values
        if (valueA < valueB) {
            return direction === 'asc' ? -1 : 1;
        } else if (valueA > valueB) {
            return direction === 'asc' ? 1 : -1;
        }
        return 0;
    });

    // Remove existing rows
    rows.forEach(row => {
        tableBody.removeChild(row);
    });

    // Append sorted rows
    sortedRows.forEach(row => {
        tableBody.appendChild(row);
    });
}

/**
 * Update sort indicators in table headers
 */
function updateSortIndicators(headers, activeIndex, direction) {
    headers.forEach((header, index) => {
        // Remove existing indicators
        header.classList.remove('sort-asc', 'sort-desc');

        // Add indicator to active column
        if (index === activeIndex) {
            header.classList.add(direction === 'asc' ? 'sort-asc' : 'sort-desc');
        }
    });
}

/**
 * Initialize date/time picker with values from URL parameters or defaults
 */
function initDateTimePicker() {
    // Get URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const startTimeParam = urlParams.get('startTime');
    const endTimeParam = urlParams.get('endTime');

    // Solution for timezone issues: https://stackoverflow.com/a/61082536

    let startTime, endTime;

    if (startTimeParam && endTimeParam) {
        // Use parameters from URL
        startTime = new Date(parseInt(startTimeParam))
        startTime.setMinutes(startTime.getMinutes() - startTime.getTimezoneOffset());
        startTime = startTime.toISOString().slice(0, 16);
        endTime = new Date(parseInt(endTimeParam));
        endTime.setMinutes(endTime.getMinutes() - endTime.getTimezoneOffset());
        endTime = endTime.toISOString().slice(0, 16);
    } else {
        // Set default end time to now
        let now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        endTime = now.toISOString().slice(0, 16); // Format: YYYY-MM-DDTHH:MM

        // Set default start time to 5 minutes ago
        let fiveMinutesAgo = new Date(now.getTime() - 5 * 60 * 1000);
        // fiveMinutesAgo.setMinutes(fiveMinutesAgo.getMinutes() - fiveMinutesAgo.getTimezoneOffset());
        startTime = fiveMinutesAgo.toISOString().slice(0, 16);
    }

    // Set values
    document.getElementById('startTime').value = startTime;
    document.getElementById('endTime').value = endTime;
}

/**
 * Apply the selected date/time range and reload metrics
 */
function applyDateTimeRange() {
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;

    if (!startTime || !endTime) {
        alert('Please select both start and end times');
        return;
    }

    // Convert to timestamps (milliseconds since epoch)
    const startTimestamp = new Date(startTime).getTime();
    const endTimestamp = new Date(endTime).getTime();

    if (startTimestamp >= endTimestamp) {
        alert('Start time must be before end time');
        return;
    }

    // Redirect to the same page with query parameters
    window.location.href = `/archive?startTime=${startTimestamp}&endTime=${endTimestamp}`;
}
