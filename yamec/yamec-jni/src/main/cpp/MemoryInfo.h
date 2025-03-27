//
// Created by cmarc on 3/23/2025.
//

#ifndef MEMORYINFO_H
#define MEMORYINFO_H

// MemoryInfo.h
#include <windows.h>

class MemoryInfo
{
public:
    MemoryInfo() = default;

    ~MemoryInfo() = default;

    [[nodiscard]] static unsigned long long getPhysicalMemory();

    [[nodiscard]] static bool getMemoryStatus(MEMORYSTATUSEX *memStatus);
};

#endif //MEMORYINFO_H
