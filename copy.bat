@echo off
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\libs L:\Doc\Git\LibInvRfidApi\libs\ /S

xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invutil\src\main L:\Doc\Git\LibInvRfidApi\invutil\src\main\ /S
:: xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invutil\libs L:\Doc\Git\LibInvRfidApi\invutil\libs\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invutil\build.gradle L:\Doc\Git\LibInvRfidApi\invutil

xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invsysdev\src\main L:\Doc\Git\LibInvRfidApi\invsysdev\src\main\ /S
:: xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invsysdev\libs L:\Doc\Git\LibInvRfidApi\invsysdev\libs\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invsysdev\build.gradle L:\Doc\Git\LibInvRfidApi\invsysdev

xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invsysctrl\src\main L:\Doc\Git\LibInvRfidApi\invsysctrl\src\main\ /S
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invsysctrl\libs L:\Doc\Git\LibInvRfidApi\invsysctrl\libs\ /S
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invsysctrl\jni L:\Doc\Git\LibInvRfidApi\invsysctrl\jni\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invsysctrl\build.gradle L:\Doc\Git\LibInvRfidApi\invsysctrl

xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invirp1\src\main L:\Doc\Git\LibInvRfidApi\invirp1\src\main\ /S
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invirp1\libs L:\Doc\Git\LibInvRfidApi\invirp1\libs\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invirp1\build.gradle L:\Doc\Git\LibInvRfidApi\invirp1

xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invcomirp1\src\main L:\Doc\Git\LibInvRfidApi\invcomirp1\src\main\ /S
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invcomirp1\libs L:\Doc\Git\LibInvRfidApi\invcomirp1\libs\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\invcomirp1\build.gradle L:\Doc\Git\LibInvRfidApi\invcomirp1

xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\lzrjobinv\src\main L:\Doc\Git\LibInvRfidApi\lzrjobinv\src\main\ /S
xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\lzrjobinv\libs L:\Doc\Git\LibInvRfidApi\lzrjobinv\libs\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\lzrjobinv\build.gradle L:\Doc\Git\LibInvRfidApi\lzrjobinv

xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\ziniulian\src\main L:\Doc\Git\LibInvRfidApi\ziniulian\src\main\ /S
:: xcopy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\ziniulian\libs L:\Doc\Git\LibInvRfidApi\ziniulian\libs\ /S
copy L:\Doc\SVN\Work\RfidComApi\trunk\src\RfidComApi\ziniulian\build.gradle L:\Doc\Git\LibInvRfidApi\ziniulian

pause
