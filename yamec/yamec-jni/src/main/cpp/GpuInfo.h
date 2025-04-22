//
// Created by cmarc on 3/23/2025.
//

#ifndef GPUINFO_H
#define GPUINFO_H

#ifdef _WIN32
    #ifdef BUILDING_DLL
        #define YAMEC_API __declspec(dllexport)
    #else
        #define YAMEC_API __declspec(dllimport)
    #endif
#else
    #define YAMEC_API
#endif

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

class YAMEC_API GpuInfo
{
public:
    GpuInfo();

    ~GpuInfo();

    [[nodiscard]] bool initialize(PdhQueryManager *pdhManager);

    [[nodiscard]] int getUsage(double *usage) const;

    [[nodiscard]] static std::vector<GpuDevice> getDevices();

private:
    PDH_HCOUNTER m_usageCounter;
    PdhQueryManager *m_pdhManager;
};


#endif //GPUINFO_H
