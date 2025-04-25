#pragma once

#ifdef _WIN32
    #ifdef BUILDING_DLL
        #define YAMEC_API __declspec(dllexport)
    #else
        #define YAMEC_API __declspec(dllimport)
    #endif
#else
    #define YAMEC_API
#endif

#include <jni.h>
#include <string>
#include <memory>

class YAMEC_API Logger {
public:
    enum class Level {
        DEBUG,
        INFO,
        WARN,
        ERR
    };

    static void init(JNIEnv* env, jobject logger);
    static void log(Level level, const std::string& message);
    static void log(Level level, const std::string& message, const std::exception& e);

private:
    static JNIEnv* env;
    static jobject javaLogger;
    static jmethodID debugMethod;
    static jmethodID infoMethod;
    static jmethodID warnMethod;
    static jmethodID errorMethod;
    static jmethodID errorWithExceptionMethod;
    
    static void ensureInitialized();

    // Add the missing method declaration
    static const char* getLevelString(Level level);
};