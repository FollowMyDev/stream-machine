set cmd=java -jar %1 %2 %3
set dir=%~dp0

echo Starting Process
wmic process call create "%cmd%"^, "%dir%"
