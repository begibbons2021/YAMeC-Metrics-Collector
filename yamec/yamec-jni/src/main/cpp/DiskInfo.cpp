//
// Created by cmarc on 3/23/2025.
//

// DiskInfo.cpp
#include "DiskInfo.h"
#include <stdexcept>
#include <iostream>

DiskInfo::DiskInfo() : m_pdhManager(nullptr) {}
DiskInfo::~DiskInfo() = default;

bool DiskInfo::initialize(PdhQueryManager *pdhManager, WmiQueryManager *wmiManager)
{
    if (!pdhManager)
    {
        throw std::runtime_error(" Disk Info - Was initialized with an invalid PdhQueryManager");
    }

    if (!wmiManager)
    {
        throw std::runtime_error(" Disk Info - Was initialized with an invalid WmiQueryManager");
    }


    m_pdhManager = pdhManager;

    m_wmiManager = wmiManager;

    // Initialize multi-instance performance counters

    PDH_HCOUNTER diskTimeCounterTemp;
    if (!m_pdhManager->addCounter(L"\\PhysicalDisk(*)\\% Disk Time", &diskTimeCounterTemp))
    {
        throw std::runtime_error(" Disk Info - All-device utilization counters could not be added");
    }

    m_allDisksUsageCounter = diskTimeCounterTemp;

    PDH_HCOUNTER diskReadBandwidthCounterTemp;
    if (!m_pdhManager->addCounter(L"\\PhysicalDisk(*)\\Disk Read Bytes/sec", &diskReadBandwidthCounterTemp))
    {
        throw std::runtime_error(" Disk Info - All-device read bandwidth counters could not be added");
    }

    m_allDisksReadBandwidthCounter = diskReadBandwidthCounterTemp;

    PDH_HCOUNTER diskWriteBandwidthCounterTemp;
    if (!m_pdhManager->addCounter(L"\\PhysicalDisk(*)\\Disk Write Bytes/sec", &diskWriteBandwidthCounterTemp))
    {
        throw std::runtime_error(" Disk Info - All-device write bandwidth counters could not be added");
    }

    m_allDisksWriteBandwidthCounter = diskWriteBandwidthCounterTemp;


    PDH_HCOUNTER diskTimeToTransferCounterTemp;
    if (!m_pdhManager->addCounter(L"\\PhysicalDisk(*)\\Avg. Disk sec/Transfer", &diskTimeToTransferCounterTemp))
    {
        throw std::runtime_error(" Disk Info - All-device time to transfer counters could not be added");
    }

    m_allDisksTimeToTransferCounter = diskTimeToTransferCounterTemp;


    // Initialize single-instance performance counters

    if (initInstances() == 0)
    {
        // Can't initialize with no disk devices
        return false;
    }

    std::vector<std::wstring> instanceNames;
    getInstanceNames(&instanceNames);

    // Add Disk Utilization Time Percentage Counter
    for (int i = 0; i < num_disks; ++i)
    {
        std::wstring pathName(L"\\PhysicalDisk(" + instanceNames.at(i) + L")\\% Disk Time");
        PDH_HCOUNTER mCounter;

        if (!m_pdhManager->addCounter(pathName, &mCounter))
        {
            throw std::runtime_error(" Disk Info - A device-level utilization counter could not be added");
        }

        m_diskUsagePercentCounters.emplace_back(mCounter);


    }

    // Add Disk Read Bandwidth Counter
    for (int i = 0; i < num_disks; ++i)
    {
        std::wstring pathName(L"\\PhysicalDisk(" + instanceNames.at(i) + L")\\Disk Read Bytes/sec");
        PDH_HCOUNTER mCounter;

        if (!m_pdhManager->addCounter(pathName, &mCounter))
        {
            throw std::runtime_error(" Disk Info - A device-level read bandwidth counter could not be added");
        }

        m_diskReadBandwidthCounters.emplace_back(mCounter);


    }


    // Add Disk Write Bandwidth Counter
    for (int i = 0; i < num_disks; ++i)
    {
        std::wstring pathName(L"\\PhysicalDisk(" + instanceNames.at(i) + L")\\Disk Write Bytes/sec");
        PDH_HCOUNTER mCounter;

        if (!m_pdhManager->addCounter(pathName, &mCounter))
        {
            throw std::runtime_error(" Disk Info - A device-level write bandwidth counter could not be added");
        }

        m_diskWriteBandwidthCounters.emplace_back(mCounter);


    }

    // Add Avg. Disk sec/Transfer (measures time to complete disk transfers) Counter
    for (int i = 0; i < num_disks; ++i)
    {
        std::wstring pathName(L"\\PhysicalDisk(" + instanceNames.at(i) + L")\\Avg. Disk sec/Transfer");
        PDH_HCOUNTER mCounter;

        if (!m_pdhManager->addCounter(pathName, &mCounter))
        {
            throw std::runtime_error(" Disk Info - A device-level time-to-transfer counter could not be added");
        }

        m_diskTimeToTransferCounters.emplace_back(mCounter);


    }


    return true;
}

