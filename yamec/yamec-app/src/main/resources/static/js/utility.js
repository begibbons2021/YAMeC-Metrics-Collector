// YAMeC Utility Java Functions

/**
 * Format bytes to human-readable format
 *
 * @param bytes The number of bytes to format
 * @param decimals The number of decimal places to format to
 * @returns {string} A string containing the number of bytes converted to an appropriately large unit
 */
function formatBytes(bytes, decimals = 2) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

/**
 * Format seconds to human-readable format
 *
 * @param seconds The number of seconds to format
 * @param decimals The number of decimal places to format to
 * @returns {string} A string containing the number of bytes converted to an appropriately large unit
 */
function formatSeconds(seconds, decimals = 2) {

    if (seconds === 0) return '0 ms';

    const dm = decimals < 0 ? 0 : decimals;

    if (seconds >= 1) {
        return parseFloat(seconds).toFixed(dm) + "sec";
    } else {
        return (parseFloat(seconds)*1000.0).toFixed(dm) + "ms";
    }

}

/**
 * Format bytes to human-readable format
 *
 * @param bytes The number of bytes to format
 * @param decimals The number of decimal places to format to
 * @returns {string} A string containing the number of bytes converted to an appropriately large unit
 */
function formatBytesTransferred(bytes, decimals = 2) {
    if (bytes === 0) return '0 Bytes/sec';

    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes/sec',
                            'KB/sec',
                            'MB/sec',
                            'GB/sec',
                            'TB/sec',
                            'PB/sec',
                            'EB/sec',
                            'ZB/sec',
                            'YB/sec'];

    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

/**
 * Format bits per second to human-readable format
 *
 * @param bps The number of bits to format
 * @param decimals The number of decimal places to format to
 * @returns {string} A string containing the number of bits per second converted
 *                      to an appropriately large unit
 */
function formatBitsPerSecond(bps, decimals = 2) {
    if (bps === 0) return '0 bps';

    const k = 1000;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['bps', 'Kbps', 'Mbps', 'Gbps', 'Tbps', 'Pbps', 'Ebps', 'Zbps', 'Ybps'];

    const i = Math.floor(Math.log(bps) / Math.log(k));

    return parseFloat((bps / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

/**
 * Format percentage
 *
 * @param value The percentage to format
 */
function formatPercentage(value) {
    return value.toFixed(1) + '%';
}

/**
 * Get progress bar color based on percentage
 *
 * @param percentage The percentage of the progress bar
 * @returns {string} The variable color string for the progress bar
 */
function getProgressBarColor(percentage) {
    if (percentage < 35) {
        return 'var(--secondary-color)'; // Green
    } else if (percentage < 60) {
        return 'var(--warning-color)';   // Yellow
    } else if (percentage < 80) {
        return 'var(--orange-color)';    // Orange
    } else {
        return 'var(--danger-color)';    // Red
    }
}

