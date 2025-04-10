//
// Created by cmarc on 3/23/2025.
//

#ifndef SYSTEMMONITORMANAGER_H
#define SYSTEMMONITORMANAGER_H


// SystemMonitorManager.h
#pragma once

#include "PdhQueryManager.h"
#include "CpuInfo.h"
#include "DiskInfo.h"
#include "MemoryInfo.h"
#include "GpuInfo.h"
#include "NicInfo.h"
#include "WmiQueryManager.h"


class SystemMonitorManager
{
public:
    SystemMonitorManager();

    ~SystemMonitorManager();

    [[nodiscard]] bool initialize();

    [[nodiscard]] CpuInfo *getCpuInfo() { return &m_cpuInfo; }
    [[nodiscard]] MemoryInfo *getMemoryInfo() { return &m_memoryInfo; }
    [[nodiscard]] GpuInfo *getGpuInfo() { return &m_gpuInfo; }
    [[nodiscard]] DiskInfo *getDiskInfo() { return &m_diskInfo; }
    [[nodiscard]] NicInfo *getNicInfo() { return &m_nicInfo; }

    // Convenience methods
    [[nodiscard]] bool getCpuUsage(double *usage) const;

    [[nodiscard]] bool getGpuUsage(double *usage) const;

    [[nodiscard]] bool getMemoryCounters(unsigned long long *physicalBytesAvailable,
                                            unsigned long long *virtualBytesCommitted,
                                            double *committedPercentUsed) const;


    [[nodiscard]] size_t getDiskInstances(std::vector<std::wstring> *instanceNames) const;

    [[nodiscard]] bool getDiskCounters(std::vector<double> *diskInstancesUsage,
                                        std::vector<unsigned long long> *diskInstancesReadBandwidth,
                                        std::vector<unsigned long long> *diskInstancesWriteBandwidth,
                                        std::vector<double> *diskInstancesAvgTimeToTransfer) const;


    [[nodiscard]] size_t getNicInstances(std::vector<std::wstring> *instanceNames) const;

    [[nodiscard]] bool getNicCounters(std::vector<unsigned long long> *nicInstancesBandwidth,
                        std::vector<unsigned long long> *nicInstancesSendBytes,
                        std::vector<unsigned long long> *nicInstancesRecvBytes) const;

    [[nodiscard]] bool getPhysicalMemoryAvailable(unsigned long long *bytesAvailable) const;
    [[nodiscard]] bool getVirtualMemoryCommitted(unsigned long long *bytesCommitted) const;
    [[nodiscard]] bool getVirtualMemoryCommittedPercentUsed(double *committedPercentUsed) const;

    [[nodiscard]] unsigned long long getPhysicalMemory();

private:
    PdhQueryManager m_pdhManager;
    WmiQueryManager m_wmiManager;
    CpuInfo m_cpuInfo;
    MemoryInfo m_memoryInfo;
    DiskInfo m_diskInfo;
    GpuInfo m_gpuInfo;
    NicInfo m_nicInfo;
    bool m_initialized;
};


#endif //SYSTEMMONITORMANAGER_H
