@ECHO OFF

SET java_home="C:\Program Files\jdk1.8.0_45"
SET libname="libHttpNative.dll"
SET platform="win32"

ECHO ON

IF NOT EXIST build md build
IF NOT EXIST build\binaries md build\binaries
IF NOT EXIST build\binaries\debug md build\binaries\debug
IF NOT EXIST build\binaries\debug%platform% md build\binaries\debug\%platform%


gcc -fpic -shared -o build\binaries\debug\%platform%\%libname% -lcurl src\main\jni\HttpNative.c -I %java_home%\include\ -I %java_home%\include\%platform%\
