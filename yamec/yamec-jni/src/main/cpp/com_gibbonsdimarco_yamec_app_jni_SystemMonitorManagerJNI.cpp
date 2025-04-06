#include "com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI.h"
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
