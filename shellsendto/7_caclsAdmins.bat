
:loop
if "%~1"=="" goto end
cacls %1 /e /p administrators:f

shift
goto loop
:end