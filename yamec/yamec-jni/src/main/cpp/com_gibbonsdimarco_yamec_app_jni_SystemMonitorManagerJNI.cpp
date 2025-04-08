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

JNIEXPORT jboolean JNICALL Java_com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI_release
                            (JNIEnv *env, jobject obj, const jlong monitorPtr)
{

    // In case an invalid pointer is passed by parameter, contain in a try-catch
    try
    {
        delete reinterpret_cast<SystemMonitorManager*>(monitorPtr);; // Free memory
    }
    catch (...)
    {
        return false; // Memory wasn't freed
    }

    return true; // Memory is successfully freed

}

