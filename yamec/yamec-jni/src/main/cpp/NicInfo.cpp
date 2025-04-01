//
// Created by cmarc on 3/23/2025.
//

// NicInfo.cpp
#include "NicInfo.h"
#include <stdexcept>
#include <iostream>

NicInfo::NicInfo() : m_pdhManager(nullptr) {}
NicInfo::~NicInfo() = default;

bool NicInfo::initialize(PdhQueryManager *pdhManager)
{
    if (!pdhManager)
    {
        std::cerr << "Invalid PDH manager" << std::endl;
        return false;
    }

    m_pdhManager = pdhManager;

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

size_t NicInfo::getInstanceNames(std::vector<std::wstring> *list) const
{
    list->clear();

    for (size_t i = 0; i < num_nics; i++)
    {
        list->emplace_back(nic_instance_names[i]);
    }

    return num_nics;
}





bool NicInfo::getAllCounters(std::vector<unsigned long long> *nicBandwidthBpsValues,
                                std::vector<unsigned long long> *nicRecvBytesValues,
                                std::vector<unsigned long long> *nicSendBytesValues) const
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

    // NIC Current Bandwidth in Bps
    for (size_t i = 0; i < num_nics; i++)
    {
        unsigned long long bandwidth;

        if (!m_pdhManager->getCounterValue(m_nicBandwidthBpsCounters.at(i), &bandwidth))
        {
            return false;
        }

        nicBandwidthBpsValues->push_back(bandwidth);
    }

    // NIC Bytes Received/sec
    for (size_t i = 0; i < num_nics; i++)
    {
        unsigned long long bytesRecv;

        if (!m_pdhManager->getCounterValue(m_nicRecvBytesCounters.at(i), &bytesRecv))
        {
            return false;
        }

        nicRecvBytesValues->push_back(bytesRecv);
    }

    // NIC Bytes Sent/sec
    for (size_t i = 0; i < num_nics; i++)
    {
        unsigned long long bytesSent;

        if (!m_pdhManager->getCounterValue(m_nicSendBytesCounters.at(i), &bytesSent))
        {
            return false;
        }

        nicSendBytesValues->push_back(bytesSent);
    }

    return true;

}
