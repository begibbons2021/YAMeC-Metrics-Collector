#include "com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI.h"
#include "Logger.h"
#include "SystemMonitorManager.h"
#include <iostream>
/* com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI */

/*
 * Class:     com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI
 * Method:    initLogger
 * Signature: (Ljava/lang/Object;)V
 */
JNIEXPORT void JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_initLogger
  (JNIEnv *env, jclass clazz, jobject logger) {
    Logger::init(env, logger);
}

int convertFromWideStrToStr(std::string &dest, const std::wstring &src)
{

    // Convert wchar instance name to char
    // Suggested method: https://stackoverflow.com/a/870444
    // Determining new length
    int utf8Length = WideCharToMultiByte(CP_UTF8,
                                            0,
                                            src.c_str(),
                                            -1,
                                            nullptr,
                                            0,
                                            nullptr,
                                            nullptr);

    // Fill character buffer
    const auto utf8String = new char[utf8Length + 1];
    utf8Length = WideCharToMultiByte(CP_UTF8,
                        0,
                        src.c_str(),
                        -1,
                        utf8String,
                        utf8Length,
                        nullptr,
                        nullptr);

    // Conversion failure fail-safe: Return -1
    if (utf8Length == 0)
    {
        std::wcerr << "Conversion of " << src << " to UTF-8 failed!" << std::endl;
        // TODO: Add log message
        return -1;
    }

    dest = utf8String;

    return 0;
}

