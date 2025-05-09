//
// Created by cmarc on 3/23/2025.
//

// NicInfo.cpp
#include "NicInfo.h"
#include <stdexcept>
#include <iostream>

NicInfo::NicInfo() : m_pdhManager(nullptr), m_wmiManager(nullptr) {}
NicInfo::~NicInfo() = default;

bool NicInfo::initialize(PdhQueryManager *pdhManager, WmiQueryManager *wmiManager)
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

    // All NICs Operating Bandwidth counter
    PDH_HCOUNTER nicBandwidthCounterTemp;
    if (!m_pdhManager->addCounter(L"\\Network Interface(*)\\Current Bandwidth", &nicBandwidthCounterTemp))
    {
        std::wcerr << "Failed to add All Disks Read Bandwidth counters " << std::endl;
        return false;
    }

    m_allNicsBandwidthBytesCounter = nicBandwidthCounterTemp;

    // All NICs Send Bytes counter
    PDH_HCOUNTER nicSendBandwidthCounterTemp;
    if (!m_pdhManager->addCounter(L"\\Network Interface(*)\\Bytes Sent/sec", &nicSendBandwidthCounterTemp))
    {
        std::wcerr << "Failed to add All Disks Write Bandwidth counters " << std::endl;
        return false;
    }

    m_allNicsSendBytesCounter = nicSendBandwidthCounterTemp;

    // All NICs Receive Bytes counter
    PDH_HCOUNTER nicReceiveBandwidthCounterTemp;
    if (!m_pdhManager->addCounter(L"\\Network Interface(*)\\Bytes Received/sec", &nicReceiveBandwidthCounterTemp))
    {
        std::wcerr << "Failed to add All Disks Usage counters " << std::endl;
        return false;
    }

    m_allNicsRecvBytesCounter = nicReceiveBandwidthCounterTemp;



    if (initInstances() == 0)
    {
        return false;
    }

    std::vector<std::wstring> instanceNames;
    getInstanceNames(&instanceNames);

    // Add NIC Current Bandwidth (bps) Counter
    for (int i = 0; i < num_nics; ++i)
    {
        std::wstring pathName(L"\\Network Interface(" + instanceNames.at(i) + L")\\Current Bandwidth");
        PDH_HCOUNTER mCounter;

        if (!m_pdhManager->addCounter(pathName, &mCounter))
        {
            std::wcerr << "Failed to add NIC Current Bandwidth counter for " << instanceNames.at(i) << std::endl;
            return false;
        }

        m_nicBandwidthBpsCounters.emplace_back(mCounter);


    }

    // Add NIC Bytes Received/sec Counter
    for (int i = 0; i < num_nics; ++i)
    {
        std::wstring pathName(L"\\Network Interface(" + instanceNames.at(i) + L")\\Bytes Received/sec");
        PDH_HCOUNTER mCounter;

        if (!m_pdhManager->addCounter(pathName, &mCounter))
        {
            std::wcerr << "Failed to add NIC Bytes Received/sec counter for " << instanceNames.at(i) << std::endl;
            return false;
        }

        m_nicRecvBytesCounters.emplace_back(mCounter);


    }


    // Add NIC Bytes Sent/sec Counter
    for (int i = 0; i < num_nics; ++i)
    {
        std::wstring pathName(L"\\Network Interface(" + instanceNames.at(i) + L")\\Bytes Sent/sec");
        PDH_HCOUNTER mCounter;

        if (!m_pdhManager->addCounter(pathName, &mCounter))
        {
            std::wcerr << "Failed to add NIC Bytes Sent/sec counter for " << instanceNames.at(i) << std::endl;
            return false;
        }

        m_nicSendBytesCounters.emplace_back(mCounter);


    }

    return true;
}

size_t NicInfo::initInstances()
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return 0;
    }

    const auto *objectName = TEXT("Network Interface");
    const size_t num_instances = m_pdhManager->getInstances(objectName, nic_instance_names);

    num_nics = num_instances;

    return num_instances;

}

size_t NicInfo::getNumNics() const
{
    return num_nics;
}

size_t NicInfo::getInstanceNames(std::vector<std::wstring> *list) const
{
    list->clear();

    for (size_t i = 0; i < num_nics; i++)
    {
        list->emplace_back(nic_instance_names[i]);
    }

    return num_nics;
}

