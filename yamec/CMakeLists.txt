cmake_minimum_required(VERSION 3.14)
project(yamec)

# Set the C++ standard
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Try to use find_package first, which is the recommended approach
find_package(JNI QUIET)

# Include GoogleTest library - https://google.github.io/googletest/quickstart-cmake.html
include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)
# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

if(JNI_FOUND)
    include_directories(${JNI_INCLUDE_DIRS})
    message(STATUS "JNI found via find_package: ${JNI_INCLUDE_DIRS}")
else()
    # Fall back to manual configuration if find_package fails
    if(DEFINED ENV{JAVA_HOME})
        set(JAVA_HOME $ENV{JAVA_HOME})
        message(STATUS "Using JAVA_HOME: ${JAVA_HOME}")
    else()
        message(FATAL_ERROR "JAVA_HOME environment variable is not set.")
    endif()

    # Add base JNI include directory
    include_directories(${JAVA_HOME}/include)

    # Add platform-specific JNI include directory
    if(WIN32)
        include_directories(${JAVA_HOME}/include/win32)
    elseif(APPLE)
        include_directories(${JAVA_HOME}/include/darwin)
    elseif(UNIX)
        include_directories(${JAVA_HOME}/include/linux)
    else()
        message(WARNING "Unknown platform, couldn't determine JNI platform-specific include directory")
    endif()
endif()

if(WIN32)
    include_directories(${JAVA_HOME}/include/win32)
    # Add source files
    add_library(yamecjni SHARED
            yamec-jni/src/main/cpp/main.cpp
            #        yamec-jni/src/main/cpp/system_metrics.cpp
            yamec-jni/src/main/cpp/CpuInfo.cpp
            yamec-jni/src/main/cpp/CpuInfo.h
            yamec-jni/src/main/cpp/GpuInfo.cpp
            yamec-jni/src/main/cpp/GpuInfo.h
            yamec-jni/src/main/cpp/MemoryInfo.cpp
            yamec-jni/src/main/cpp/MemoryInfo.h
            yamec-jni/src/main/cpp/DiskInfo.cpp
            yamec-jni/src/main/cpp/DiskInfo.h
            yamec-jni/src/main/cpp/NicInfo.cpp
            yamec-jni/src/main/cpp/NicInfo.h
            yamec-jni/src/main/cpp/PdhQueryManager.cpp
            yamec-jni/src/main/cpp/PdhQueryManager.h
            yamec-jni/src/main/cpp/Logger.cpp
            yamec-jni/src/main/cpp/Logger.h
            yamec-jni/src/main/cpp/SystemMonitorManager.cpp
            yamec-jni/src/main/cpp/SystemMonitorManager.h
            #        yamec-jni/src/main/cpp/system_metrics.cpp
            #        yamec-jni/src/main/cpp/system_metrics.cpp
            # yamec-jni/src/main/jni/SystemMetricsJNI.cpp
            yamec-jni/src/main/cpp/com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI.cpp
            yamec-jni/src/main/cpp/com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI.h
            yamec-jni/src/main/cpp/WmiQueryManager.cpp
            yamec-jni/src/main/cpp/WmiQueryManager.h
            yamec-jni/src/main/cpp/ApplicationInfo.cpp
            yamec-jni/src/main/cpp/ApplicationInfo.h

    )


else()
    message(FATAL_ERROR "The OS this is being built on is not supported! Compilation halted.")
endif()


# Link against JNI libraries if they were found
if(JNI_FOUND)
    target_link_libraries(yamecjni PRIVATE ${JNI_LIBRARIES})
    target_link_libraries(yamecjni PRIVATE pdh)
endif()

# Set output name to match what's referenced in the pom.xml
set_target_properties(yamecjni PROPERTIES
    OUTPUT_NAME "yamecjni"
    PREFIX "")

# Add install command to copy the built library
install(TARGETS yamecjni
       RUNTIME DESTINATION ${CMAKE_INSTALL_PREFIX}
       LIBRARY DESTINATION ${CMAKE_INSTALL_PREFIX}
       ARCHIVE DESTINATION ${CMAKE_INSTALL_PREFIX})