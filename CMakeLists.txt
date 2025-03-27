cmake_minimum_required(VERSION 3.20)
project(yamec_project)

# Set C++ standard to 23 for std::format support
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Set the correct path to your C++ source file
set(CPP_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/yamec/yamec-jni/src/main/cpp)

## Include gtest
#include(FetchContent)
#FetchContent_Declare(
#        googletest
#        GIT_REPOSITORY https://github.com/google/googletest.git
#        GIT_TAG v1.14.0
#)
#FetchContent_MakeAvailable(googletest)

# Find JNI package
find_package(JNI REQUIRED)

# Include JNI headers
include_directories(${JNI_INCLUDE_DIRS})

# Add the C++ source files as a library
add_library(system_metrics SHARED
        ${CPP_SOURCE_DIR}/system_metrics.cpp
        ${CPP_SOURCE_DIR}/PdhQueryManager.cpp
        ${CPP_SOURCE_DIR}/PdhQueryManager.h
        ${CPP_SOURCE_DIR}/CpuInfo.cpp
        ${CPP_SOURCE_DIR}/CpuInfo.h
        ${CPP_SOURCE_DIR}/GpuInfo.cpp
        ${CPP_SOURCE_DIR}/GpuInfo.h
        ${CPP_SOURCE_DIR}/MemoryInfo.cpp
        ${CPP_SOURCE_DIR}/MemoryInfo.h
        ${CPP_SOURCE_DIR}/SystemMonitorManager.cpp
        ${CPP_SOURCE_DIR}/SystemMonitorManager.h
)

# Link against JNI libraries and PDH library for Windows performance counters
target_link_libraries(system_metrics ${JNI_LIBRARIES} pdh)

# Create functional test executable
add_executable(test_system_metrics
        ${CPP_SOURCE_DIR}/main.cpp
        ${CPP_SOURCE_DIR}/PdhQueryManager.cpp
        ${CPP_SOURCE_DIR}/CpuInfo.cpp
        ${CPP_SOURCE_DIR}/GpuInfo.cpp
        ${CPP_SOURCE_DIR}/MemoryInfo.cpp
        ${CPP_SOURCE_DIR}/SystemMonitorManager.cpp
)
target_link_libraries(test_system_metrics pdh)

## Create unit tests executable
#add_executable(system_metrics_unit_tests
#        yamec/yamec-jni/src/test/cpp/PdhQueryManagerTest.cpp
#        yamec/yamec-jni/src/test/cpp/CpuInfoTest.cpp
#        yamec/yamec-jni/src/test/cpp/MemoryInfoTest.cpp
#        yamec/yamec-jni/src/test/cpp/GpuInfoTest.cpp
#        yamec/yamec-jni/src/test/cpp/SystemMonitorManagerTest.cpp
#        ${CPP_SOURCE_DIR}/PdhQueryManager.cpp
#        ${CPP_SOURCE_DIR}/CpuInfo.cpp
#        ${CPP_SOURCE_DIR}/GpuInfo.cpp
#        ${CPP_SOURCE_DIR}/MemoryInfo.cpp
#        ${CPP_SOURCE_DIR}/SystemMonitorManager.cpp
#)
#target_link_libraries(system_metrics_unit_tests gtest_main pdh)
#
## Enable testing support in CMake
#enable_testing()
#include(GoogleTest)
#gtest_discover_tests(system_metrics_unit_tests)