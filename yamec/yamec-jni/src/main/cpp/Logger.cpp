#include "Logger.h"

#include <mutex>
#include <stdexcept>

// Initialize static members
JNIEnv* Logger::env = nullptr;
jobject Logger::javaLogger = nullptr;
jmethodID Logger::debugMethod = nullptr;
jmethodID Logger::infoMethod = nullptr;
jmethodID Logger::warnMethod = nullptr;
jmethodID Logger::errorMethod = nullptr;
jmethodID Logger::errorWithExceptionMethod = nullptr;
std::mutex loggerLock;

void Logger::init(JNIEnv* env, jobject logger) {
    // Remove the Logger:: qualifiers when inside a method
    Logger::env = env;  // This is okay for assignment outside methods
    Logger::javaLogger = env->NewGlobalRef(logger);

    jclass loggerClass = env->GetObjectClass(logger);

    debugMethod = env->GetMethodID(loggerClass, "debug", "(Ljava/lang/String;)V");
    infoMethod = env->GetMethodID(loggerClass, "info", "(Ljava/lang/String;)V");
    warnMethod = env->GetMethodID(loggerClass, "warn", "(Ljava/lang/String;)V");
    errorMethod = env->GetMethodID(loggerClass, "error", "(Ljava/lang/String;)V");
    errorWithExceptionMethod = env->GetMethodID(loggerClass, "error", "(Ljava/lang/String;Ljava/lang/Throwable;)V");

    env->DeleteLocalRef(loggerClass);
}

void Logger::log(Level level, const std::string& message) {
    ensureInitialized();

    jstring jMessage = env->NewStringUTF(message.c_str());

    switch (level) {
        case Level::DEBUG:
            env->CallVoidMethod(javaLogger, debugMethod, jMessage);
            break;
        case Level::INFO:
            env->CallVoidMethod(javaLogger, infoMethod, jMessage);
            break;
        case Level::WARN:
            env->CallVoidMethod(javaLogger, warnMethod, jMessage);
            break;
        case Level::ERROR:
            env->CallVoidMethod(javaLogger, errorMethod, jMessage);
            break;
    }

    env->DeleteLocalRef(jMessage);
}

void Logger::log(Level level, const std::string& message, const std::exception& e) {
    // Ensure logging from multiple concurrent threads doesn't conflict
    std::unique_lock<std::mutex> lock(loggerLock);
    ensureInitialized();

    jstring jMessage = env->NewStringUTF(message.c_str());

    // Create a Java exception object
    jclass exceptionClass = env->FindClass("java/lang/RuntimeException");
    jmethodID constructor = env->GetMethodID(exceptionClass, "<init>", "(Ljava/lang/String;)V");
    jstring exceptionMessage = env->NewStringUTF(e.what());
    jobject exception = env->NewObject(exceptionClass, constructor, exceptionMessage);

    env->CallVoidMethod(javaLogger, errorWithExceptionMethod, jMessage, exception);

    env->DeleteLocalRef(jMessage);
    env->DeleteLocalRef(exceptionMessage);
    env->DeleteLocalRef(exception);
    env->DeleteLocalRef(exceptionClass);

    // Release lock on resource
    lock.release();
}

void Logger::ensureInitialized() {
    if (!env || !javaLogger) {
        throw std::runtime_error("Logger not initialized. Call Logger::init first.");
    }
}

// Add proper return type and class qualifier
const char* Logger::getLevelString(Level level) {
    switch (level) {
        case Level::DEBUG: return "DEBUG";
        case Level::INFO: return "INFO";
        case Level::WARN: return "WARN";
        case Level::ERROR: return "ERROR";
        default: return "UNKNOWN";
    }
}