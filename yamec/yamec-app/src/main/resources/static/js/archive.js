function convertMetrics() {

    // Set network bit fields to appropriate capacities
    let networkBitFields = document.getElementsByClassName("nic-bits-transmitted");

    if (networkBitFields !== undefined) {
        for (let elementNum = 0; elementNum < networkBitFields.length; elementNum++) {
            let currentValue = (Number)(networkBitFields[elementNum].innerText);
            networkBitFields[elementNum].innerText = formatBitsPerSecond(currentValue)
        }
    }

    // Now, the time fields
    let timeFields
        = document.getElementsByClassName("time-field");

    if (timeFields !== undefined) {
        for (let elementNum = 0; elementNum < timeFields.length; elementNum++) {
            let currentValue = (Number)(timeFields[elementNum].innerText);
            timeFields[elementNum].innerText = formatSeconds(currentValue, 2)
        }
    }

    // Now, the memory capacities
    let memoryBytesFields
        = document.getElementsByClassName("memory-bytes");

    if (memoryBytesFields !== undefined) {
        for (let elementNum = 0; elementNum < memoryBytesFields.length; elementNum++) {
            let currentValue = (Number)(memoryBytesFields[elementNum].innerText);
            memoryBytesFields[elementNum].innerText = formatBytes(currentValue, 2)
        }
    }

    // Now, the storage capacities
    let storageBytesFields
        = document.getElementsByClassName("storage-bytes");

    if (storageBytesFields !== undefined) {
        for (let elementNum = 0; elementNum < storageBytesFields.length; elementNum++) {
            let currentValue = (Number)(storageBytesFields[elementNum].innerText);
            storageBytesFields[elementNum].innerText = formatBytes(currentValue, 2)
        }
    }

    // Now, the storage transfer rates
    let storageBytesTransferredFields
        = document.getElementsByClassName("storage-bytes-transferred");

    if (storageBytesTransferredFields !== undefined) {
        for (let elementNum = 0; elementNum < storageBytesTransferredFields.length; elementNum++) {
            let currentValue = (Number)(storageBytesTransferredFields[elementNum].innerText);
            storageBytesTransferredFields[elementNum].innerText = formatBytesTransferred(currentValue, 2)
        }
    }

    // Finally, the usage percents
    let usagePercentFields
        = document.getElementsByClassName("usage-percent");

    if (usagePercentFields !== undefined) {
        for (let elementNum = 0; elementNum < usagePercentFields.length; elementNum++) {
            let currentValue = (Number)(usagePercentFields[elementNum].innerText);
            usagePercentFields[elementNum].innerText = formatPercentage(currentValue)
        }
    }

}

document.addEventListener('DOMContentLoaded', function() {
    window.initialLoad = true;

    convertMetrics();
});