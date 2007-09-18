@echo off

SETLOCAL

SET DOXYGEN_EXE=%DOXYGEN%\doxygen.exe
SET DOXYGEN_LOGO=html_doxygen\Cerberus_logo.png
SET DOXYGEN_ICON=html_doxygen\Cerberus.ico
SET DOXYGEN_OUTPUT=html\

SET PATH_DOXYGEN_FOLDER=..\..\..\..\doc\doxygen

@echo .
@echo '... Create Doxygen Doku ...'
@echo .
@echo.  using %DOXYGEN%\doxygen.exe
@echo .
@echo.   in folder %PATH_DOXYGEN_FOLDER%\%DOXYGEN_OUTPUT%
@echo .

cd %PATH_DOXYGEN_FOLDER%

@mkdir html

echo.  copy logo to output folder: %DOXYGEN_OUTPUT% ...
COPY /Y %DOXYGEN_LOGO% %DOXYGEN_OUTPUT%

echo.  copy icon to output folder: %DOXYGEN_OUTPUT% ...
COPY /Y %DOXYGEN_ICON% %DOXYGEN_OUTPUT%

echo .
echo '... Start Doxygen ...'
echo .

%DOXYGEN_EXE% cerberus_html.dox



echo.
echo.   Doxygen for  ---  Cerberus ---   ...... [DONE]
echo.
echo.    in folder %PATH_DOXYGEN_FOLDER%\%DOXYGEN_OUTPUT%
echo.

ENDLOCAL

