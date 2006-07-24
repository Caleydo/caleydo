@echo off
cls

rem ..................................
rem author: Michael Kalkusch
rem batch file for latex under Windows
rem creation: 24-07-2006
rem ..................................


setlocal 

set path_latex=
set path_local_src_latex=..\..
set path_local_build=build\win32
set path_latex_ouput_directory=bin_pdf
set latex_input_file=main
set latex_debug_info=all_pdf_debug_info.txt


cd %path_local_src_latex%


echo.
echo    makeindex  .... RELEASE .... 
echo.

rem %path_latex%pdflatex %latex_input_file%.tex
rem %path_latex%makeindex %path_latex_ouput_directory%/%latex_input_file%.idx

echo.
echo    makeindex  ....   [done]
echo.
echo.

echo.
echo    pdflatex  .... RELEASE .... 
echo.
echo.
echo on

rem %path_latex%pdflatex -quiet %latex_input_file%.tex > tmp_latex_output.txt
rem %path_latex%pdflatex -output-directory=%path_latex_ouput_directory% %latex_input_file%.tex > %path_latex_ouput_directory%/%latex_debug_info%
%path_latex%pdflatex -output-directory=%path_latex_ouput_directory% %latex_input_file%.tex 

@echo off
echo.
echo.
echo    pdflatex  .... RELEASE .... [done]
echo.
echo.

dir

pause

cd %path_local_build%

dir

endlocal 

