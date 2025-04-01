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
    [[nodiscard]] bool getPhysicalMemoryAvailable(unsigned long long *bytesAvailable) const;
    [[nodiscard]] bool getVirtualMemoryCommitted(unsigned long long *bytesCommitted) const;
    [[nodiscard]] bool getVirtualMemoryCommittedPercentUsed(double *committedPercentUsed) const;

    [[nodiscard]] unsigned long long getPhysicalMemory();

private:
    PdhQueryManager m_pdhManager;
    CpuInfo m_cpuInfo;
    MemoryInfo m_memoryInfo;
    DiskInfo m_diskInfo;
    GpuInfo m_gpuInfo;
    NicInfo m_nicInfo;
    bool m_initialized;
};


#endif //SYSTEMMONITORMANAGER_H
