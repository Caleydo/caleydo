@echo off

echo.
echo ---  Cerberus Tester  ---
echo.

SETLOCAL

SET PATH_BATCH_FILE= 

call %PATH_BATCH_FILE%execute_by_ref.bat RUN cerberus.view.manager.tester.CanvasFromXMLFileTester


ENDLOCAL