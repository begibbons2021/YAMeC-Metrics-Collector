//
// Created by Brendan on 4/10/2025.
//

#include "WmiQueryManager.h"

#include <iostream>

WmiQueryManager::WmiQueryManager() : m_initialized(false)
{

}

WmiQueryManager::~WmiQueryManager()
{
    if (m_initialized)
    {
        // Release all active service connections (clearing the memory)
        if (m_wbemServices_WindowsStorage != nullptr)
        {
            m_wbemServices_WindowsStorage->Release();
        }

        if (m_wbemServices_StandardCimv2 != nullptr)
        {
            m_wbemServices_StandardCimv2->Release();
        }

        if (m_wbemServices_CimV2 != nullptr)
        {
            m_wbemServices_CimV2->Release();
        }

        if (m_wbemLocator != nullptr)
        {
            m_wbemLocator->Release();
        }

        // End the WMI query interface
        CoUninitialize();
    }
}

bool WmiQueryManager::initialize()
{
    // Don't reinitialize the WMI query managers
    if (m_initialized)
    {
        return true;
    }

    HRESULT hr = CoInitializeEx(nullptr, COINIT_MULTITHREADED);
    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << "The COM library could not be initialized. " << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        return false;
    }

    // Set general COM security levels
    hr = CoInitializeSecurity(nullptr,
                                -1,
                                nullptr,
                                nullptr,
                                RPC_C_AUTHN_LEVEL_DEFAULT,
                                RPC_C_IMP_LEVEL_IMPERSONATE,
                                nullptr,
                                EOAC_NONE,
                                nullptr);

    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << "The COM library's security levels couldn't be set. " << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        CoUninitialize();
        return false;
    }

    // Get the initial WMI locator
    IWbemLocator *tempWbemLocator = nullptr;
    hr = CoCreateInstance(CLSID_WbemLocator,
                        nullptr,
                        CLSCTX_INPROC_SERVER,
                        IID_IWbemLocator,
                        reinterpret_cast<LPVOID *>(&tempWbemLocator));

    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << "The IWbemLocator couldn't be initialized. " << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        CoUninitialize();
        return false;
    }

    // Connect to WMI using the current user's credentials to call to system
    // information databases

    IWbemServices *tempWbemCimV2Service = nullptr;
    IWbemServices *tempWbemStandardCimV2Service = nullptr;
    IWbemServices *tempWbemWindowsStorageService = nullptr;

    // ROOT\CIMV2
    hr = tempWbemLocator->ConnectServer(const_cast<BSTR>(L"ROOT\\CIMV2"),
                                        nullptr,
                                        nullptr,
                                        nullptr,
                                        0,
                                        nullptr,
                                        nullptr,
                                        &tempWbemCimV2Service);
    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << R"(The IWbemService "ROOT\CIMV2" couldn't be initialized. )" << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        tempWbemLocator->Release();
        CoUninitialize();
        return false;
    }

    // ROOT\StandardCimv2
    hr = tempWbemLocator->ConnectServer(const_cast<BSTR>(L"ROOT\\StandardCimv2"),
                                        nullptr,
                                        nullptr,
                                        nullptr,
                                        0,
                                        nullptr,
                                        nullptr,
                                        &tempWbemStandardCimV2Service);
    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << R"(The IWbemService "ROOT\StandardCimv2" couldn't be initialized. )" << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        tempWbemLocator->Release();
        tempWbemCimV2Service->Release();
        CoUninitialize();
        return false;
    }

    // ROOT\microsoft\windows\storage
    hr = tempWbemLocator->ConnectServer(const_cast<BSTR>(L"ROOT\\microsoft\\windows\\storage"),
                                        nullptr,
                                        nullptr,
                                        nullptr,
                                        0,
                                        nullptr,
                                        nullptr,
                                        &tempWbemWindowsStorageService);
    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << R"(The IWbemService "ROOT\microsoft\windows\storage" couldn't be initialized. )" << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        tempWbemLocator->Release();
        tempWbemCimV2Service->Release();
        tempWbemStandardCimV2Service->Release();
        CoUninitialize();
        return false;
    }

    // Set up security levels on pWbemServices Proxies

    // Nonstandard CIM V2 Namespace Service
    hr = CoSetProxyBlanket(tempWbemCimV2Service,
                            RPC_C_AUTHN_WINNT,
                            RPC_C_AUTHZ_NONE,
                            NULL,
                            RPC_C_AUTHN_LEVEL_CALL,
                            RPC_C_IMP_LEVEL_IMPERSONATE,
                            NULL,
                            EOAC_NONE);

    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << "The IWbemService " << R"(ROOT\CIMV2)"
                        << " service's proxy couldn't be set up. )" << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        tempWbemLocator->Release();
        tempWbemCimV2Service->Release();
        tempWbemStandardCimV2Service->Release();
        tempWbemWindowsStorageService->Release();
        CoUninitialize();
        return false;
    }

    // Standard CIM V2 Namespace Service
    hr = CoSetProxyBlanket(tempWbemStandardCimV2Service,
                            RPC_C_AUTHN_WINNT,
                            RPC_C_AUTHZ_NONE,
                            NULL,
                            RPC_C_AUTHN_LEVEL_CALL,
                            RPC_C_IMP_LEVEL_IMPERSONATE,
                            NULL,
                            EOAC_NONE);

    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << "The IWbemService " << R"(ROOT\StandardCimv2)"
                        << " service's proxy couldn't be set up. )" << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        tempWbemLocator->Release();
        tempWbemCimV2Service->Release();
        tempWbemStandardCimV2Service->Release();
        tempWbemWindowsStorageService->Release();
        CoUninitialize();
        return false;
    }

    // Windows Storage Namespace Service
    hr = CoSetProxyBlanket(tempWbemWindowsStorageService,
                            RPC_C_AUTHN_WINNT,
                            RPC_C_AUTHZ_NONE,
                            NULL,
                            RPC_C_AUTHN_LEVEL_CALL,
                            RPC_C_IMP_LEVEL_IMPERSONATE,
                            NULL,
                            EOAC_NONE);

    if (FAILED(hr))
    {
        std::cerr << "Could not initialize the WMI Query Manager. " << std::endl;
        std::cerr << "The IWbemService " << R"(ROOT\microsoft\windows\storage)"
                        << " service's proxy couldn't be set up. )" << std::endl;
        std::cerr << "Error Code: " << std::hex << hr << std::endl;
        tempWbemLocator->Release();
        tempWbemCimV2Service->Release();
        tempWbemStandardCimV2Service->Release();
        tempWbemWindowsStorageService->Release();
        CoUninitialize();
        return false;
    }

    // Set pointer fields in object
    m_wbemLocator = tempWbemLocator;
    m_wbemServices_CimV2 = tempWbemCimV2Service;
    m_wbemServices_StandardCimv2 = tempWbemStandardCimV2Service;
    m_wbemServices_WindowsStorage = tempWbemWindowsStorageService;

    return true;

}
