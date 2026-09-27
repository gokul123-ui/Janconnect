# JanConnect — AI-Powered Citizen Grievance Navigator

JanConnect is a multilingual citizen grievance platform that helps people report public-service problems through voice or text.

The application understands the citizen's grievance, identifies missing information, routes the complaint to the appropriate department, generates a structured complaint, allows the citizen to review and confirm it, and provides complaint tracking.

JanConnect runs as a **single Spring Boot application** that serves both the frontend and backend.

---

## 🚀 Overview

Citizens may face difficulties when reporting issues such as:

- Water supply problems
- Electricity issues
- Road problems
- Drainage complaints
- Garbage/waste management

They may not know:

- Which department is responsible
- What information is required
- How to formally describe the issue
- How to track their complaint

JanConnect simplifies this process using AI-assisted grievance understanding and multilingual interaction.

---

## ✨ Key Features

### 🔐 Authentication

- User registration
- User login
- JWT-based authentication
- BCrypt password hashing
- Logout
- Protected APIs
- Demo citizen accounts
- Admin role support

### 🌐 Multilingual Support

Supports:

- English
- Tamil
- Hindi

### 🎙️ Grievance Input

Citizens can provide complaints using:

- Voice input
- Text input

### 🤖 AI Grievance Processing

JanConnect can:

1. Understand the grievance
2. Extract important information
3. Identify the complaint category
4. Detect missing information
5. Identify the responsible department
6. Determine priority
7. Generate a structured complaint

### 📝 Complaint Workflow

The complete workflow is:

```text
Voice/Text Input
       ↓
AI Understands Grievance
       ↓
Check Missing Information
       ↓
Identify Department
       ↓
Generate Structured Complaint
       ↓
Citizen Reviews Complaint
       ↓
Citizen Confirms
       ↓
Add Photo / Location
       ↓
Submit Complaint
       ↓
Generate Complaint ID
       ↓
Track Complaint Timeline
       ↓
Resolution
