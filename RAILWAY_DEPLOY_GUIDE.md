# Railway.app 1-Click Deployment Guide

You can deploy the exact same tested project on **Railway.app** in under 5 minutes without touching any code!

---

## 🛠️ What We Already Configured for Railway:
1. **`Dockerfile` added:** Pre-configured multi-stage build with Java 17, Maven, Apache Tomcat 10.1, and dynamic `$PORT` routing.
2. **`DBConnectionUtil.java` updated:** Automatically detects Railway's MySQL environment variables (`MYSQLHOST`, `MYSQLPORT`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`) and falls back to `db.properties` on local/AWS.

---

## 🚀 Step-by-Step Railway Deployment:

### Step 1: Push Code to GitHub
Open **PowerShell** in the project folder and push to your GitHub account:
```powershell
cd C:\Users\pc\.gemini\antigravity\scratch\online-shopping-system

git init
git add .
git commit -m "Initial commit for Railway and AWS deployment"
git branch -M main
git remote add origin https://github.com/<YOUR_GITHUB_USERNAME>/online-shopping-system.git
git push -u origin main
```

---

### Step 2: Create Railway Project with MySQL
1. Go to [railway.app](https://railway.app) and sign in with GitHub.
2. Click **"New Project"** -> **"Provision MySQL"**.
3. Railway will provision a dedicated MySQL 8 database in 5 seconds.

---

### Step 3: Import Database Schema & Sample Data
1. Click on the **MySQL** service box in Railway.
2. Go to the **"Connect"** tab: Copy the **Public Connection URL** (or use Railway's built-in **Data** tab).
3. Connect using **MySQL Workbench** or any DB tool with those credentials, and run:
   - `database/schema.sql`
   - `database/sample_data.sql`

---

### Step 4: Deploy the Web Application
1. In the same Railway project canvas, click **"+ New"** -> **"GitHub Repo"**.
2. Select your `online-shopping-system` repository.
3. Railway will immediately detect the `Dockerfile` and start building with Maven & Tomcat 10!
4. Go to your web service's **"Variables"** tab -> Click **"Add Reference"** and link the MySQL variables (`MYSQLHOST`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`).
5. Go to **"Settings"** -> **Networking** -> Click **"Generate Domain"**.

---

### 🌐 Your App is Live!
Railway gives you a public HTTPS URL:
`https://online-shopping-system-production.up.railway.app/`

- **Admin Login:** `/admin/login` (User: `admin`, Pass: `Admin@123`)
- **Customer Login:** `/login` (User: `john.doe@example.com`, Pass: `Customer@123`)