int NicInfo::getAllCounters(std::vector<unsigned long long> *nicBandwidthBpsValues,
                            std::vector<unsigned long long> *nicSendBytesValues,
                            std::vector<unsigned long long> *nicRecvBytesValues) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return -1;
    }

    // NIC Current Bandwidth in Bps
    for (size_t i = 0; i < num_nics; i++)
    {
        unsigned long long bandwidth;

        if (!m_pdhManager->getCounterValue(m_nicBandwidthBpsCounters.at(i), &bandwidth))
        {
            return -3;
        }

        nicBandwidthBpsValues->push_back(bandwidth);
    }

    // NIC Bytes Received/sec
    for (size_t i = 0; i < num_nics; i++)
    {
        unsigned long long bytesRecv;

        if (!m_pdhManager->getCounterValue(m_nicRecvBytesCounters.at(i), &bytesRecv))
        {
            return -4;
        }

        nicRecvBytesValues->push_back(bytesRecv);
    }

    // NIC Bytes Sent/sec
    for (size_t i = 0; i < num_nics; i++)
    {
        unsigned long long bytesSent;

        if (!m_pdhManager->getCounterValue(m_nicSendBytesCounters.at(i), &bytesSent))
        {
            return -5;
        }

        nicSendBytesValues->push_back(bytesSent);
    }

    return 0;

}


int NicInfo::getAllCounters(std::vector<std::wstring> *nicInstanceNames,
                                std::vector<unsigned long long> *nicOperatingBandwidthValues,
                                std::vector<unsigned long long> *nicSendBytesValues,
                                std::vector<unsigned long long> *nicRecvBytesValues) const
{
    if (!m_pdhManager)
    {
        std::cerr << "PDH manager not initialized" << std::endl;
        return -1;
    }

    // Collect Operating Bandwidth
    std::unordered_map<std::wstring, unsigned long long> nicOperatingBandwidthMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_allNicsBandwidthBytesCounter, &nicOperatingBandwidthMap))
        {
            return -3;
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error(" NIC Info - Operating Bandwidth - " + std::string(e.what()));
    }


    // Collect Bytes Sent/sec
    std::unordered_map<std::wstring, unsigned long long> nicBytesSentMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_allNicsSendBytesCounter, &nicBytesSentMap))
        {
            return -4;
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error(" NIC Info - Bytes Sent/sec -" + std::string(e.what()));
    }


    // Collect Bytes Received/sec
    std::unordered_map<std::wstring, unsigned long long> nicBytesRecvMap;
    try
    {
        if (!m_pdhManager->getCounterValues(m_allNicsRecvBytesCounter, &nicBytesRecvMap))
        {
            return -5;
        }
    }
    catch (std::exception &e)
    {
        throw std::runtime_error(" NIC Info - Bytes Received/sec -" + std::string(e.what()));
    }


    // Clear storage buffers
    if (nicInstanceNames != nullptr)
    {
        nicInstanceNames->clear();
    }

    if (nicOperatingBandwidthValues != nullptr)
    {
        nicOperatingBandwidthValues->clear();
    }

    if (nicSendBytesValues != nullptr)
    {
        nicSendBytesValues->clear();
    }

    if (nicRecvBytesValues != nullptr)
    {
        nicRecvBytesValues->clear();
    }

    if (nicOperatingBandwidthMap.empty())
    {
        return 0;
    }

    std::vector<std::wstring> nicInstancesTemp;

    for (const auto&[nicName, operatingBandwidth] : nicOperatingBandwidthMap)
    {
        // Don't return the total counters
        // if (processNameAndId == L"_Total")
        // {
        //     continue;
        // }

        // Get metrics for processes which have all metrics available
        if (nicBytesSentMap.contains(nicName)
            && nicBytesRecvMap.contains(nicName))
        {
            nicInstancesTemp.emplace_back(nicName);
        }
    }

    // Transfer data to the output buffers
    for (const auto &nicName : nicInstancesTemp)
    {
        unsigned long long operatingBandwidth = nicOperatingBandwidthMap.at(nicName);
        unsigned long long sendBandwidth = nicBytesSentMap.at(nicName);
        unsigned long long receiveBandwidth = nicBytesRecvMap.at(nicName);

        if (nicInstanceNames != nullptr)
        {
            nicInstanceNames->emplace_back(nicName);
        }

        if (nicOperatingBandwidthValues != nullptr)
        {
            nicOperatingBandwidthValues->emplace_back(operatingBandwidth);
        }

        if (nicSendBytesValues != nullptr)
        {
            nicSendBytesValues->emplace_back(sendBandwidth);
        }

        if (nicRecvBytesValues != nullptr)
        {
            nicRecvBytesValues->emplace_back(receiveBandwidth);

        }

    }

    return 0;

}




