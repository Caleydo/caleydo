@echo off



SETLOCAL

SET DOXYGEN_OUTPUT=html\
SET DOXYGEN_OUTPUT_DOT=html_dot\

SET PATH_DOXYGEN_FOLDER=..\..\..\..\doc\doxygen

echo.
echo.   Remove Doxygen for  ---  Cerberus  ---
echo.


cd %PATH_DOXYGEN_FOLDER%

echo.     from folder %PATH_DOXYGEN_FOLDER%\%DOXYGEN_OUTPUT%
echo.     from folder %PATH_DOXYGEN_FOLDER%\%DOXYGEN_OUTPUT_DOT%
echo.

pause


@echo on

rmdir /S /Q html\

@echo .

rmdir /S /Q html_dot\

@echo off

echo.
echo.   Doxygen for  ---  Cerberus ---   ...... [REMOVED]
echo.

pause

ENDLOCAL