@echo off

SETLOCAL

SET JAVADOC_EXE=%JAVABINROOT%\javadoc.exe
SET JAVADOC_OUTPUT=html\

SET PATH_JAVADOC_FOLDER=..\..\..\..\doc\javadoc
SET PATH_JAVADOC_FOLDER_HTML=html
SET PATH_TO_SRC_FROM_JAVADOC=..\..\..\..\doc\doxygen

echo.
echo   ......................
echo   ... Create JavaDoc ...
echo   ...                ...
echo   ...    Cerberus    ...
echo   ......................
echo.
echo.  using %JAVADOC_EXE%
echo.
echo.   in folder %PATH_JAVADOC_FOLDER%\%PATH_JAVADOC_FOLDER_HTML%
echo.

cd %PATH_JAVADOC_FOLDER%

mkdir html

echo.
echo  ... Start JavaDoc ...
echo.

rem %JAVADOC_EXE% cerberus_html.dox

echo.
echo.   JavaDoc for  ---  Cerberus ---   ...... [DONE]
echo.
echo.    in folder %PATH_JAVADOC_FOLDER%\%JAVADOC_OUTPUT%
echo.

ENDLOCAL