/*
 * Class:     com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI
 * Method:    sayHello
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_sayHello
  (JNIEnv *env, jobject obj) {
    Logger::log(Logger::Level::INFO, "Hello from C++ native code");
}

JNIEXPORT jlong JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_initialize
                            (JNIEnv *env, jobject obj)
{
    // Method suggested: https://stackoverflow.com/questions/58178431/jni-and-constructors
    // Also Marcus thought of it too!

    // Allocate memory for new SystemMonitorManager and initialize
    auto *monitor = new SystemMonitorManager;
    if (const int status = monitor->initialize(); 0 == status)
    {
        return reinterpret_cast<jlong>(monitor); // Return the memory address
    }
    else
    {
        // If initialization fails, clear memory
        delete monitor;

        Logger::log(Logger::Level::WARN, "System Monitor Manager could not be initialized.");
        Logger::log(Logger::Level::WARN, "Error Code: " + std::to_string(status));

        return -1; // Failed
    }
}

// Note to self: JAVA_HOME/bin/javap -s -p <class file> to see descriptors
JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getCpuMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemCpuMetric");
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>",
                                                            "(Ljava/lang/String;D)V");


    // Create buffers to hold the other information temporarily
    // Metrics Buffers (CPU Usage only has one!)
    const std::string deviceName = CpuInfo::getBrandString();
    double usageBuffer;

    // Attempt to fill buffers
    if (const int status = monitor->getCpuUsage(&usageBuffer); 0 != status)
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects
    const jobject systemMetricObject = env->NewObject(systemMetricClass,
                                                systemMetricConstructor,
                                                env->NewStringUTF(deviceName.c_str()),
                                                usageBuffer);

    return systemMetricObject;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getHardwareCpuInformation
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/CpuHardwareInformation");
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>",
                                                            "(Ljava/lang/String;JJLjava/lang/String;JJJJZ)V");


    // Create buffers to hold the other information temporarily
    // Information data buffers
    std::wstring brandString;
    unsigned int numCores;
    unsigned int numLogicalProcessors;
    std::wstring architecture;
    unsigned int numNumaNodes;
    unsigned int l1CacheSize;
    unsigned int l2CacheSize;
    unsigned int l3CacheSize;
    bool supportsVirtualization;

    // Attempt to fill buffers
    try
    {
        if (const int status = monitor->getHardwareCpuInformation(&brandString,
                                                                &numCores,
                                                                &numLogicalProcessors,
                                                                &architecture,
                                                                &numNumaNodes,
                                                                &l1CacheSize,
                                                                &l2CacheSize,
                                                                &l3CacheSize,
                                                                &supportsVirtualization);
                                                                0 != status)
        {
            Logger::log(Logger::Level::WARN, "CPU Hardware information retrieval failed.");
            Logger::log(Logger::Level::WARN, "Error Code: " + std::to_string(status));
            // Retrieval of counters failed, so return null
            return env->NewGlobalRef(nullptr);
        }
    }
    catch (std::exception &e)
    {
        Logger::log(Logger::Level::WARN, "CPU Hardware information retrieval failed due to an exception:");
        Logger::log(Logger::Level::WARN, e.what());
        return env->NewGlobalRef(nullptr);
    }
    catch (std::runtime_error &e)
    {
        Logger::log(Logger::Level::WARN, "CPU Hardware information retrieval failed due to a runtime error:");
        Logger::log(Logger::Level::WARN, e.what());
        return env->NewGlobalRef(nullptr);
    }

    std::string brandStringAsBSTR;
    if (const int success = convertFromWideStrToStr(brandStringAsBSTR, brandString);
        0 != success)
    {
        const std::string message("CPU Hardware information retrieval failed because the ",
            "brand string could not be converted to a standard width string.");

        Logger::log(Logger::Level::WARN, message);
        return env->NewGlobalRef(nullptr);
    }

    std::string architectureAsBSTR;
    if (const int success = convertFromWideStrToStr(architectureAsBSTR, architecture);
        0 != success)
    {
        const std::string message("CPU Hardware information retrieval failed because the ",
            "CPU architecture string could not be converted to a standard width string.");

        Logger::log(Logger::Level::WARN, message);
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects
    jobject systemMetricObject;

    try
    {

        systemMetricObject = env->NewObject(systemMetricClass,
                                            systemMetricConstructor,
                                            env->NewStringUTF(brandStringAsBSTR.c_str()),
                                            static_cast<jlong>(numCores),
                                            static_cast<jlong>(numLogicalProcessors),
                                            env->NewStringUTF(architectureAsBSTR.c_str()),
                                            static_cast<jlong>(numNumaNodes),
                                            static_cast<jlong>(l1CacheSize),
                                            static_cast<jlong>(l2CacheSize),
                                            static_cast<jlong>(l3CacheSize),
                                            static_cast<jboolean>(supportsVirtualization));
    }
    catch (std::exception &e)
    {
        const std::string message("CPU Hardware Retrieval failed because the CpuHardwareInformation object ",
            "containing the data could not be created due to an exception: ");
        Logger::log(Logger::Level::WARN, message);
        Logger::log(Logger::Level::WARN, e.what());
        return env->NewGlobalRef(nullptr);
    }
    catch (std::runtime_error &e)
    {
        const std::string message("CPU Hardware Retrieval failed because the CpuHardwareInformation object ",
            "containing the data could not be created due to a runtime error: ");
        Logger::log(Logger::Level::WARN, message);
        Logger::log(Logger::Level::WARN, e.what());
        return env->NewGlobalRef(nullptr);
    }

    return systemMetricObject;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getGpuMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemGpuMetric");
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(Ljava/lang/String;D)V");


    // Create buffers to hold the other information temporarily
    // Metrics Buffers (GPU Usage only has one!)
    const std::string deviceName = "_Total";
    double usageBuffer;

    // Attempt to fill buffers
    if (const int status = monitor->getGpuUsage(&usageBuffer); 0 != status)
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects
    const jobject systemMetricObject = env->NewObject(systemMetricClass,
                                                systemMetricConstructor,
                                                env->NewStringUTF(deviceName.c_str()),
                                                usageBuffer);


    return systemMetricObject;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getMemoryMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{
    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemMemoryMetric");
    // long long, long long, double, bool, bool
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(JJDZZ)V");


    // Create buffers to hold the other information temporarily
    // Metrics Buffers (CPU Usage only has one!)
    unsigned long long physicalMemoryAvailable;
    unsigned long long virtualMemoryCommitted;
    double committedVirtualMemoryUsage;
    constexpr bool isPhysicalMemoryAvailableUnsigned    = true;
    constexpr bool isVirtualMemoryCommittedUnsigned = true;

    // Attempt to fill buffers
    if (const int status = monitor->getMemoryCounters(&physicalMemoryAvailable,
                            &virtualMemoryCommitted,
                            &committedVirtualMemoryUsage); 0 != status)
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects
    const jobject systemMetricObject = env->NewObject(systemMetricClass,
                                                systemMetricConstructor,
                                                static_cast<jlong>(physicalMemoryAvailable),
                                                static_cast<jlong>(virtualMemoryCommitted),
                                                committedVirtualMemoryUsage,
                                                static_cast<jboolean>(isPhysicalMemoryAvailableUnsigned),
                                                static_cast<jboolean>(isVirtualMemoryCommittedUnsigned));

    return systemMetricObject;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getDiskMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass arrayListClass = env->FindClass("java/util/ArrayList");
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemDiskMetric");
    const jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    // Java does Generic type checks at compile time but not runtime, so we add objects of type Object
    const jmethodID arrayListAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    // Ljava/lang/string; double, long long, long long, double, bool, bool
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(Ljava/lang/String;DJJDZZ)V");

    // Create buffers to hold the disk instance names and disk instance count
    std::vector<std::wstring> diskInstanceNames;
    size_t diskInstanceCount;

    try
    {
        diskInstanceCount = monitor->getDiskInstances(&diskInstanceNames);
    }
    catch (...)

    {
        // If the monitor's pointer is incorrect or some error occurs in retrieval
        return env->NewGlobalRef(nullptr); // An error occurs when retrieving data
    }

    // No disks are being tracked with program counters (how???)
    if (diskInstanceCount == 0)
    {
        return env->NewGlobalRef(nullptr);
    }

    // Create buffers to hold the other information temporarily
    std::vector<double> diskInstancesUsage;
    std::vector<unsigned long long> diskInstancesReadBandwidth;
    std::vector<unsigned long long> diskInstancesWriteBandwidth;
    std::vector<double> diskInstancesAvgTimeToTransfer;
    constexpr bool isReadBandwidthUnsigned = true;
    constexpr bool isWriteBandwidthUnsigned = true;

    // Attempt to fill buffers
    if (const int status = monitor->getDiskCounters(&diskInstancesUsage,
                                            &diskInstancesReadBandwidth,
                                            &diskInstancesWriteBandwidth,
                                            &diskInstancesAvgTimeToTransfer);
                                            0 != status)
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects

    // Create ArrayList of size diskInstanceCount
    const jobject diskInstanceArrayList = env->NewObject(arrayListClass,
                                                    arrayListConstructor,
                                                    static_cast<jlong>(diskInstanceCount));

    // Append each SystemDiskMetric to the end of the ArrayList
    for (size_t i = 0; i < diskInstanceCount; i++)
    {
        // Convert wchar instance name to char
        // Suggested method: https://stackoverflow.com/a/870444
        // Determining new length
        int utf8Length = WideCharToMultiByte(CP_UTF8,
                                                0,
                                                diskInstanceNames[i].c_str(),
                                                -1,
                                                nullptr,
                                                0,
                                                nullptr,
                                                nullptr);

        // Conversion failure fail-safe: Just return null
        if (utf8Length == 0)
        {
            return env->NewGlobalRef(nullptr);
        }

        // Fill character buffer
        const auto utf8String = new char[utf8Length + 1];
        utf8Length = WideCharToMultiByte(CP_UTF8,
                            0,
                            diskInstanceNames[i].c_str(),
                            -1,
                            utf8String,
                            utf8Length,
                            nullptr,
                            nullptr);

        // Conversion failure fail-safe: Just return null
        if (utf8Length == 0)
        {
            return env->NewGlobalRef(nullptr);
        }


        // Allocate Java SystemDiskMetric object
        // Ljava/lang/String; double, long long, long long, double, bool, bool
        const jobject diskInstanceObject = env->NewObject(systemMetricClass,
                                                        systemMetricConstructor,
                                                        env->NewStringUTF(utf8String),
                                                        diskInstancesUsage[i],
                                                        static_cast<jlong>(diskInstancesReadBandwidth[i]),
                                                        static_cast<jlong>(diskInstancesWriteBandwidth[i]),
                                                        diskInstancesAvgTimeToTransfer[i],
                                                        static_cast<jboolean>(isReadBandwidthUnsigned),
                                                        static_cast<jboolean>(isWriteBandwidthUnsigned));

        // Try to add the object to the ArrayList
        if (const jboolean success = env->CallBooleanMethod(diskInstanceArrayList,
                                        arrayListAddMethod,
                                        diskInstanceObject); !success)
        {
            return env->NewGlobalRef(nullptr);
        }

    }

    // Return created ArrayList
    return diskInstanceArrayList;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getNicMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass arrayListClass = env->FindClass("java/util/ArrayList");
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemNicMetric");
    const jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    // Java does Generic type checks at compile time but not runtime, so we add objects of type Object
    const jmethodID arrayListAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    // Ljava/lang/string; long long, long long, long long, bool, bool, bool
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(Ljava/lang/String;JJJZZZ)V");

    // Create buffers to hold the NIC instance names and NIC instance count
    std::vector<std::wstring> nicInstanceNames;
    size_t nicInstanceCount;

    try
    {
        nicInstanceCount = monitor->getNicInstances(&nicInstanceNames);
    }
    catch (...)
    {
        // If the monitor's pointer is incorrect or some error occurs in retrieval
        return env->NewGlobalRef(nullptr); // An error occurs when retrieving data
    }

    // No disks are being tracked with program counters (how???)
    if (nicInstanceCount == 0)
    {
        return env->NewGlobalRef(nullptr);
    }

    // Create buffers to hold the other information temporarily
    std::vector<unsigned long long> nicInstancesBandwidth;
    std::vector<unsigned long long> nicInstancesBytesSent;
    std::vector<unsigned long long> nicInstancesBytesReceived;
    constexpr bool isNicBandwidthUnsigned = true;
    constexpr bool isBytesSentUnsigned = true;
    constexpr bool isBytesReceivedUnsigned = true;

    // Attempt to fill buffers
    if (const int status = monitor->getNicCounters(&nicInstancesBandwidth,
                                            &nicInstancesBytesSent,
                                            &nicInstancesBytesReceived);
                                            0 != status)
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects

    // Create ArrayList of size nicInstanceCount
    const jobject nicInstanceArrayList = env->NewObject(arrayListClass,
                                                    arrayListConstructor,
                                                    static_cast<jlong>(nicInstanceCount));

    // Append each SystemNicMetric to the end of the ArrayList
    for (size_t i = 0; i < nicInstanceCount; i++)
    {
        // Convert wchar instance name to char
        // Suggested method: https://stackoverflow.com/a/870444
        // Determining new length
        int utf8Length = WideCharToMultiByte(CP_UTF8,
                                                0,
                                                nicInstanceNames[i].c_str(),
                                                -1,
                                                nullptr,
                                                0,
                                                nullptr,
                                                nullptr);

        // Conversion failure fail-safe: Just return null
        if (utf8Length == 0)
        {
            return env->NewGlobalRef(nullptr);
        }

        // Fill character buffer
        const auto utf8String = new char[utf8Length + 1];
        utf8Length = WideCharToMultiByte(CP_UTF8,
                            0,
                            nicInstanceNames[i].c_str(),
                            -1,
                            utf8String,
                            utf8Length,
                            nullptr,
                            nullptr);

        // Conversion failure fail-safe: Just return null
        if (utf8Length == 0)
        {
            return env->NewGlobalRef(nullptr);
        }


        // Allocate Java SystemNicMetric object
        // Ljava/lang/String; long long, long long, long long, bool, bool, bool
        const jobject nicInstanceObject = env->NewObject(systemMetricClass,
                                                        systemMetricConstructor,
                                                        env->NewStringUTF(utf8String),
                                                        static_cast<jlong>(nicInstancesBandwidth[i]),
                                                        static_cast<jlong>(nicInstancesBytesSent[i]),
                                                        static_cast<jlong>(nicInstancesBytesReceived[i]),
                                                        static_cast<jboolean>(isNicBandwidthUnsigned),
                                                        static_cast<jboolean>(isBytesSentUnsigned),
                                                        static_cast<jboolean>(isBytesReceivedUnsigned));

        // Try to add the object to the ArrayList
        if (const jboolean success = env->CallBooleanMethod(nicInstanceArrayList, arrayListAddMethod, nicInstanceObject); !success)
        {
            return env->NewGlobalRef(nullptr);
        }

    }

    // Return created ArrayList
    return nicInstanceArrayList;

}


JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getProcessMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{
    // I apologize if this seems like spaghetti code because of the way the data is being managed
    // Please let me know if this could benefit from a major refactor. (std::map<wstring, any>?)

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass arrayListClass = env->FindClass("java/util/ArrayList");
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/ProcessMetric");
    const jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    // Java does Generic type checks at compile time but not runtime, so we add objects of type Object
    const jmethodID arrayListAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    // String;int;double;long long;long long
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>",
                                        "(Ljava/lang/String;IDJJ)V");

    // Create buffers to hold the disk information temporarily
    std::vector<std::wstring> processNames;
    std::vector<int> processIds;
    std::vector<double> cpuUsages;
    std::vector<long long> physicalMemoryUsedBytes;
    std::vector<long long> virtualMemoryUsedBytes;

    try
    {
        // Attempt to fill buffers
        if (const int status = monitor->getApplicationCounters(&processNames,
                                                                &processIds,
                                                                &cpuUsages,
                                                                &physicalMemoryUsedBytes,
                                                                &virtualMemoryUsedBytes);
                                                                0 != status)
        {
            std::wcerr << "Application metrics could not be retrieved. "
                        << std::endl;
            std::wcerr << "Error Code: " << std::hex << status << std::endl;
            // Retrieval of counters failed, so return null
            return env->NewGlobalRef(nullptr);
        }
    }
    catch (std::exception &e)
    {
        std::cerr << "GetProcessMetrics failed: \n Error: "
                        << e.what() << std::endl;
        return env->NewGlobalRef(nullptr);
    }
    catch (std::runtime_error &e)
    {
        std::cerr << "GetProcessMetrics failed: \n Runtime Error: "
                        << e.what() << std::endl;
        return env->NewGlobalRef(nullptr);
    }


    // Put data into Java objects


    // Create an ArrayList to return all object instances in
    jobject processMetricArrayList = env->NewObject(arrayListClass, arrayListConstructor);

    for (size_t i = 0; i < processNames.size(); ++i)
    {
        // Convert the friendlyName to UTF8
        std::string processNameAsUTF8Str;

        if (const int utf8Length = convertFromWideStrToStr(processNameAsUTF8Str, processNames.at(i));
            utf8Length < 0)
        {

            return env->NewGlobalRef(nullptr);
        }

        const int processId = processIds.at(i);
        const double cpuUsage = cpuUsages.at(i);
        const long long physicalMemory = physicalMemoryUsedBytes.at(i);
        const long long virtualMemory = virtualMemoryUsedBytes.at(i);

        // Allocate Java ProcessMetric object
        // String;int;double;long long; long long
        const _jobject *processMetricObject = env->NewObject(systemMetricClass,
                                                        systemMetricConstructor,
                                                        env->NewStringUTF(processNameAsUTF8Str.c_str()),
                                                        static_cast<jint>(processId),
                                                        static_cast<jdouble>(cpuUsage),
                                                        static_cast<jlong>(physicalMemory),
                                                        static_cast<jlong>(virtualMemory));

        // Try to add the object to the ArrayList
        if (const jboolean success = env->CallBooleanMethod(processMetricArrayList,
                                                            arrayListAddMethod,
                                                            processMetricObject); !success)
        {
            return env->NewGlobalRef(nullptr);
        }

    }

    // Return created ArrayList
    return processMetricArrayList;

}

JNIEXPORT jboolean JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_release
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    // In case an invalid pointer is passed by parameter, contain in a try-catch
    try
    {
        delete reinterpret_cast<SystemMonitorManager*>(monitorPtr); // Free memory
    }
    catch (...)
    {
        return false; // Memory wasn't freed
    }

    return true; // Memory is successfully freed

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getHardwareMemoryInformation
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/MemoryHardwareInformation");
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(JJJJZZ)V");

    try
    {
        // Create buffers to hold the other information temporarily
        // Metrics Buffers (CPU Usage only has one!)
        unsigned long long speed;
        unsigned long long capacity;
        unsigned int slotsUsed;
        unsigned int slotsTotal;
        constexpr bool speedIsUnsigned    = true;
        constexpr bool capacityIsUnsigned = true;


        // Attempt to fill buffers
        if (const int status = monitor->getHardwareMemoryInformation(&speed,
                                                        &capacity,
                                                        &slotsUsed,
                                                        &slotsTotal); 0 != status)
        {
            // Add log message
            // Retrieval of counters failed, so return null
            return env->NewGlobalRef(nullptr);
        }

        // Put data into Java objects
        const jobject systemMetricObject = env->NewObject(systemMetricClass,
                                                    systemMetricConstructor,
                                                    static_cast<jlong>(capacity),
                                                    static_cast<jlong>(speed),
                                                    static_cast<jlong>(slotsUsed),
                                                    static_cast<jlong>(slotsTotal),
                                                    static_cast<jboolean>(capacityIsUnsigned),
                                                    static_cast<jboolean>(speedIsUnsigned));

        return systemMetricObject;

    }
    catch (std::exception &e)
    {
        // Add log message
        return env->NewGlobalRef(nullptr);
    }

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getHardwareDiskInformation
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{
    // I apologize if this seems like spaghetti code because of the way the data is being managed
    // Please let me know if this could benefit from a major refactor. (std::map<wstring, any>?)

    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass arrayListClass = env->FindClass("java/util/ArrayList");
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/DiskHardwareInformation");
    const jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    // Java does Generic type checks at compile time but not runtime, so we add objects of type Object
    const jmethodID arrayListAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    // String;String;long long;long long;long long;boolean;ArrayList
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>",
                                        "(Ljava/lang/String;Ljava/lang/String;JJJZLjava/util/ArrayList;)V");

    // Create buffers to hold the disk information temporarily
    std::vector<std::wstring> diskFriendlyNames;
    std::vector<std::wstring> diskUniqueIds;
    std::vector<unsigned int> diskMediaTypes;
    std::vector<unsigned long long> diskCapacities;
    std::vector<unsigned int> diskNumbers;
    std::map<std::wstring, unsigned int> diskPartitionNameToDiskNumberMaps;

    try
    {
        // Attempt to fill buffers
        if (const int status = monitor->getHardwareDiskInformation(&diskFriendlyNames,
                                                                    &diskUniqueIds,
                                                                    &diskMediaTypes,
                                                                    &diskCapacities,
                                                                    &diskNumbers,
                                                                    &diskPartitionNameToDiskNumberMaps);
                                                0 != status)
        {
            std::wcerr << "Hardware disk information could not be retrieved. "
                        << std::endl;
            std::wcerr << "Error Code: " << std::hex << status << std::endl;
            // Retrieval of counters failed, so return null
            return env->NewGlobalRef(nullptr);
        }
    }
    catch (std::exception &e)
    {
        std::cerr << "GetHardwareDiskInformation failed: \n Error: "
                        << e.what() << std::endl;
        return env->NewGlobalRef(nullptr);
    }
    catch (std::runtime_error &e)
    {
        std::cerr << "GetHardwareDiskInformation failed: \n Runtime Error: "
                        << e.what() << std::endl;
        return env->NewGlobalRef(nullptr);
    }


    // Put data into Java objects

    // Initialize list of ArrayLists

    std::vector<jobject> diskPartitionLists;

    for (int i = 0; i < diskNumbers.size(); ++i)
    {
        jobject arrayList = env->NewObject(arrayListClass, arrayListConstructor);

        for (const std::pair<std::wstring, unsigned int> partition : diskPartitionNameToDiskNumberMaps)
        {
            // Compare disk numbers to add to appropriate ArrayLists
            if (partition.second == diskNumbers[i])
            {
                // Get partition name
                std::string partitionNameAsUTF8Str;

                if (int utf8Length = convertFromWideStrToStr(partitionNameAsUTF8Str, partition.first);
                                utf8Length < 0)
                {
                    // Error converting partition name string
                    return env->NewGlobalRef(nullptr);
                }

                // Skip this partition if there is no
                if (partitionNameAsUTF8Str.size() == 0)
                {
                    continue;
                }

                // If not successfully added to array list, we can't proceed
                if (const jboolean success =
                    env->CallBooleanMethod(arrayList, arrayListAddMethod,
                                            reinterpret_cast<jobject>
                                            (env->NewStringUTF(partitionNameAsUTF8Str.c_str())));
                                            !success)
                {
                    // TODO: Add log message
                    return env->NewGlobalRef(nullptr);
                }
            }
        }

        diskPartitionLists.emplace_back(arrayList);
    }

    // Create an ArrayList to return all object instances in
    jobject diskHardwareInformationArrayList = env->NewObject(arrayListClass, arrayListConstructor);

    for (size_t i = 0; i < diskFriendlyNames.size(); ++i)
    {
        // Convert the friendlyName to UTF8
        std::string friendlyNameAsUTF8Str;

        if (int utf8Length = convertFromWideStrToStr(friendlyNameAsUTF8Str, diskFriendlyNames.at(i));
            utf8Length < 0)
        {

            return env->NewGlobalRef(nullptr);
        }

        // Skip this drive if the friendly name is not a valid string
        if (friendlyNameAsUTF8Str.size() == 0)
        {
            continue;
        }

        // Convert the uniqueId to UTF8
        std::string uniqueIdAsUTF8Str;

        if (int utf8Length = convertFromWideStrToStr(uniqueIdAsUTF8Str, diskUniqueIds.at(i)); utf8Length < 0)
        {
            return env->NewGlobalRef(nullptr);
        }

        // Skip this drive if the unique ID is not a valid string
        if (uniqueIdAsUTF8Str.size() == 0)
        {
            continue;
        }

        unsigned int mediaType = diskMediaTypes.at(i);
        unsigned long long capacity = diskCapacities.at(i);
        unsigned int diskNumber = diskNumbers.at(i);
        constexpr bool diskCapacityIsUnsigned = true;
        jobject partitionList = diskPartitionLists.at(i);


        // Allocate Java DiskHardwareInformation object
        // String;String;long long;long long;long long;boolean;ArrayList
        const jobject diskHardwareInformationObject = env->NewObject(systemMetricClass,
                                                        systemMetricConstructor,
                                                        env->NewStringUTF(friendlyNameAsUTF8Str.c_str()),
                                                        env->NewStringUTF(uniqueIdAsUTF8Str.c_str()),
                                                        static_cast<jlong>(diskNumber),
                                                        static_cast<jlong>(mediaType),
                                                        static_cast<jlong>(capacity),
                                                        static_cast<jboolean>(diskCapacityIsUnsigned),
                                                        partitionList);

        // Try to add the object to the ArrayList

        if (const jboolean success = env->CallBooleanMethod(diskHardwareInformationArrayList,
                                        arrayListAddMethod,
                                        diskHardwareInformationObject); !success)
        {
            return env->NewGlobalRef(nullptr);
        }

    }

    // Return created ArrayList
    return diskHardwareInformationArrayList;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getHardwareNicInformation
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{
    const auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    const jclass arrayListClass = env->FindClass("java/util/ArrayList");
    const jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/NicHardwareInformation");
    const jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    // Java does Generic type checks at compile time but not runtime, so we add objects of type Object
    const jmethodID arrayListAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    // String;String;long long;long long;long long;boolean;ArrayList
    const jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>",
                                        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V");

    // Create buffers to hold the disk information temporarily
    std::vector<std::wstring> nicFriendlyNames;
    std::vector<std::wstring> nicUniqueIds;
    std::vector<std::wstring> nicLabels;
    std::vector<unsigned int> nicTypes;

    try
    {
        // Attempt to fill buffers
        if (const int status = monitor->getHardwareNicInformation(&nicFriendlyNames,
                                                                    &nicLabels,
                                                                    &nicUniqueIds,
                                                                    &nicTypes); 0 != status)
        {
            std::wcerr << "Hardware NIC information could not be retrieved. "
                        << std::endl;
            std::wcerr << "Error Code: " << std::hex << status << std::endl;
            // Retrieval of counters failed, so return null
            return env->NewGlobalRef(nullptr);
        }
    }
    catch (std::exception &e)
    {
        std::cerr << "GetHardwareNicInformation failed: \n Error: "
                        << e.what() << std::endl;
        return env->NewGlobalRef(nullptr);
    }
    catch (std::runtime_error &e)
    {
        std::cerr << "GetHardwareNicInformation failed: \n Runtime Error: "
                        << e.what() << std::endl;
        return env->NewGlobalRef(nullptr);
    }


    // Put data into Java objects


    // Create an ArrayList to return all object instances in
    jobject nicHardwareInformationArrayList = env->NewObject(arrayListClass, arrayListConstructor);

    for (size_t i = 0; i < nicFriendlyNames.size(); ++i)
    {
        // Convert the friendlyName to UTF8
        std::string friendlyNameAsUTF8Str;

        if (const int utf8Length = convertFromWideStrToStr(friendlyNameAsUTF8Str, nicFriendlyNames.at(i));
            utf8Length < 0)
        {

            return env->NewGlobalRef(nullptr);
        }

        // Skip this drive if the friendly name is not a valid string
        if (friendlyNameAsUTF8Str.size() == 0)
        {
            continue;
        }

        // Convert the uniqueId to UTF8
        std::string uniqueIdAsUTF8Str;

        if (const int utf8Length = convertFromWideStrToStr(uniqueIdAsUTF8Str, nicUniqueIds.at(i)); utf8Length < 0)
        {
            return env->NewGlobalRef(nullptr);
        }

        // Convert the uniqueId to UTF8
        std::string labelAsUTF8Str;

        if (const int utf8Length = convertFromWideStrToStr(labelAsUTF8Str, nicLabels.at(i)); utf8Length < 0)
        {
            return env->NewGlobalRef(nullptr);
        }

        // Skip this drive if the unique ID is not a valid string
        if (labelAsUTF8Str.size() == 0)
        {
            continue;
        }

        const unsigned int nicType = nicTypes.at(i);

        // Allocate Java DiskHardwareInformation object
        // String;String;String;long long
        const jobject nicHardwareInformationObject = env->NewObject(systemMetricClass,
                                                        systemMetricConstructor,
                                                        env->NewStringUTF(friendlyNameAsUTF8Str.c_str()),
                                                        env->NewStringUTF(labelAsUTF8Str.c_str()),
                                                        env->NewStringUTF(uniqueIdAsUTF8Str.c_str()),
                                                        static_cast<jlong>(nicType));

        // Try to add the object to the ArrayList
        if (const jboolean success = env->CallBooleanMethod(nicHardwareInformationArrayList,
                                                            arrayListAddMethod,
                                                            nicHardwareInformationObject); !success)
        {
            return env->NewGlobalRef(nullptr);
        }

    }

    // Return created ArrayList
    return nicHardwareInformationArrayList;

}



/*
*std::vector<std::wstring> *hardwareNames,
std::map<std::wstring, unsigned int> *uniqueIdsToDiskIdMappings,
std::vector<std::wstring> *uniqueIds, std::vector<unsigned int> *mediaTypes,
std::vector<unsigned long long> *capacities,
std::map<std::wstring, unsigned int> *partitionMappings
*/

