#include "com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI.h"
#include "SystemMonitorManager.h"
#include <iostream>
/* com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI */

/*
 * Class:     com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI
 * Method:    sayHello
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_sayHello
                        (JNIEnv *env, jobject obj) {
    std::cout << "Hello World" << std::endl;
  }

JNIEXPORT jlong JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_initialize
                            (JNIEnv *env, jobject obj)
{
    // Method suggested: https://stackoverflow.com/questions/58178431/jni-and-constructors
    // Also Marcus thought of it too!

    // Allocate memory for new SystemMonitorManager and initialize
    if (auto *monitor = new SystemMonitorManager; 0 == monitor->initialize())
    {
        return reinterpret_cast<jlong>(monitor); // Return the memory address
    }
    else
    {
        // If initialization fails, clear memory
        delete monitor;

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
    catch (const std::exception &e)
    {
        // Add log message
        return env->NewGlobalRef(nullptr);
    }

}

