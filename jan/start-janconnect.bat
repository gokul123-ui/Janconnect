@echo off
echo Starting JanConnect on http://localhost:8081 ...
set DB_PASSWORD=Gokul@2008
cd /d "%~dp0"
mvnw.cmd spring-boot:run
