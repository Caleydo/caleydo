@echo off

rem 
rem author: Michael Kalkusch
rem
rem creation date: 04-04-2006
rem
rem description: compile, run and create javadoc for Muddleware JAVA client
rem
rem usage: execute_by_ref.bat [COMPILE (target-files)|RUN (target-files)|JAVADOC|WIPE]
rem
rem examples:
rem         execute_by_ref.bat COMPILE (target-file)
rem         execute_by_ref.bat RUN (target-file)
rem         execute_by_ref.bat JAVADOC
rem

SETLOCAL

SET JAVA_BIN_PATH=C:\Compiler\JAVA\jdk1.5.0_04\bin\
SET JAVAC_EXE=%JAVA_BIN_PATH%javac.exe
SET JAVA_EXE=%JAVA_BIN_PATH%java.exe
SET JAVADOC_EXE=%JAVA_BIN_PATH%javadoc.exe

SET PROMETHEUS_PATH_BIN=..\..\..\bin\class
SET PROMETHEUS_PATH_SRC=..\..\..\src
SET PROMETHEUS_PATH_SRC_2BIN=..\bin\class
SET PROMETHEUS_PATH_SRC_2JAVADOC=..\doc\java_doc\html
SET PROMETHEUS_PATH_JAVADOC=..\..\doc\java_doc\html

rem
rem include jar files
rem

SET JAVA_JAR_PARAMETER=-classpath .;
SET JAVA_JAR_FILES=jogl.jar

SET PROMETHEUS_PATH_SRC_2JAR=..\bin\jar_ext\
SET PROMETHEUS_PATH_BIN_2JAR=..\jar_ext\

IF "%1"=="" GOTO NOPARAM
IF %1==JAVADOC GOTO JAVADOC
IF %1==WIPE GOTO WIPE

IF "%2"=="" GOTO NOPARAM

IF %1==COMPILE GOTO COMPILE
IF %1==RUN GOTO RUN


GOTO WRONGPARAM


REM +++++++++++++++
REM ++  COMPILE  ++
REM +++++++++++++++
:COMPILE

cd %PROMETHEUS_PATH_SRC%

echo.
echo.  now in %PROMETHEUS_PATH_SRC%
echo.
echo compile %JAVAC_EXE% %JAVA_JAR_PARAMETER%%PROMETHEUS_PATH_SRC_2JAR%%JAVA_JAR_FILES% -d %PROMETHEUS_PATH_SRC_2BIN% %2 
echo.

mkdir %PROMETHEUS_PATH_SRC_2BIN%

@echo on

%JAVAC_EXE% %JAVA_JAR_PARAMETER%%PROMETHEUS_PATH_SRC_2JAR%%JAVA_JAR_FILES%  -d %PROMETHEUS_PATH_SRC_2BIN% %2
@echo off

GOTO END



REM +++++++++++++++
REM ++    RUN    ++
REM +++++++++++++++
:RUN

cd %PROMETHEUS_PATH_BIN%

echo.
echo.  now in %PROMETHEUS_PATH_BIN%
echo.
echo run %JAVA_EXE% %JAVA_JAR_PARAMETER%%PROMETHEUS_PATH_BIN_2JAR%%JAVA_JAR_FILES% %2 
echo.

%JAVA_EXE% %JAVA_JAR_PARAMETER%%PROMETHEUS_PATH_BIN_2JAR%%JAVA_JAR_FILES%  %2

GOTO END



REM +++++++++++++++
REM ++  JAVA DOC ++
REM +++++++++++++++
:JAVADOC

ECHO.    create JavaDoc ...
ECHO.

cd %PROMETHEUS_PATH_SRC%

%JAVADOC_EXE% -d %PROMETHEUS_PATH_SRC_2JAVADOC% %2 %3 %4 %5 %6 %7 %8

ECHO.
ECHO.    created JavaDoc in folder %PROMETHEUS_PATH_SRC%\%PROMETHEUS_PATH_SRC_2JAVADOC%
ECHO.


GOTO END

REM +++++++++++++++
REM ++    WIPE   ++
REM +++++++++++++++
:WIPE

echo.
echo.  remove folder %PROMETHEUS_PATH_BIN%
echo.

dir %PROMETHEUS_PATH_BIN%

echo.

rmdir /S	 %PROMETHEUS_PATH_BIN%

echo.
echo. remove java_doc..
echo.

rmdir /S	%PROMETHEUS_PATH_JAVADOC%

GOTO END

REM +++++++++++++++
REM ++  NO PARAM ++
REM +++++++++++++++
:NOPARAM

ECHO  do not call this batch file without paramertes
ECHO.

GOTO WRONGPARAM



REM ++++++++++++++++++
REM ++  WRONG PARAM ++
REM ++++++++++++++++++
:WRONGPARAM

ECHO  wrong paramertes
ECHO. usage:
ECHO.         execute_by_ref.bat COMPILE (target-file)
ECHO.         execute_by_ref.bat RUN (target-file)
ECHO.         execute_by_ref.bat JAVADOC
ECHO.         execute_by_ref.bat WIPE
ECHO.



REM +++++++++++++++
REM ++    ENDE   ++
REM +++++++++++++++
:END

ENDLOCAL