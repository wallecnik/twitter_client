@ECHO OFF

SET java_home="<fill-in-the-jdk-location>"
SET libname="libHttpNative.dll"
SET platform="win32"

ECHO ON

IF NOT EXIST build md build
IF NOT EXIST build\binaries md build\binaries
IF NOT EXIST build\binaries\debug md build\binaries\debug
IF NOT EXIST build\binaries\debug\%platform% md build\binaries\debug\%platform%


g++ -fpic -shared -o build\binaries\debug\%platform%\%libname% -lcurl src\main\jni\HttpNative.cpp -I %java_home%\include\ -I %java_home%\include\%platform%\
