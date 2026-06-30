@REM Maven Wrapper startup script for Windows
@if "%DEBUG%"=="" @echo off
@setlocal
set "MAVEN_PROJECTBASEDIR=%~dp0"
set "CLASSWORLDS_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set "CLASSWORLDS_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"
set "MAVEN_CONFIG=%USERPROFILE%\.m2"
java -cp "%CLASSWORLDS_JAR%" "%CLASSWORLDS_LAUNCHER%" %*
@endlocal
