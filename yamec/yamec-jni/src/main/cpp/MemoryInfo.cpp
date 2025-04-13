//
// Created by cmarc on 3/23/2025.
//

// MemoryInfo.cpp
#include "MemoryInfo.h"
#include <stdexcept>
#include <iostream>

MemoryInfo::MemoryInfo() : m_pdhManager(nullptr), m_wmiManager(nullptr) {}
MemoryInfo::~MemoryInfo() = default;

bool MemoryInfo::initialize(PdhQueryManager *pdhManager,
                                WmiQueryManager *wmiManager)
{
    if (!pdhManager)
    {
        std::cerr << "Invalid PDH manager" << std::endl;
        return false;
    }

    if (!wmiManager)
    {
        std::cerr << "Invalid WMI manager" << std::endl;
        return false;
    }

    m_pdhManager = pdhManager;

    m_wmiManager = wmiManager;

    // Add Available (physical) Bytes Counter
    if (!m_pdhManager->addCounter(TEXT("\\Memory\\Available Bytes"), &m_physicalMemoryAvailableCounter))
    {
        std::cerr << "Failed to add Memory Available Bytes counter" << std::endl;
        return false;
    }

    // Add Committed (Reserved Virtual Memory) Bytes Counter
    if (!m_pdhManager->addCounter(TEXT("\\Memory\\Committed Bytes"), &m_virtualMemoryCommittedCounter))
    {
        std::cerr << "Failed to add Memory Committed Bytes counter" << std::endl;
        return false;
    }

    // Add % Committed (virtual) Bytes In Use Counter
    if (!m_pdhManager->addCounter(TEXT("\\Memory\\% Committed Bytes In Use"), &m_virtualMemoryCommittedPercentUsedCounter))
    {
        std::cerr << "Failed to add Memory % Committed Bytes In Use counter" << std::endl;
        return false;
    }

    return true;
}

bool MemoryInfo::getAllCounters(unsigned long long *physicalMemoryAvailable,
                                        unsigned long long *virtualMemoryCommitted,
                                        double *virtualMemoryCommittedPercentUsed) const
{
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

    if (!m_pdhManager->getCounterValue(m_physicalMemoryAvailableCounter, physicalMemoryAvailable))
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedCounter, virtualMemoryCommitted))
    {
        return false;
    }

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedPercentUsedCounter, virtualMemoryCommittedPercentUsed))
    {
        return false;
    }

    return true;

}

bool MemoryInfo::getPhysicalMemoryAvailable(unsigned long long *physicalMemoryAvailable) const
{
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

    if (!m_pdhManager->getCounterValue(m_physicalMemoryAvailableCounter, physicalMemoryAvailable))
    {
        return false;
    }

    return true;
}

bool MemoryInfo::getVirtualMemoryCommitted(unsigned long long *virtualMemoryCommitted) const
{
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

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedCounter, virtualMemoryCommitted))
    {
        return false;
    }

    return true;
}

bool MemoryInfo::getVirtualMemoryCommittedPercentUsed(double *virtualMemoryCommittedPercentUsed) const
{
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

    if (!m_pdhManager->getCounterValue(m_virtualMemoryCommittedPercentUsedCounter, virtualMemoryCommittedPercentUsed))
    {
        return false;
    }

    return true;
}

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

int MemoryInfo::getMemoryInformation(unsigned long long *speed,
                                           char *formfactor,
                                           unsigned long long *capacity,
                                           unsigned int *slotsUsed,
                                           unsigned int *slotsTotal) const
{
    if (!m_wmiManager)
    {
        std::cerr << "WMI manager not initialized" << std::endl;
        return -1;
    }

    IEnumWbemClassObject *response;

    HRESULT hr = m_wmiManager->queryCimV2Service("SELECT * FROM Win32_PhysicalMemory", response);

    if (FAILED(hr))
    {
        return hr;
    }

    // Output data
    IWbemClassObject *pWbemObject = nullptr; // Returned struct of system data
    ULONG ulReturn = 0; // Lines left to return
    unsigned int memorySlotsUsed = 0;
    unsigned int memorySlotsTotal = 0;
    unsigned long long memoryCapacity = 0;
    unsigned int clockSpeed = 0;

    // Each DIMM/unit of memory will have its own output
    // https://learn.microsoft.com/en-us/windows/win32/cimwin32prov/win32-physicalmemory
    while (response)
    {
        hr = response->Next(WBEM_INFINITE, 1, &pWbemObject, &ulReturn);

        if (0 == ulReturn)
        {
            break;
        }

        VARIANT capacityVar, speedVar, formfactorVar;
        VariantInit(&capacityVar);
        VariantInit(&speedVar);
        VariantInit(&formfactorVar);

        hr = pWbemObject->Get(L"Capacity", 0, &capacityVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"ConfiguredClockSpeed", 0, &speedVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"Formfactor", 0, &formfactorVar, nullptr, nullptr);

        // std::wcout << "Memory Capacity: " << capacityVar.bstrVal << " bytes" << std::endl;
        // std::wcout << "Memory Speed: " << speedVar.ullVal << "MT/s" <<  std::endl;
        // std::wcout << "Memory Formfactor: " << formfactorVar.uintVal << std::endl;

        ++memorySlotsUsed;

        // WMI uint64 doesn't become a ullVal, so it must be converted to a wstring, then
        // parsed as am unsigned long long
        auto capacityAsWString = std::wstring(capacityVar.bstrVal);
        unsigned long long capacityAsUINT64 = std::stoull(capacityAsWString);

        memoryCapacity += capacityAsUINT64;
        if (clockSpeed == 0)
        {
            clockSpeed = speedVar.ulVal;
        }

        VariantClear(&capacityVar);
        VariantClear(&speedVar);
        VariantClear(&formfactorVar);

        pWbemObject->Release();
    }

    response->Release();

    // Query Win32_PhysicalMemoryArray for total memory dimms on each channel
    hr = m_wmiManager->queryCimV2Service("SELECT * FROM Win32_PhysicalMemoryArray", response);

    if (FAILED(hr))
    {
        return hr;
    }


    // Output data
    ulReturn = 0; // Lines left to return

    // https://superuser.com/questions/331282/how-can-i-detect-the-amount-of-memory-slots-i-have
    // https://learn.microsoft.com/en-us/windows/win32/cimwin32prov/win32-physicalmemoryarray
    while (response)
    {
        hr = response->Next(WBEM_INFINITE, 1, &pWbemObject, &ulReturn);

        if (0 == ulReturn)
        {
            break;
        }

        VARIANT memoryDevicesVar;
        VariantInit(&memoryDevicesVar);


        hr = pWbemObject->Get(L"MemoryDevices", 0, &memoryDevicesVar, nullptr, nullptr);


        // std::wcout << "Slots Total: " << memoryDevicesVar.uintVal  << std::endl;
        // std::wcout << "Slots Used: " << memorySlotsUsed << std::endl;

        memorySlotsTotal += memoryDevicesVar.uintVal;

        VariantClear(&memoryDevicesVar);

        pWbemObject->Release();
    }

    response->Release();

    // Set reference variables
    if (speed != nullptr)
    {
        *speed = clockSpeed;
    }

    if (capacity != nullptr)
    {
        *capacity = memoryCapacity;
    }

    if (slotsUsed != nullptr)
    {
        *slotsUsed = memorySlotsUsed;
    }

    if (slotsTotal != nullptr)
    {
        *slotsTotal = memorySlotsTotal;
    }


    return 0;
}
