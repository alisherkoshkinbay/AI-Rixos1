@echo off
setlocal
cd /d "%~dp0"
if not exist out mkdir out
set "SRC_LIST=%TEMP%\qonaq_sources_%RANDOM%.txt"
(for /r "src\main\java" %%f in (*.java) do @echo "%%f") > "%SRC_LIST%"
javac --release 17 -encoding UTF-8 -d out @"%SRC_LIST%"
if errorlevel 1 (
    echo Compilation failed. Ensure that JDK 17 or newer is installed.
    pause
    del "%SRC_LIST%"
    exit /b 1
)
del "%SRC_LIST%"
java -cp out kz.qonaq.app.Main
if errorlevel 1 pause
endlocal
