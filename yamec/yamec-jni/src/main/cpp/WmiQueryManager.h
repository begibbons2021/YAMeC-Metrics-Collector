//
// Created by Brendan on 4/10/2025.
//

#ifndef WMIQUERYMANAGER_H
#define WMIQUERYMANAGER_H

#include <Wbemidl.h>
#include <comdef.h>
#include <any>
#include <map>
#include <string>
#include <vector>

#pragma comment(lib, "wbemuuid.lib")


class WmiQueryManager {

    public:

    WmiQueryManager();

    /**
     * Releases all WMI resources back to the system and ends the query connection
     * upon deconstruction
     */
    ~WmiQueryManager();

    /**
     * Attempts to set up resources to complete WMI queries and returns whether
     * it was successful or not
     * @return A boolean value of true if the WMI Query Manager was initialized
     * successfully, or false if it fails
     */
    bool initialize();

    /**
     * Queries the CimV2 WMI Service based on the SQL-style query passed by the query parameter,
     * then returns the response in the response reference variable passed by parameter
     * and a status code
     *
     * Note that all pairs
     * @param query The query string
     * @param response The reference to a pointer to hold the returned IEnumWbemClassObject
     * @return A 32-bit integer containing the WMI Query Status Code
     */
    int queryCimV2Service(const char *query, IEnumWbemClassObject *&response) const;

    /**
     * Queries the StandardCimV2 WMI Service based on the SQL-style query passed by the query parameter,
     * then returns the response in the response reference variable passed by parameter
     * and a status code
     *
     * Note that all pairs
     * @param query The query string
     * @param response The reference to a pointer to hold the returned IEnumWbemClassObject
     * @return A 32-bit integer containing the WMI Query Status Code
     */
    int queryStandardCimv2Service(const char *query, IEnumWbemClassObject *&response) const;

    /**
     * Queries the Microsoft/Windows/Services WMI Service based on the SQL-style query
     * passed by the query parameter, then returns the response in the response
     * reference variable passed by parameter and a status code
     *
     * Note that all pairs
     * @param query The query string
     * @param response The reference to a pointer to hold the returned IEnumWbemClassObject
     * @return A 32-bit integer containing the WMI Query Status Code
     */
    int queryWindowsStorageService(const char *query, IEnumWbemClassObject *&response) const;

    // /**
    //  * Queries the CimV2 WMI Service based on the SQL-style query passed by the query parameter, then returns
    //  * a std::vector containing a map for all returned objects' key-value pairs.
    //  *
    //  * Note that all pairs
    //  * @param query
    //  * @param responses
    //  * @return
    //  */
    // int queryCimV2Service(const char *query, std::vector<std::map<std::wstring, std::string>> *responses);



    private:
    /**
     * The pointer to the location in memory which the main WMI query
     * services are running
     */
    IWbemLocator *m_wbemLocator = nullptr;

    /**
     * The pointer to the Namespace with the path ROOT\\CIMV2,
     * used for Memory-related information queries and other system information
     * kept in a non-standard (or legacy) CIM v2 object
     */
    IWbemServices *m_wbemServices_CimV2 = nullptr;


    /**
     * The pointer to the Namespace with the path ROOT\\StandardCimv2,
     * used for NIC-related information queries and other system information
     * kept in a standardized CIM v2 object
     */
    IWbemServices *m_wbemServices_StandardCimv2 = nullptr;

    /**
     * The pointer to the Namespace with the path ROOT\\microsoft\\windows\\storage,
     * used for Disk-related information queries
     */
    IWbemServices *m_wbemServices_WindowsStorage = nullptr;

    /**
     * Whether the WmiQueryManager is initialized (true) or not (false)
     */
    bool m_initialized;

};



#endif //WMIQUERYMANAGER_H