int NicInfo::getNicInformation(std::vector<std::wstring> *hardwareNames,
                                            std::vector<std::wstring> *labels,
                                            std::vector<std::wstring> *uniqueIds,
                                            std::vector<unsigned int> *nicTypes) const
{
    if (!m_wmiManager)
    {
        std::cerr << "WMI manager not initialized" << std::endl;
        return -1;
    }

    IEnumWbemClassObject *response;

    // Get general disk information from this query
    HRESULT hr = m_wmiManager->queryStandardCimv2Service("SELECT * FROM MSFT_NetAdapter", response);

    if (FAILED(hr))
    {
        return hr;
    }

    // Output data
    IWbemClassObject *pWbemObject = nullptr; // Returned struct of system data
    ULONG ulReturn = 0; // Lines left to return
    std::vector<std::wstring> hardwareNamesTemp;
    std::vector<std::wstring> labelsTemp;
    std::vector<std::wstring> uniqueIdsTemp;
    std::vector<unsigned int> interfaceTypesTemp;

    while (response)
    {
        hr = response->Next(WBEM_INFINITE, 1, &pWbemObject, &ulReturn);

        if (0 == ulReturn)
        {
            break;
        }

        VARIANT nameVar,
                isHardwareInterfaceVar,
                uniqueIdVar,
                interfaceTypeVar,
                labelVar;

        VariantInit(&nameVar);
        VariantInit(&uniqueIdVar);
        VariantInit(&isHardwareInterfaceVar);
        VariantInit(&labelVar);
        VariantInit(&interfaceTypeVar);

        hr = pWbemObject->Get(L"InterfaceDescription", 0, &nameVar, nullptr, nullptr);
        // Technically Windows stores this as an unsigned long long, but this is stored
        // as a String for consistency with the Disk UniqueId
        hr = pWbemObject->Get(L"NetLuid", 0, &uniqueIdVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"Name", 0, &labelVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"InterfaceType", 0, &interfaceTypeVar, nullptr, nullptr);
        hr = pWbemObject->Get(L"HardwareInterface", 0, &isHardwareInterfaceVar, nullptr, nullptr);

        // Device ID should always be a drive number
        // So we should always be able to convert it to an unsigned
        // 32-bit integer
        // If not, skip adding this drive to the return object as its data is malformed

        // Only add this NIC if it's a hardware-level Network Adapter
        if (isHardwareInterfaceVar.boolVal == VARIANT_TRUE)
        {
            hardwareNamesTemp.emplace_back(nameVar.bstrVal);
            uniqueIdsTemp.emplace_back(uniqueIdVar.bstrVal);
            labelsTemp.emplace_back(labelVar.bstrVal);
            interfaceTypesTemp.emplace_back(interfaceTypeVar.ulVal);
        }

        VariantClear(&nameVar);
        VariantClear(&uniqueIdVar);
        VariantClear(&isHardwareInterfaceVar);
        VariantClear(&labelVar);
        VariantClear(&interfaceTypeVar);

        pWbemObject->Release();
    }

    response->Release();

    // Copy contents to pointer objects
    if (hardwareNames != nullptr)
    {
        hardwareNames->clear();
        hardwareNames->reserve(hardwareNamesTemp.size());
        for (std::wstring hardwareName : hardwareNamesTemp)
        {
            hardwareNames->emplace_back(hardwareName);
        }
    }


    if (uniqueIds != nullptr)
    {
        uniqueIds->clear();
        uniqueIds->reserve(uniqueIdsTemp.size());
        for (std::wstring uniqueId : uniqueIdsTemp)
        {
            uniqueIds->emplace_back(uniqueId);
        }
    }

    if (labels != nullptr)
    {
        labels->clear();
        labels->reserve(labelsTemp.size());
        for (std::wstring label : labelsTemp)
        {
            labels->emplace_back(label);
        }
    }

    if (nicTypes != nullptr)
    {
        nicTypes->clear();
        nicTypes->reserve(interfaceTypesTemp.size());
        for (unsigned long long nicType : interfaceTypesTemp)
        {
            nicTypes->emplace_back(nicType);
        }
    }


    // Success!
    return 0;
}
