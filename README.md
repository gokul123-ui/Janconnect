JanConnect — Unified Spring Boot Application
Overview
JanConnect is a citizen grievance platform that runs as a single Spring Boot application serving both the frontend and backend from http://localhost:8081.

Architecture
Browser → http://localhost:8081
             │
             ▼
    Spring Boot (port 8081)
    ├── /                    → React Frontend (index.html)
    ├── /assets/**           → Static JS/CSS/Images
    ├── /api/auth/**         → Authentication APIs
    ├── /api/complaints/**   → Complaint APIs
    ├── /api/ai/**           → AI Classification API
    └── /actuator/health     → Health Check
             │
             ▼
    MySQL (localhost:3306/janconnect)
Running the Application
Prerequisites
Java 21
Maven (or use included mvnw)
MySQL 8.x running on localhost:3306
Steps
Start MySQL and ensure the janconnect database exists (or it will be created automatically)

Set environment variable for DB password (optional — defaults to Gokul@2008):

$env:DB_PASSWORD = "your_mysql_password"
Run the application:

cd jan
.\mvnw.cmd spring-boot:run
Or run JanConnectApplication.java directly from IntelliJ IDEA.

Open browser: http://localhost:8081

Demo Accounts
Email	Password	Role
demo@janconnect.com	Demo@123	Citizen
citizen@janconnect.com	Citizen@123	Citizen
admin@janconnect.com	Admin@123	Admin
API Endpoints
Authentication
POST /api/auth/register — Register new user
POST /api/auth/login — Login (returns JWT)
GET /api/auth/me — Get current user (requires JWT)
POST /api/auth/logout — Logout
Complaints
POST /api/complaints — Create complaint
GET /api/complaints — List user's complaints
GET /api/complaints/{id} — Get complaint by ID
PUT /api/complaints/{id} — Update complaint
DELETE /api/complaints/{id} — Delete complaint
Evidence & Location
POST /api/complaints/{id}/evidence — Upload photo evidence (max 10MB)
GET /api/complaints/{id}/timeline — Get complaint timeline
AI
POST /api/ai/classify — AI grievance classification
Configuration
Edit src/main/resources/application.properties:

server.port=8081
spring.datasource.password=${DB_PASSWORD:Gokul@2008}
jwt.secret=${JWT_SECRET:your-secret}
ai.api.key=${AI_API_KEY:}
Frontend Rebuild
If you modify the React frontend:

cd C:\Users\rajas\Downloads\JanConnect\frontend
npm run build
Copy-Item dist\* -Destination ..\jan\jan\src\main\resources\static -Recurse -Force
Then restart the Spring Boot application.
