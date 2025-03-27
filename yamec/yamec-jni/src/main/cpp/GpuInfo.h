//
// Created by cmarc on 3/23/2025.
//

#ifndef GPUINFO_H
#define GPUINFO_H

// GpuInfo.h
#include "PdhQueryManager.h"
#include <string>
#include <vector>


struct GpuDevice
{
    std::string name;
    size_t dedicatedMemory = 0;
    size_t sharedMemory = 0;
};

class GpuInfo
{
public:
    GpuInfo();

    ~GpuInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager);

    [[nodiscard]] bool getUsage(double *usage) const;

    [[nodiscard]] static std::vector<GpuDevice> getDevices();

private:
    PDH_HCOUNTER m_usageCounter;
    PdhQueryManager *m_pdhManager;
};


#endif //GPUINFO_H