size_t DiskInfo::initInstances()
{
    if (!m_pdhManager)
    {
        throw std::runtime_error(" Disk Info - Instances were retrieved before the PdhQueryManager was initialized");
    }

    const auto *objectName = TEXT("PhysicalDisk");
    const size_t num_instances = m_pdhManager->getInstances(objectName, disk_instance_names);

    num_disks = num_instances;

    return num_instances;

}

size_t DiskInfo::getNumDisks() const
{
    return num_disks;
}

size_t DiskInfo::getInstanceNames(std::vector<std::wstring> *list) const
{
    list->clear();

    for (size_t i = 0; i < num_disks; i++)
    {
        list->emplace_back(disk_instance_names[i]);
    }

    return num_disks;
}


int DiskInfo::getAllCounters(std::vector<double> *diskUsageValues,
                             std::vector<unsigned long long> *diskReadBandwidthValues,
                             std::vector<unsigned long long> *diskWriteBandwidthValues,
                             std::vector<double> *diskTimeToTransferValues) const
{
    if (!m_pdhManager)
    {
        throw std::runtime_error(" Disk Info - Device-level counters were retrieved before the PdhQueryManager was initialized");
    }

    // Disk Utilization
    for (size_t i = 0; i < num_disks; i++)
    {
        double diskUsage;

        try
        {
            if (!m_pdhManager->getCounterValue(m_diskUsagePercentCounters.at(i), &diskUsage))
            {
                throw std::runtime_error(
                    std::format(" Disk Info - Usage (Disk {}) - Device-level counter retrieval failed", i));
            }
        }
        catch (std::exception &e)
        {
            throw std::runtime_error(std::format(" Disk Info - Usage (Disk {}) - {}",
                i, std::string(e.what())));
        }


        diskUsageValues->push_back(diskUsage);
    }

    // Disk Bytes Read/sec
    for (size_t i = 0; i < num_disks; i++)
    {
        unsigned long long bytesRead;

        try
        {
            if (!m_pdhManager->getCounterValue(m_diskReadBandwidthCounters.at(i), &bytesRead))
            {
                throw std::runtime_error(
                    std::format(
                        " Disk Info - Read Bandwidth (Disk {}) - Device-level counter retrieval failed",
                        i));
            }
        }
        catch (std::exception &e)
        {
            throw std::runtime_error(std::format(" Disk Info - Read Bandwidth (Disk {}) - {}",
                i, std::string(e.what())));
        }


        diskReadBandwidthValues->push_back(bytesRead);
    }

    // Disk Bytes Write/sec
    for (size_t i = 0; i < num_disks; i++)
    {
        unsigned long long bytesWritten;

        try
        {
            if (!m_pdhManager->getCounterValue(m_diskWriteBandwidthCounters.at(i), &bytesWritten))
            {
                throw std::runtime_error(
                        std::format(
                            " Disk Info - Write Bandwidth (Disk {}) - Device-level counter retrieval failed",
                            i));
            }
        }
        catch (std::exception &e)
        {
            throw std::runtime_error(std::format(" Disk Info - Write Bandwidth (Disk {}) - {}",
                i, std::string(e.what())));
        }


        diskWriteBandwidthValues->push_back(bytesWritten);
    }

    // Avg. Disk sec/Transfer
    for (size_t i = 0; i < num_disks; i++)
    {
        double avgTimeToTransfer;

        try
        {
            if (!m_pdhManager->getCounterValue(m_diskTimeToTransferCounters.at(i), &avgTimeToTransfer))
            {
                throw std::runtime_error(
                        std::format(
                            " Disk Info - Time To Transfer (Disk {}) - Device-level counter retrieval failed",
                            i));
            }
        }
        catch (std::exception &e)
        {
            throw std::runtime_error(std::format(" Disk Info - Time to Transfer (Disk {}) - {}",
                i, std::string(e.what())));
        }


        diskTimeToTransferValues->push_back(avgTimeToTransfer);
    }

    return 0;

}

