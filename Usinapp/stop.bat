@ECHO OFF
FOR /F %%i IN (./application/application.pid) DO taskkill /F /PID %%i