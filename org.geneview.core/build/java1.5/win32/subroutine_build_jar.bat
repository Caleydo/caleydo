@echo off

rem test if JAVAHOME environment variable ist set...
IF NOT "%JAVAHOME%"=="" GOTO JAVAHOME_IS_SET

:NO_JAVAHOMEE
echo.
echo.  Enviroment variable JAVAHOME need to be set to java.exe   (java 1.6 or higher)
echo.
GOTO END

:JAVAHOME_IS_SET

SETLOCAL

rem you can switch between "src" and "bin"

SET destination=%1
SET destination2=%2

SET version=%3

IF NOT "%3"=="" SET version=_r%3

SET path_org_geneview_core=..\..\..\

SET path_org_geneview_lib_jogl_jar=%path_org_geneview_core%..\org.geneview.lib\lib\jogl\*.jar
SET path_org_geneview_core_jogl_jar=%path_org_geneview_core%lib\jogl\
SET file_org_geneview_core_jogl_jar=%path_org_geneview_core%lib\jogl\jogl.jar

SET path_org_geneview_lib_swt_jar=%path_org_geneview_core%..\org.geneview.lib\lib\swt\*.jar
SET path_org_geneview_core_swt_jar=%path_org_geneview_core%lib\swt\
SET file_org_geneview_core_swt_jar=%path_org_geneview_core%lib\swt\swt.jar

SET path_org_geneview_rcp_lib=%path_org_geneview_core%..\org.geneview.rcp\lib\

rem path and file name for jar.exe
SET file_jar=%JAVAHOME%\jar.exe

rem path to bin folder were *.class files are stored
SET class_files=%path_org_geneview_core%%destination%%destination2%


rem path and filename of resulting jar file
SET jar_file_name=%path_org_geneview_core%lib\org.geneview.core_%destination%%version%.jar

rem test if jogl.jar and swt.jar is in place.
IF NOT EXIST %file_org_geneview_core_jogl_jar% GOTO COPY_PLATFORM_DEPANDENT_JAR
IF NOT EXIST %file_org_geneview_core_swt_jar% GOTO COPY_PLATFORM_DEPANDENT_JAR

GOTO MAKE_JAR

:COPY_PLATFORM_DEPANDENT_JAR

echo.
echo. copy %path_org_geneview_lib_jogl_jar% %path_org_geneview_core_jogl_jar%
echo. copy %path_org_geneview_lib_swt_jar% %path_org_geneview_core_swt_jar%
echo.

copy %path_org_geneview_lib_jogl_jar% %path_org_geneview_core_jogl_jar%
copy %path_org_geneview_lib_swt_jar% %path_org_geneview_core_swt_jar%


echo.
echo.   make jar next..
pause

:MAKE_JAR

echo info= %class_files%   %jar_file_name%
echo.
echo create jar in folder %jar_file_name%
echo.

pause

IF NOT EXIST %file_jar% GOTO NO_JAR_EXE

%file_jar% cvfM %jar_file_name% -C  %class_files% .

echo.
echo info= %class_files%   %jar_file_name%
echo.
echo jar in folder %jar_file_name% .. [done]
echo.

echo. copy to rcp:
echo. copy %jar_file_name%
echo.      %path_org_geneview_rcp_lib%

copy %jar_file_name% %path_org_geneview_rcp_lib%

echo.
echo. copy to %path_org_geneview_rcp_lib%   ..[done]
echo.

GOTO END

:NO_JAR_EXE

IF EXIST %%1 GOTO REPLACE_JAR_PATH
echo jar.exe could not be found
echo.
echo curretn path to jar.exe: %file_jar%
echo.
echo. usage: %0 [path/jar.exe]
echo.

:END

ENDLOCAL

pause
