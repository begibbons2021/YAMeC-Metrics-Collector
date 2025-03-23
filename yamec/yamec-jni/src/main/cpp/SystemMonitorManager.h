//
// Created by cmarc on 3/23/2025.
//

#ifndef SYSTEMMONITORMANAGER_H
#define SYSTEMMONITORMANAGER_H


// SystemMonitorManager.h
#pragma once

#include "PdhQueryManager.h"
#include "CpuInfo.h"
#include "MemoryInfo.h"
#include "GpuInfo.h"


class SystemMonitorManager
{
public:
    SystemMonitorManager();

    ~SystemMonitorManager();

    [[nodiscard]] bool initialize();

    [[nodiscard]] CpuInfo *getCpuInfo() { return &m_cpuInfo; }
    [[nodiscard]] MemoryInfo *getMemoryInfo() { return &m_memoryInfo; }
    [[nodiscard]] GpuInfo *getGpuInfo() { return &m_gpuInfo; }

    // Convenience methods
    [[nodiscard]] bool getCpuUsage(double *usage) const;

    [[nodiscard]] bool getGpuUsage(double *usage) const;

    [[nodiscard]] unsigned long long getPhysicalMemory();

private:
    PdhQueryManager m_pdhManager;
    CpuInfo m_cpuInfo;
    MemoryInfo m_memoryInfo;
    GpuInfo m_gpuInfo;
    bool m_initialized;
};


#endif //SYSTEMMONITORMANAGER_H
