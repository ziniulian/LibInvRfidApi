@echo off
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\api\src\main L:\Doc\Git\LibInvRfidApi\api\src\main\ /S
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\api\libs L:\Doc\Git\LibInvRfidApi\api\libs\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\api\build.gradle L:\Doc\Git\LibInvRfidApi\api
pause
