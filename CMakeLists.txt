cmake_minimum_required(VERSION 3.20)
project(yamec_project)

# Set C++ standard to 23 for std::format support
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find JNI package
find_package(JNI REQUIRED)

# Include JNI headers
include_directories(${JNI_INCLUDE_DIRS})

# Set the correct path to your C++ source file
set(CPP_SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/yamec/yamec-jni/src/main/cpp)

# Add the C++ source files as a library
add_library(system_metrics SHARED ${CPP_SOURCE_DIR}/system_metrics.cpp)

# Link against JNI libraries and PDH library for Windows performance counters
target_link_libraries(system_metrics ${JNI_LIBRARIES} pdh)

# Create a test executable with a different name
add_executable(test_system_metrics ${CPP_SOURCE_DIR}/system_metrics.cpp)
target_link_libraries(test_system_metrics ${JNI_LIBRARIES} pdh)