int DiskInfo::getAllCounters(std::vector<std::wstring> *diskInstanceNames,
                                std::vector<double> *diskUsageValues,
                                std::vector<unsigned long long> *diskReadBandwidthValues,
                                std::vector<unsigned long long> *diskWriteBandwidthValues,
                                std::vector<double> *diskTimeToTransferValues) const
{
    if (!m_pdhManager)
    {
        throw std::runtime_error(" Disk Info - All-device counters were retrieved before the PdhQueryManager was initialized");
    }

    // Collect Disk Usage
    std::unordered_map<std::wstring, double> diskUsageMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_allDisksUsageCounter, &diskUsageMap))
        {
            throw std::runtime_error(" Disk Info - Usage - All-device counter retrieval failed");
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error(
                    std::format(" Disk Info - Usage - {}", std::string(e.what())));
    }


    // Collect Bytes Read usage
    std::unordered_map<std::wstring, unsigned long long> diskReadBandwidthMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_allDisksReadBandwidthCounter, &diskReadBandwidthMap))
        {
            throw std::runtime_error(" Disk Info - Read Bandwidth - All-device counter retrieval failed");
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error(
                    std::format(" Disk Info - Read Bandwidth - {}", std::string(e.what())));
    }


    // Collect Bytes Written usage
    std::unordered_map<std::wstring, unsigned long long> diskWriteBandwidthMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_allDisksWriteBandwidthCounter, &diskWriteBandwidthMap))
        {
            throw std::runtime_error(" Disk Info - Write Bandwidth - All-device counter retrieval failed");
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error(
                    std::format(" Disk Info - Write Bandwidth - {}", std::string(e.what())));
    }


    // Collect Disk Time to Transfer
    std::unordered_map<std::wstring, double> diskTimeToTransferMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_allDisksTimeToTransferCounter, &diskTimeToTransferMap))
        {
            throw std::runtime_error(" Disk Info - Time to Transfer - All-device counter retrieval failed");
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error(
                    std::format(" Disk Info - Time to Transfer - {}", std::string(e.what())));
    }



    // Clear storage buffers
    if (diskInstanceNames != nullptr)
    {
        diskInstanceNames->clear();
    }

    if (diskUsageValues != nullptr)
    {
        diskUsageValues->clear();
    }

    if (diskReadBandwidthValues != nullptr)
    {
        diskReadBandwidthValues->clear();
    }

    if (diskWriteBandwidthValues != nullptr)
    {
        diskWriteBandwidthValues->clear();
    }

    if (diskTimeToTransferValues != nullptr)
    {
        diskTimeToTransferValues->clear();
    }

    // No counter data to retrieve
    if (diskUsageMap.empty())
    {
        return 0;
    }

    std::vector<std::wstring> diskInstancesTemp;

    for (const auto&[diskName, usage] : diskUsageMap)
    {
        // Don't return the total counters
        // if (processNameAndId == L"_Total")
        // {
        //     continue;
        // }

        // Get metrics for processes which have all metrics available
        if (diskReadBandwidthMap.contains(diskName)
            && diskWriteBandwidthMap.contains(diskName)
            && diskTimeToTransferMap.contains(diskName))
        {

            diskInstancesTemp.emplace_back(diskName);
        }
    }

    // Transfer data to the output buffers
    for (const auto &diskName : diskInstancesTemp)
    {
        double usage = diskUsageMap.at(diskName);
        unsigned long long readBandwidth = diskReadBandwidthMap.at(diskName);
        unsigned long long writeBandwidth = diskWriteBandwidthMap.at(diskName);
        double timeToTransfer = diskTimeToTransferMap.at(diskName);

        if (diskInstanceNames != nullptr)
        {
            diskInstanceNames->emplace_back(diskName);
        }

        if (diskUsageValues != nullptr)
        {
            diskUsageValues->emplace_back(usage);

        }

        if (diskReadBandwidthValues != nullptr)
        {
            diskReadBandwidthValues->emplace_back(readBandwidth);
        }

        if (diskWriteBandwidthValues != nullptr)
        {
            diskWriteBandwidthValues->emplace_back(writeBandwidth);

        }

        if (diskTimeToTransferValues != nullptr)
        {
            diskTimeToTransferValues->emplace_back(timeToTransfer);
        }

    }

    return 0;

}



