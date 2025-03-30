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



    // Add Available (physical) Bytes Counter
    //if (!m_pdhManager->addCounter(TEXT("\\Memory\\Available Bytes"), &m_physicalMemoryAvailableCounter))
    //{
    //    std::cerr << "Failed to add Memory Available Bytes counter" << std::endl;
    //    return false;
    //}
//
    //// Add Committed (Reserved Virtual Memory) Bytes Counter
    //if (!m_pdhManager->addCounter(TEXT("\\Memory\\Committed Bytes"), &m_virtualMemoryCommittedCounter))
    //{
    //    std::cerr << "Failed to add Memory Committed Bytes counter" << std::endl;
    //    return false;
    //}
//
    //// Add % Committed (virtual) Bytes In Use Counter
    //if (!m_pdhManager->addCounter(TEXT("\\Memory\\% Committed Bytes In Use"), &m_virtualMemoryCommittedPercentUsedCounter))
    //{
    //    std::cerr << "Failed to add Memory % Committed Bytes In Use counter" << std::endl;
    //    return false;
    //}


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

//
//
// bool DiskInfo::getAllCounters(unsigned long long *physicalMemoryAvailable,
//                                         unsigned long long *virtualMemoryCommitted,
//                                         double *virtualMemoryCommittedPercentUsed) const
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
//     if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedCounter, virtualMemoryCommitted))
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
//
// }
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


