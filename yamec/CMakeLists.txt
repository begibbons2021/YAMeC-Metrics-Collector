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

# Enable testing
enable_testing()

# JNI configuration
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

# Create include directory structure for clean includes
set(YAMEC_INCLUDE_DIR ${CMAKE_CURRENT_BINARY_DIR}/include)
file(MAKE_DIRECTORY ${YAMEC_INCLUDE_DIR}/yamecjni)

# Get all header files
file(GLOB HEADER_FILES
    "yamec-jni/src/main/cpp/*.h"
)

# Copy headers to the include directory
foreach(HEADER ${HEADER_FILES})
    get_filename_component(HEADER_NAME ${HEADER} NAME)
    configure_file(${HEADER} ${YAMEC_INCLUDE_DIR}/yamecjni/${HEADER_NAME} COPYONLY)
endforeach()

if(WIN32)
    # Add source files for the library
    add_library(yamecjni SHARED
        yamec-jni/src/main/cpp/main.cpp
        yamec-jni/src/main/cpp/CpuInfo.cpp
        yamec-jni/src/main/cpp/GpuInfo.cpp
        yamec-jni/src/main/cpp/MemoryInfo.cpp
        yamec-jni/src/main/cpp/DiskInfo.cpp
        yamec-jni/src/main/cpp/NicInfo.cpp
        yamec-jni/src/main/cpp/PdhQueryManager.cpp
        yamec-jni/src/main/cpp/Logger.cpp
        yamec-jni/src/main/cpp/SystemMonitorManager.cpp
        yamec-jni/src/main/cpp/com_gibbonsdimarco_yamec_app_jni_SystemMonitorManagerJNI.cpp
        yamec-jni/src/main/cpp/WmiQueryManager.cpp
        yamec-jni/src/main/cpp/ApplicationInfo.cpp
    )

    # Define symbol export macro for Windows
    target_compile_definitions(yamecjni PRIVATE BUILDING_DLL)

    # Set up include directories for the library
    target_include_directories(yamecjni
        PUBLIC
            $<BUILD_INTERFACE:${YAMEC_INCLUDE_DIR}>
            $<INSTALL_INTERFACE:include>
        PRIVATE
            yamec-jni/src/main/cpp
    )

    # Link against JNI libraries if they were found
    if(JNI_FOUND)
        target_link_libraries(yamecjni PRIVATE ${JNI_LIBRARIES})
        target_link_libraries(yamecjni PRIVATE pdh)
    endif()

    # Set output name to match what's referenced in the pom.xml
    set_target_properties(yamecjni PROPERTIES
        OUTPUT_NAME "yamecjni"
        PREFIX "")

    # Add test executable
    add_executable(yamec_tests
        yamec-jni/src/test/cpp/SystemMonitorManagerTest.cpp
        # Add other test files here as they are created
    )

    # Make sure the test builds after the library
    add_dependencies(yamec_tests yamecjni)

    # Set up include directories for the test
    target_include_directories(yamec_tests
        PRIVATE
            ${YAMEC_INCLUDE_DIR}
            yamec-jni/src/main/cpp
    )

    # Link test executable with needed libraries
    target_link_libraries(yamec_tests PRIVATE
        gtest
        gtest_main
        yamecjni
        pdh  # If needed by your tests
    )

    # Set working directory for tests
    set_target_properties(yamec_tests PROPERTIES
        VS_DEBUGGER_WORKING_DIRECTORY "${CMAKE_BINARY_DIR}"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}"
    )

    # Register tests with CTest
    include(GoogleTest)
    gtest_discover_tests(yamec_tests)

    # Add install command to copy the built library
    install(TARGETS yamecjni
        RUNTIME DESTINATION ${CMAKE_INSTALL_PREFIX}
        LIBRARY DESTINATION ${CMAKE_INSTALL_PREFIX}
        ARCHIVE DESTINATION ${CMAKE_INSTALL_PREFIX})

    # Install headers to include directory
    install(DIRECTORY ${YAMEC_INCLUDE_DIR}/
        DESTINATION include
    )
else()
    message(FATAL_ERROR "The OS this is being built on is not supported! Compilation halted.")
endif()