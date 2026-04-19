@echo off
echo stopping java services...

for %%p in (8080 8081 8082 8083 8848) do (
  for /f "tokens=5" %%a in ('netstat -ano ^| findstr :%%p ^| findstr LISTENING') do (
    taskkill /PID %%a /F
  )
)

echo done.