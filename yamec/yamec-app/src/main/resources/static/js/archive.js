

document.addEventListener('DOMContentLoaded', function() {
    window.initialLoad = true;

    // Convert metrics to human-readable format
    convertMetrics();

    // Initialize table sorting
    initTableSorting();
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
