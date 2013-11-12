:loop
if "%~1"=="" goto end
takeown /f %1
shift
goto loop
:end