int DiskInfo::getDiskInformation(std::vector<std::wstring> *hardwareNames,
                                            std::vector<std::wstring> *uniqueIds,
                                            std::vector<unsigned int> *mediaTypes,
                                            std::vector<unsigned long long> *capacities,
                                            std::vector<unsigned int> *diskNumbers,
                                            std::map<std::wstring, unsigned int> *partitionMappings) const
{
    if (!m_wmiManager)
    {
        throw std::runtime_error(" Disk Info - Hardware data were retrieved before the WmiQueryManager was initialized");
    }

    IEnumWbemClassObject *response;

    // Get general disk information from this query
    HRESULT hr = m_wmiManager->queryWindowsStorageService("SELECT * FROM MSFT_PhysicalDisk", response);

    if (FAILED(hr))
    {
        return hr;
    }

    // Output data
    IWbemClassObject *pWbemObject = nullptr; // Returned struct of system data
    ULONG ulReturn = 0; // Lines left to return
    std::vector<std::wstring> hardwareNamesTemp;
    std::vector<std::wstring> uniqueIdsTemp;
    std::vector<unsigned int> diskNumbersTemp;
    std::vector<unsigned int> mediaTypesTemp;
    std::vector<unsigned long long> capacitiesTemp;

    while (response)
    {
        hr = response->Next(WBEM_INFINITE, 1, &pWbemObject, &ulReturn);

        if (0 == ulReturn)
        {
            break;
        }

        VARIANT nameVar,
                uniqueIdVar,
                mediaTypeVar,
                deviceIdVar,
                capacityVar;

        VariantInit(&nameVar);
        VariantInit(&uniqueIdVar);
        VariantInit(&capacityVar);
        VariantInit(&deviceIdVar);
        VariantInit(&mediaTypeVar);

        hr = pWbemObject->Get(L"FriendlyName", 0, &nameVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"UniqueId", 0, &uniqueIdVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"Size", 0, &capacityVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"MediaType", 0, &mediaTypeVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"DeviceId", 0, &deviceIdVar, nullptr, nullptr);

        // Device ID should always be a drive number
        // So we should always be able to convert it to an unsigned
        // 32-bit integer
        // If not, skip adding this drive to the return object as its data is malformed
        wchar_t *charsLeft;
        unsigned int deviceIdAsUINT32 = wcstoul(deviceIdVar.bstrVal, &charsLeft, 10);
        if (!*charsLeft)
        {
            diskNumbersTemp.emplace_back(deviceIdAsUINT32);

            hardwareNamesTemp.emplace_back(nameVar.bstrVal);
            uniqueIdsTemp.emplace_back(uniqueIdVar.bstrVal);
            mediaTypesTemp.emplace_back(mediaTypeVar.ulVal);

            // WMI uint64 doesn't become a ullVal, so it must be converted to a wstring, then
            // parsed as an unsigned long long
            auto capacityAsWString = std::wstring(capacityVar.bstrVal);
            unsigned long long capacityAsUINT64 = std::stoull(capacityAsWString);

            capacitiesTemp.emplace_back(capacityAsUINT64);

        }

        VariantClear(&nameVar);
        VariantClear(&uniqueIdVar);
        VariantClear(&capacityVar);
        VariantClear(&mediaTypeVar);
        VariantClear(&deviceIdVar);

        pWbemObject->Release();
    }

    response->Release();

    // // Query MSFT_Partition for Partition Letters
    hr = m_wmiManager->queryWindowsStorageService("SELECT * FROM MSFT_Partition", response);

    if (FAILED(hr))
    {
        return hr;
    }

    // Output data
    ulReturn = 0; // Lines left to return
    std::map<std::wstring, unsigned int> diskPartitionToUniqueIdMappingsTemp;

    while (response)
    {
        hr = response->Next(WBEM_INFINITE, 1, &pWbemObject, &ulReturn);

        if (0 == ulReturn)
        {
            break;
        }

        VARIANT driveLetterVar,
                diskNumberVar;

        VariantInit(&diskNumberVar);
        VariantInit(&driveLetterVar);

        hr = pWbemObject->Get(L"DiskNumber", 0, &diskNumberVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"DriveLetter", 0, &driveLetterVar, nullptr, nullptr);

        // Map drive letter to disk number
        // This can change between boots and on hardware updates
        // So we can't rely on drive letter as the only source to associate disks and
        // partitions, which is why the database will store the unique id and
        // partition letters currently associated with it, but not the disk numbers
        //
        // It also doesn't matter if not all drives have partitions mapped to them (some won't)

        // All this work just to get this into a widestring is crazy but
        // a string was chosen with consideration of other platforms
        // which use different directories (though this code still has to
        // change for other platforms because this uses native code, so idk)

        std::string driveLetterStr(1, driveLetterVar.cVal);
        int driveLetterAsStrLen = 1;
        auto partitionNameAsWchar = new wchar_t[2];
        size_t convertedChars = 0;
        mbstowcs_s(&convertedChars, partitionNameAsWchar,
            driveLetterAsStrLen + 1, driveLetterStr.c_str(),
            driveLetterAsStrLen);
        std::wstring partitionName(partitionNameAsWchar);

        std::cout << driveLetterStr << std::endl;

        diskPartitionToUniqueIdMappingsTemp[partitionName] = diskNumberVar.ulVal;

        VariantClear(&diskNumberVar);
        VariantClear(&driveLetterVar);

        pWbemObject->Release();
    }

    response->Release();

    // Phew! At last! We're done!
    // Copy contents to pointer objects
    if (hardwareNames != nullptr)
    {
        hardwareNames->clear();
        hardwareNames->reserve(hardwareNamesTemp.size());
        for (std::wstring hardwareName : hardwareNamesTemp)
        {
            hardwareNames->emplace_back(hardwareName);
        }
    }


    if (uniqueIds != nullptr)
    {
        uniqueIds->clear();
        uniqueIds->reserve(uniqueIdsTemp.size());
        for (std::wstring uniqueId : uniqueIdsTemp)
        {
            uniqueIds->emplace_back(uniqueId);
        }
    }

    if (mediaTypes != nullptr)
    {
        mediaTypes->clear();
        mediaTypes->reserve(mediaTypesTemp.size());
        for (unsigned int mediaType : mediaTypesTemp)
        {
            mediaTypes->emplace_back(mediaType);
        }
    }

    if (capacities != nullptr)
    {
        capacities->clear();
        capacities->reserve(capacitiesTemp.size());
        for (unsigned long long capacity : capacitiesTemp)
        {
            capacities->emplace_back(capacity);
        }
    }

    if (diskNumbers != nullptr)
    {
        diskNumbers->clear();
        diskNumbers->reserve(diskNumbersTemp.size());
        for (unsigned long long diskNumber : diskNumbersTemp)
        {
            diskNumbers->emplace_back(diskNumber);
        }
    }

    if (partitionMappings != nullptr)
    {
        partitionMappings->clear();
        for (std::pair<std::wstring, unsigned int> partitionMapping
            : diskPartitionToUniqueIdMappingsTemp)
        {
            partitionMappings->emplace(partitionMapping.first, partitionMapping.second);
        }
    }

    // Success!
    return 0;
}
