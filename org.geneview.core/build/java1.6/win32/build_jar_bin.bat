@echo off

IF "%1" == "" GOTO NO_VERSION_NUMBER

call subroutine_build_jar.bat bin \class %1
GOTO END

:NO_VERSION_NUMBER
call subroutine_build_jar.bat bin \class

:END
