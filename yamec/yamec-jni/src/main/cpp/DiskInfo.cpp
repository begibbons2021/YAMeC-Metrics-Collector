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
        std::cerr << "Invalid PDH manager" << std::endl;
        return false;
    }

    if (!wmiManager)
    {
        std::cerr << "Invalid WMI manager" << std::endl;
        return false;
    }

    m_pdhManager = pdhManager;

    m_wmiManager = wmiManager;

    if (initInstances() == 0)
    {
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
            std::wcerr << "Failed to add Disk Usage counter for " << instanceNames.at(i) << std::endl;
            return false;
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
            std::wcerr << "Failed to add Disk Read Bandwidth counter for " << instanceNames.at(i) << std::endl;
            return false;
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
            std::wcerr << "Failed to add Disk Write Bandwidth counter for " << instanceNames.at(i) << std::endl;
            return false;
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
            std::wcerr << "Failed to add Disk Time to Transfer counter for " << instanceNames.at(i) << std::endl;
            return false;
        }

        m_diskTimeToTransferCounters.emplace_back(mCounter);


    }


    return true;
}

size_t DiskInfo::initInstances()
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return 0;
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
        std::cerr << "PDH manager not initialized" << std::endl;
        return -1;
    }

    // Collect data twice for accurate readings
    if (!m_pdhManager->collectData())
    {
        return -2;
    }

    Sleep(500); // Wait for 500ms

    if (!m_pdhManager->collectData())
    {
        return -2;
    }

    // Disk Utilization
    for (size_t i = 0; i < num_disks; i++)
    {
        double diskUsage;

        if (!m_pdhManager->getCounterValue(m_diskUsagePercentCounters.at(i), &diskUsage))
        {
            return -3;
        }

        diskUsageValues->push_back(diskUsage);
    }

    // Disk Bytes Read/sec
    for (size_t i = 0; i < num_disks; i++)
    {
        unsigned long long bytesRead;

        if (!m_pdhManager->getCounterValue(m_diskReadBandwidthCounters.at(i), &bytesRead))
        {
            return -4;
        }

        diskReadBandwidthValues->push_back(bytesRead);
    }

    // Disk Bytes Write/sec
    for (size_t i = 0; i < num_disks; i++)
    {
        unsigned long long bytesWritten;

        if (!m_pdhManager->getCounterValue(m_diskWriteBandwidthCounters.at(i), &bytesWritten))
        {
            return -5;
        }

        diskWriteBandwidthValues->push_back(bytesWritten);
    }

    // Avg. Disk sec/Transfer
    for (size_t i = 0; i < num_disks; i++)
    {
        double avgTimeToTransfer;

        if (!m_pdhManager->getCounterValue(m_diskTimeToTransferCounters.at(i), &avgTimeToTransfer))
        {
            return -6;
        }

        diskTimeToTransferValues->push_back(avgTimeToTransfer);
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
        std::cerr << "WMI manager not initialized" << std::endl;
        return -1;
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
