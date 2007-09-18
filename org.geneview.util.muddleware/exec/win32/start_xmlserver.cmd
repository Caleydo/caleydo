@echo off

SETLOCAL

SET path_to_bin_folder=..\..\bin\win32\server
SET path_bin_folder_to_exec_folder=..\..\..\exec\win32
SET muddleware_name=Muddlware_server_v1.0

echo.
echo. Run %muddleware_name% ..
echo.

echo Killing previous instace...
"c:\Program Files\PsKill\pskill.exe" XMLServerWin.exe
echo. Waiting until previous instance is closed...
sleep 1

cd %path_to_bin_folder%

echo.
echo. switched to folder %path_bin_folder_to_exec_folder%
echo. start %muddleware_name% ..

start XMLServerWind.exe

echo. start %muddleware_name% .... [done]
echo.

cd %path_bin_folder_to_exec_folder%

ENDLOCAL

rem pause
