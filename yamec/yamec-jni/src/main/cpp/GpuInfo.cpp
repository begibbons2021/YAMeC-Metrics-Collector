//
// Created by cmarc on 3/23/2025.
//

// GpuInfo.cpp
#include "GpuInfo.h"
#include <iostream>

GpuInfo::GpuInfo() : m_pdhManager(nullptr) {}

GpuInfo::~GpuInfo() = default;

bool GpuInfo::initialize(PdhQueryManager *pdhManager)
{
    if (!pdhManager)
    {
        std::cerr << "Invalid PDH manager" << std::endl;
        return false;
    }

    m_pdhManager = pdhManager;

    // Add GPU usage counter
    // For generic GPU: "\GPU Engine(*)\Utilization Percentage"
    // For NVIDIA: "\GPU Engine(*engtype_3D)\Utilization Percentage"
    // You may need to try different counter paths depending on the GPU

    if (!m_pdhManager->addCounter(TEXT("\\GPU Engine(*)\\Utilization Percentage"), &m_usageCounter))
    {
        std::cerr << "Failed to add GPU usage counter - this may be normal if no compatible GPU is present" <<
                std::endl;
        return false;
    }

    return true;
}

bool GpuInfo::getUsage(double *usage) const
{
    // TODO: FIX THIS ALGORITHM (https://stackoverflow.com/questions/77643749/getting-gpu-usage-statistics-with-pdh)

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

    return m_pdhManager->getCounterValue(m_usageCounter, usage);
}

std::vector<GpuDevice> GpuInfo::getDevices()
{
    // This is a placeholder for future implementation
    // You would use something like DXGI or WMI to enumerate GPU devices
    return {};
}
