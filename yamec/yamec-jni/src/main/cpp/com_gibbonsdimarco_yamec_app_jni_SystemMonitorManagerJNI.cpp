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
    if (auto *monitor = new SystemMonitorManager; monitor->initialize())
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

    auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemCpuMetric");
    jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(Ljava/lang/String;D)V");


    // Create buffers to hold the other information temporarily
    // Metrics Buffers (CPU Usage only has one!)
    const std::string deviceName = CpuInfo::getBrandString();
    double usageBuffer;

    // Attempt to fill buffers
    if (!monitor->getCpuUsage(&usageBuffer))
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects
    jobject systemMetricObject = env->NewObject(systemMetricClass,
                                                systemMetricConstructor,
                                                env->NewStringUTF(deviceName.c_str()),
                                                usageBuffer);

    return systemMetricObject;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getGpuMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    // jclass arrayListClass = env->FindClass("java/util/ArrayList");
    jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemGpuMetric");
    // jmethodID arrayListConstructor = env->GetMethodID(arrayListClass, "<init>", "()V");
    // jmethodID arrayListAddMethod = env->GetMethodID(arrayListClass, "add", "(Ljava/lang/Object;)Z");
    jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(Ljava/lang/String;D)V");


    // Create buffers to hold the other information temporarily
    // Metrics Buffers (GPU Usage only has one!)
    const std::string deviceName = "_Total";
    double usageBuffer;

    // Attempt to fill buffers
    if (!monitor->getGpuUsage(&usageBuffer))
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects
    jobject systemMetricObject = env->NewObject(systemMetricClass,
                                                systemMetricConstructor,
                                                env->NewStringUTF(deviceName.c_str()),
                                                usageBuffer);

    // // Try to get the disk instance names and count
    // std::vector<std::wstring> diskInstanceNames;
    // size_t diskInstanceCount;
    //
    // try
    // {
    //     diskInstanceCount = monitor->getDiskInstances(&diskInstanceNames);
    // }
    // catch (...)
    // {
    //     // If the monitor's pointer is incorrect or some error occurs in retrieval
    //     return env->NewGlobalRef(nullptr); // An error occurs when retrieving data
    // }
    //
    // // No disks are being tracked with program counters (how???)
    // if (diskInstanceCount == 0)
    // {
    //     return env->NewGlobalRef(nullptr);
    // }
    //
    // // Create buffers to hold the other information temporarily
    // std::vector<double> diskInstancesUsage;
    // std::vector<unsigned long long> diskInstancesReadBandwidth;
    // std::vector<unsigned long long> diskInstancesWriteBandwidth;
    // std::vector<double> diskInstancesAvgTimeToTransfer;
    //
    // // Attempt to fill buffers
    // if (!monitor->getDiskCounters(&diskInstancesUsage,
    //                                         &diskInstancesReadBandwidth,
    //                                         &diskInstancesWriteBandwidth,
    //                                         &diskInstancesAvgTimeToTransfer))
    // {
    //     // Retrieval of counters failed, so return null
    //     return env->NewGlobalRef(nullptr);
    // }
    //
    // // Put data into Java objects
    //
    // for (size_t i = 0; i < diskInstanceCount; i++)
    // {
    //
    // }

    return systemMetricObject;

}

JNIEXPORT jobject JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_getMemoryMetrics
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{
    auto *monitor = reinterpret_cast<SystemMonitorManager *>(monitorPtr); // Access the SystemMonitorManager

    // Java Classes & Methods Used
    jclass systemMetricClass = env->FindClass("com/gibbonsdimarco/yamec/app/data/SystemMemoryMetric");
    // long long, long long, double, bool, bool
    jmethodID systemMetricConstructor = env->GetMethodID(systemMetricClass, "<init>", "(JJDZZ)V");


    // Create buffers to hold the other information temporarily
    // Metrics Buffers (CPU Usage only has one!)
    unsigned long long physicalMemoryAvailable;
    unsigned long long virtualMemoryCommitted;
    double committedVirtualMemoryUsage;
    constexpr bool isPhysicalMemoryAvailableUnsigned    = true;
    constexpr bool isVirtualMemoryCommittedUnsigned = true;

    // Attempt to fill buffers
    if (!monitor->getMemoryCounters(&physicalMemoryAvailable, &virtualMemoryCommitted, &committedVirtualMemoryUsage))
    {
        // Retrieval of counters failed, so return null
        return env->NewGlobalRef(nullptr);
    }

    // Put data into Java objects
    jobject systemMetricObject = env->NewObject(systemMetricClass,
                                                systemMetricConstructor,
                                                static_cast<jlong>(physicalMemoryAvailable),
                                                static_cast<jlong>(virtualMemoryCommitted),
                                                committedVirtualMemoryUsage,
                                                static_cast<jboolean>(isPhysicalMemoryAvailableUnsigned),
                                                static_cast<jboolean>(isVirtualMemoryCommittedUnsigned));

    return systemMetricObject;

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

