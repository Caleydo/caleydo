@echo off

IF "%1" == "" GOTO NO_VERSION_NUMBER

call subroutine_build_jar.bat %1 bin \class
GOTO END

:NO_VERSION_NUMBER
call subroutine_build_jar.bat 000 bin \class

:END
