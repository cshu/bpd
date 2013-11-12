@echo off
powershell $argFile = '%1';if(!(Test-Path($argFile))){ echo 'No such file'; Exit; } $lastbackslash =  $argFile.LastIndexOf('\'); $lastdot = $argFile.LastIndexOf('.'); if($lastdot -gt $lastbackslash){ $output = $argFile.Remove($lastdot) + (Get-Date).ToString('yyyyMMddHHmmss') + $argFile.Substring($lastdot); } else{ $output = $argFile + (Get-Date).ToString('yyyyMMddHHmmss'); } if(Test-Path($output)){echo 'Filename exists (Rare exception)';Exit;} [System.IO.File]::Move($argFile, $output);

