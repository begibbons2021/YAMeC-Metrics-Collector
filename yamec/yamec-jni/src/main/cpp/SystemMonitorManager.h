//
// Created by cmarc on 3/23/2025.
//

#ifndef SYSTEMMONITORMANAGER_H
#define SYSTEMMONITORMANAGER_H

#ifdef _WIN32
    #ifdef BUILDING_DLL
        #define YAMEC_API __declspec(dllexport)
    #else
        #define YAMEC_API __declspec(dllimport)
    #endif
#else
    #define YAMEC_API
#endif

// SystemMonitorManager.h
#pragma once

#include "ApplicationInfo.h"
#include "PdhQueryManager.h"
#include "CpuInfo.h"
#include "DiskInfo.h"
#include "MemoryInfo.h"
#include "GpuInfo.h"
#include "NicInfo.h"
#include "WmiQueryManager.h"


class YAMEC_API SystemMonitorManager
{
public:
    SystemMonitorManager();

    ~SystemMonitorManager();

    [[nodiscard]] int initialize();

    /**
     * Commands the PdhQueryManager to collect the current performance counter data and returns a status
     * code indicating its success or failure.<p>
     *
     * Status Codes:
     * - 0: Counters were retrieved successfully
     * - -1: The SystemMonitotManager was not initialized
     * - -2: Counter retrieval failed
     *
     * @return A status code indicating the success or failure of the operation
     */
    [[nodiscard]] int collectMetricsData() const;

    [[nodiscard]] CpuInfo *getCpuInfo() { return &m_cpuInfo; }
    [[nodiscard]] MemoryInfo *getMemoryInfo() { return &m_memoryInfo; }
    [[nodiscard]] GpuInfo *getGpuInfo() { return &m_gpuInfo; }
    [[nodiscard]] DiskInfo *getDiskInfo() { return &m_diskInfo; }
    [[nodiscard]] NicInfo *getNicInfo() { return &m_nicInfo; }
    [[nodiscard]] ApplicationInfo *getApplicationInfo() { return &m_applicationInfo; }

    // Convenience methods
    [[nodiscard]] int getCpuUsage(double *usage) const;

    [[nodiscard]] int getGpuUsage(double *usage) const;

    [[nodiscard]] int getMemoryCounters(unsigned long long *physicalBytesAvailable,
                                        unsigned long long *virtualBytesCommitted,
                                        double *committedPercentUsed) const;


    [[nodiscard]] size_t getDiskInstances(std::vector<std::wstring> *instanceNames) const;

    [[nodiscard]] int getDiskCounters(std::vector<double> *diskInstancesUsage,
                                      std::vector<unsigned long long> *diskInstancesReadBandwidth,
                                      std::vector<unsigned long long> *diskInstancesWriteBandwidth,
                                      std::vector<double> *diskInstancesAvgTimeToTransfer) const;

    [[nodiscard]] int getDiskCounters(std::vector<std::wstring> *diskInstanceNames,
                                        std::vector<double> *diskInstancesUsage,
                                        std::vector<unsigned long long> *diskInstancesReadBandwidth,
                                        std::vector<unsigned long long> *diskInstancesWriteBandwidth,
                                        std::vector<double> *diskInstancesAvgTimeToTransfer) const;

    int getAllDiskCounters(std::vector<std::wstring> *diskInstanceNames, std::vector<double> *diskInstancesUsage,
                           std::vector<unsigned long long> *diskInstancesReadBandwidth,
                           std::vector<unsigned long long> *diskInstancesWriteBandwidth,
                           std::vector<double> *diskInstancesAvgTimeToTransfer) const;


    [[nodiscard]] size_t getNicInstances(std::vector<std::wstring> *instanceNames) const;

    [[nodiscard]] int getNicCounters(std::vector<unsigned long long> *nicInstancesBandwidth,
                                     std::vector<unsigned long long> *nicInstancesSendBytes,
                                     std::vector<unsigned long long> *nicInstancesRecvBytes) const;

    [[nodiscard]] bool isInitialized() const { return m_initialized; }

    /**
    * Retrieves information pertaining to all currently executing processes/applications on
    * the system
    * @param processNames A pointer to a std::vector of wide strings to hold process names
    * @param processIds A pointer to a std::vector of integers to hold process IDs
    * @param cpuUsages A pointer to a std::vector of double precision decimals to hold
    * processes' CPU usages. The System Monitor Manager scales all usage values by the number
    * of processors available on the system, if it can be identified.
    * @param physicalMemoryUsed A pointer to a std::vector of 64-bit long integers containing the
    * amount of physical memory in use for a process, derived from its working set size
    * @param virtualMemoryUsed A pointer to a std::vector of 64-bit long integers containing the
    * amount of virtual memory in use for a process, derived from its page file size
    * @return A status code either as part of the PDH standard or 0 for success or a negative number
    *          for a failure of some kind (as an int)
    */
    [[nodiscard]] int getApplicationCounters(std::vector<std::wstring> *processNames,
                                                std::vector<int> *processIds,
                                                std::vector<double> *cpuUsages,
                                                std::vector<long long> *physicalMemoryUsed,
                                                std::vector<long long> *virtualMemoryUsed) const;

    [[nodiscard]] int getPhysicalMemoryAvailable(unsigned long long *bytesAvailable) const;
    [[nodiscard]] int getVirtualMemoryCommitted(unsigned long long *bytesCommitted) const;
    [[nodiscard]] int getVirtualMemoryCommittedPercentUsed(double *committedPercentUsed) const;

    int getHardwareCpuInformation(std::wstring *brandString, unsigned int *numCores, unsigned int *numLogicalProcessors,
                                  std::wstring *architecture, unsigned int *numNumaNodes, unsigned int *l1CacheSize,
                                  unsigned int *l2CacheSize, unsigned int *l3CacheSize, bool *supportsVirtualization) const;

    [[nodiscard]] int getHardwareMemoryInformation(unsigned long long *speed, unsigned long long *capacity,
                                                   unsigned int *slotsUsed, unsigned int *slotsTotal) const;

    [[nodiscard]] int getHardwareDiskInformation(std::vector<std::wstring> *hardwareNames,
                                   std::vector<std::wstring> *uniqueIds,
                                   std::vector<unsigned int> *mediaTypes, std::vector<unsigned long long> *capacities,
                                   std::vector<unsigned int> *diskNumbers,
                                   std::map<std::wstring, unsigned int> *partitionMappings) const;

    [[nodiscard]] int getHardwareNicInformation(std::vector<std::wstring> *hardwareNames,
                                            std::vector<std::wstring> *labels,
                                            std::vector<std::wstring> *uniqueIds,
                                            std::vector<unsigned int> *nicTypes) const;

    [[nodiscard]] unsigned long long getPhysicalMemory() const;

private:
    PdhQueryManager m_pdhManager;
    WmiQueryManager m_wmiManager;
    CpuInfo m_cpuInfo;
    MemoryInfo m_memoryInfo;
    DiskInfo m_diskInfo;
    GpuInfo m_gpuInfo;
    NicInfo m_nicInfo;
    ApplicationInfo m_applicationInfo;
    bool m_initialized;
};


#endif //SYSTEMMONITORMANAGER_H
