@echo off
setlocal

set BASE_DIR=%~dp0
cd /d %BASE_DIR%\..

echo [1/5] starting nacos...
start "nacos" cmd /k "cd /d D:\nacos-server-2.3.2\nacos\bin && startup.cmd -m standalone"

timeout /t 10 >nul

echo [2/5] starting gateway-service...
start "gateway-service" cmd /k "cd /d %cd%\gateway-service && mvn spring-boot:run"

timeout /t 8 >nul

echo [3/5] starting user-service...
start "user-service" cmd /k "cd /d %cd%\user-service && mvn spring-boot:run"

timeout /t 8 >nul

echo [4/5] starting item-service...
start "item-service" cmd /k "cd /d %cd%\item-service && mvn spring-boot:run"

timeout /t 8 >nul

echo [5/5] starting admin-service...
start "admin-service" cmd /k "cd /d %cd%\admin-service && mvn spring-boot:run"

echo all services started.
endlocal