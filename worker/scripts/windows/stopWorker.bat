set dir=%~dp0

echo Stopping Process
wmic Path win32_process Where "CommandLine Like '%%worker.yaml%%'" Call Terminate