@REM Maven Wrapper startup script for Windows
@REM Adapted from https://github.com/apache/maven-wrapper
@echo off

set MAVEN_PROJECTBASEDIR=%~dp0
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_PROPERTIES="%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.properties"

@REM Download maven-wrapper.jar if not present
if not exist %WRAPPER_JAR% (
    echo Downloading Maven Wrapper...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar' -OutFile '%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar' -UseBasicParsing"
)

@REM Find java.exe
set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo Error: JAVA_HOME is not set and no 'java' command could be found in your PATH.
goto error

:execute
"%JAVA_EXE%" ^
  -jar %WRAPPER_JAR% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
exit /b %ERROR_CODE%
