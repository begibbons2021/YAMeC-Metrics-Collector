//
// Created by cmarc on 3/23/2025.
//

// MemoryInfo.cpp
#include "MemoryInfo.h"
#include <stdexcept>

unsigned long long MemoryInfo::getPhysicalMemory()
{
    ULONGLONG memory;

    if (!GetPhysicallyInstalledSystemMemory(&memory))
    {
        throw std::runtime_error("Unable to get RAM information.");
    }

    return memory;
}

bool MemoryInfo::getMemoryStatus(MEMORYSTATUSEX *memStatus)
{
    memStatus->dwLength = sizeof(MEMORYSTATUSEX);
    return GlobalMemoryStatusEx(memStatus);
}
