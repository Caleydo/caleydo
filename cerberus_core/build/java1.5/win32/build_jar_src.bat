@echo off

IF "%1" == "" GOTO NO_VERSION_NUMBER

call subroutine_build_jar.bat %1 src
GOTO END

:NO_VERSION_NUMBER
call subroutine_build_jar.bat 000 src

:END