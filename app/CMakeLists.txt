# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

# CMAKE_VERBOSE_MAKEFILE on 输出详细的编译和链接信息
set(CMAKE_VERBOSE_MAKEFILE on)

set(libs "${CMAKE_SOURCE_DIR}/src/main/jniLibs")

include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp)



add_library(libscanprn SHARED IMPORTED)
set_target_properties(libscanprn  PROPERTIES
        IMPORTED_LOCATION  "${libs}/${ANDROID_ABI}/libscanprn.so")

add_library(libbitmap SHARED IMPORTED)
set_target_properties(libbitmap  PROPERTIES
        IMPORTED_LOCATION  "${libs}/${ANDROID_ABI}/libbitmap.so")

add_library(libConfigFileINI SHARED IMPORTED)
set_target_properties(libConfigFileINI  PROPERTIES
        IMPORTED_LOCATION  "${libs}/${ANDROID_ABI}/libConfigFileINI.so")

add_library(libflog SHARED IMPORTED)
set_target_properties(libflog  PROPERTIES
        IMPORTED_LOCATION  "${libs}/${ANDROID_ABI}/libflog.so")

add_library(libusb1.0 SHARED IMPORTED)
set_target_properties(libusb1.0  PROPERTIES
        IMPORTED_LOCATION  "${libs}/${ANDROID_ABI}/libusb1.0.so")

add_library(libHWICgltBCR SHARED IMPORTED)
set_target_properties(libHWICgltBCR  PROPERTIES
                IMPORTED_LOCATION  "${libs}/${ANDROID_ABI}/libHWICgltBCR.so")



set(CMAKE_CXX_FLAGS "-std=c++11")


add_library( # Sets the name of the library.
             native-printer

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/native-printer.cpp )

add_library( # Sets the name of the library.
             native-hscanner

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/native-hscanner.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.



find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log
               )



# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-hscanner
                       libHWICgltBCR

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

target_link_libraries( # Specifies the target library.
                       native-printer
                       libscanprn
                       libConfigFileINI
                       libflog
                       libusb1.0
                       libbitmap
                       # Links the target library to the log library
                       # included in the NDK.

                       ${log-lib}
                       )
