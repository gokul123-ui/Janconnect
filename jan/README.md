# JanConnect — AI-Powered Citizen Grievance Navigator

JanConnect is an AI-powered citizen grievance platform designed to make public-service complaint registration simpler, faster, and more accessible.

The application allows citizens to describe a grievance using **text or voice**, understand the issue using AI, identify missing information, route the complaint to the appropriate department, generate a structured complaint, attach evidence and location information, submit the complaint, and track its status through a timeline.

JanConnect is implemented as a **single Spring Boot application** that serves both the frontend and backend from:

**http://localhost:8081**

---

## 🚀 Project Overview

Citizens often face difficulties when reporting public-service problems such as:

- Water supply issues
- Electricity problems
- Road damage
- Drainage problems
- Garbage and waste-management issues

Common challenges include:

- Not knowing which department is responsible
- Not knowing what information is required
- Difficulty writing formal complaints
- Language barriers
- Limited digital literacy
- Difficulty tracking complaint progress
- Lack of transparency after submission

JanConnect addresses these problems through a guided AI-assisted grievance workflow.

### Core Workflow

```text
Voice / Text Input
        ↓
AI Understands Grievance
        ↓
Extract Complaint Information
        ↓
Check Missing Information
        ↓
Identify Department
        ↓
Route Complaint
        ↓
Generate Structured Complaint
        ↓
Citizen Reviews & Confirms
        ↓
Add Photo / Evidence
        ↓
Add Location
        ↓
Submit Complaint
        ↓
Generate Complaint ID
        ↓
Track Complaint Timeline
        ↓
Resolution
