//
// Created by cmarc on 3/23/2025.
//

// DiskInfo.cpp
#include "DiskInfo.h"
#include <stdexcept>
#include <iostream>

DiskInfo::DiskInfo() : m_pdhManager(nullptr) {}
DiskInfo::~DiskInfo() = default;

bool DiskInfo::initialize(PdhQueryManager *pdhManager)
{
    if (!pdhManager)
    {
        std::cerr << "Invalid PDH manager" << std::endl;
        return false;
    }

    m_pdhManager = pdhManager;

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

size_t DiskInfo::getInstanceNames(std::vector<std::wstring> *list) const
{
    list->clear();

    for (size_t i = 0; i < num_disks; i++)
    {
        list->emplace_back(disk_instance_names[i]);
    }

    return num_disks;
}





bool DiskInfo::getAllCounters(std::vector<double> *diskUsageValues,
                                std::vector<unsigned long long> *diskReadBandwidthValues,
                                std::vector<unsigned long long> *diskWriteBandwidthValues,
                                std::vector<double> *diskTimeToTransferValues) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return false;
    }

    // Collect data twice for accurate readings
    if (!m_pdhManager->collectData())
    {
        return false;
    }

    Sleep(500); // Wait for 500ms

    if (!m_pdhManager->collectData())
    {
        return false;
    }

    // Disk Utilization
    for (size_t i = 0; i < num_disks; i++)
    {
        double diskUsage;

        if (!m_pdhManager->getCounterValue(m_diskUsagePercentCounters.at(i), &diskUsage))
        {
            return false;
        }

        diskUsageValues->push_back(diskUsage);
    }

    // Disk Bytes Read/sec
    for (size_t i = 0; i < num_disks; i++)
    {
        unsigned long long bytesRead;

        if (!m_pdhManager->getCounterValue(m_diskReadBandwidthCounters.at(i), &bytesRead))
        {
            return false;
        }

        diskReadBandwidthValues->push_back(bytesRead);
    }

    // Disk Bytes Write/sec
    for (size_t i = 0; i < num_disks; i++)
    {
        unsigned long long bytesWritten;

        if (!m_pdhManager->getCounterValue(m_diskWriteBandwidthCounters.at(i), &bytesWritten))
        {
            return false;
        }

        diskWriteBandwidthValues->push_back(bytesWritten);
    }

    // Avg. Disk sec/Transfer
    for (size_t i = 0; i < num_disks; i++)
    {
        double avgTimeToTransfer;

        if (!m_pdhManager->getCounterValue(m_diskTimeToTransferCounters.at(i), &avgTimeToTransfer))
        {
            return false;
        }

        diskTimeToTransferValues->push_back(avgTimeToTransfer);
    }

    return true;

}
//
// bool DiskInfo::getPhysicalMemoryAvailable(unsigned long long *physicalMemoryAvailable) const
// {
//     if (!m_pdhManager)
//     {
//         std::cerr << "PDH manager not initialized" << std::endl;
//         return false;
//     }
//
//     // Collect data twice for accurate readings
//     if (!m_pdhManager->collectData())
//     {
//         return false;
//     }
//
//     Sleep(500); // Wait for 500ms
//
//     if (!m_pdhManager->collectData())
//     {
//         return false;
//     }
//
//     if (!m_pdhManager->getCounterValue(m_physicalMemoryAvailableCounter, physicalMemoryAvailable))
//     {
//         return false;
//     }
//
//     return true;
// }
//
// bool DiskInfo::getVirtualMemoryCommitted(unsigned long long *virtualMemoryCommitted) const
// {
//     if (!m_pdhManager)
//     {
//         std::cerr << "PDH manager not initialized" << std::endl;
//         return false;
//     }
//
//     // Collect data twice for accurate readings
//     if (!m_pdhManager->collectData())
//     {
//         return false;
//     }
//
//     Sleep(500); // Wait for 500ms
//
//     if (!m_pdhManager->collectData())
//     {
//         return false;
//     }
//
//     if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedCounter, virtualMemoryCommitted))
//     {
//         return false;
//     }
//
//     return true;
// }
//
// bool DiskInfo::getVirtualMemoryCommittedPercentUsed(double *virtualMemoryCommittedPercentUsed) const
// {
//     if (!m_pdhManager)
//     {
//         std::cerr << "PDH manager not initialized" << std::endl;
//         return false;
//     }
//
//     // Collect data twice for accurate readings
//     if (!m_pdhManager->collectData())
//     {
//         return false;
//     }
//
//     Sleep(500); // Wait for 500ms
//
//     if (!m_pdhManager->collectData())
//     {
//         return false;
//     }
//
//     if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedPercentUsedCounter, virtualMemoryCommittedPercentUsed))
//     {
//         return false;
//     }
//
//     return true;
// }

// unsigned long long DiskInfo::getPhysicalMemory()
// {
//     ULONGLONG memory;
//
//     if (!GetPhysicallyInstalledSystemMemory(&memory))
//     {
//         throw std::runtime_error("Unable to get RAM information.");
//     }
//
//     return memory;
// }
//
// bool DiskInfo::getMemoryStatus(MEMORYSTATUSEX *memStatus)
// {
//     memStatus->dwLength = sizeof(MEMORYSTATUSEX);
//     return GlobalMemoryStatusEx(memStatus);
// }


