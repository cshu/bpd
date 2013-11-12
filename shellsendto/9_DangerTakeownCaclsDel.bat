:loop
if "%~1"=="" goto end
takeown /f %1
cacls %1 /e /p administrators:f
del/q %1
shift
goto loop
:end