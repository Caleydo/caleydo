@echo off

SETLOCAL

SET DOXYGEN_OUTPUT=html\

SET PATH_DOXYGEN_FOLDER=..\..\..\..\doc\javadoc

echo.
echo.   Remove Javadoc for  ---  Cerberus  ---
echo.


cd %PATH_DOXYGEN_FOLDER%

echo.     from folder %PATH_DOXYGEN_FOLDER%\%DOXYGEN_OUTPUT%
echo.

pause


@echo on

rmdir /S /Q html\

@echo off

echo.
echo.   Javadoc for  ---  Cerberus ---   ...... [REMOVED]
echo.

pause

ENDLOCAL