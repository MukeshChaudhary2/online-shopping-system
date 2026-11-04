# 🚀 ShopHub Online Shopping System - Render Free Deployment Guide (24/7 Hamesha Live)

Ye guide aapko project ko **Render.com** par **100% Free**, bina kisi credit card ke, aur **24/7 Hamesha Live** rakhne ka complete step-by-step tarika batata hai.

---

## 📌 Architecture & Zero-Cost Guarantee (₹0 Hamesha)

| Component | Service | Cost | Features |
| :--- | :--- | :--- | :--- |
| **Web Service** | [Render.com](https://render.com) (Docker) | **₹0 Free** | 750 Free Hours/month (1 service 24x7 = 744 hrs) |
| **MySQL Database** | [TiDB Cloud](https://tidbcloud.com) (Serverless) | **₹0 Free Forever** | 5 GB Storage, MySQL 8 compatible, No Expiry |
| **24/7 Keep-Alive** | [UptimeRobot](https://uptimerobot.com) | **₹0 Free** | Har 5 minute me `/health` ko ping karke sleep rokta hai |

---

## 🛠 Project me humne kya-kya tayyar kiya hai?

1. **`DatabaseInitializer.java`**: Aapko Cloud database me manually SQL queries ya tables run karne ki zarurat nahi hai. Jab aapka project Render par start hoga, ye automatically check karega aur agar database blank hai toh `schema.sql` aur `sample_data.sql` execute karke 11 tables aur sample data seed kar dega!
2. **`DBConnectionUtil.java`**: TiDB, Render, Railway, AWS ya Local MySQL kisi bhi environment variable (`DB_HOST`, `DB_URL`, `MYSQLHOST`, `DATABASE_URL`) aur SSL ko automatically detect karta hai.
3. **`HealthServlet.java` (`/health` & `/ping`)**: Ultra-lightweight endpoint jo UptimeRobot aur Render health checks ke liye ready hai.
4. **`Dockerfile` & `render.yaml`**: Multi-stage Docker build jo Maven se compile karke Apache Tomcat 10.1 par dynamic `$PORT` ke sath deploy karta hai.

---

## 📋 Step 1: Code ko apne GitHub par Push karein (Sirf 1 baar)

Aapke project me Git repository already initialize aur commit ho chuki hai. Bas apne GitHub account par push karna hai:

1. Apne browser me **[GitHub.com](https://github.com)** kholein aur login karein.
2. Top right me **`+`** icon par click karke **"New repository"** chunein.
3. Repository name likhein: `online-shopping-system` (Public ya Private dono chalega).
4. **"Create repository"** button par click karein.
5. Ab apne computer me PowerShell / Terminal kholein aur ye commands chalayein:

```powershell
cd "C:\Users\pc\.gemini\antigravity\scratch\online-shopping-system"

# Apne username ke sath URL replace karein:
git remote add origin https://github.com/<YOUR-GITHUB-USERNAME>/online-shopping-system.git
git branch -M main
git push -u origin main
```

*(Agar GitHub login credentials ya token maange toh sign in complete karein).*

---

## 🗄️ Step 2: TiDB Cloud par Free MySQL Database Banayein (2 Minute, No Credit Card)

Render ka apna free PostgreSQL database 30 din baad delete ho jata hai, aur Render par MySQL nahi milta. Isliye hum **TiDB Cloud Serverless** use karenge jo **100% Free Forever**, MySQL 8 syntax compatible hai aur kabhi expire nahi hota!

1. Browser me **[https://tidbcloud.com](https://tidbcloud.com)** kholein aur **"Sign Up"** karein (Google ya GitHub se 1-click login).
2. Login ke baad **"Create Cluster"** par click karein.
3. **"Serverless" (Free $0/mo)** option select karein.
4. Region me **AWS / Singapore** ya **Mumbai** select karein (Asia users ke liye fast hota hai).
5. Cluster name me `shophub-db` likhein aur **"Create"** par click karein.
6. Cluster create hone par screen par **"Connect"** popup aayega:
   - **User**: (Jaise: `xxxx.root`)
   - **Password**: "Generate Password" par click karke password copy kar lein!
   - **Host**: (Jaise: `gateway01.ap-southeast-1.prod.aws.tidbcloud.com`)
   - **Port**: `4000`
   - **Database**: `test` (default database)

> 💡 **Note:** Aapko tables banane ki koi chinta nahi karni! Humne jo `DatabaseInitializer` banaya hai, wo application start hote hi apne aap sari 11 tables aur sample products create kar dega.

---

## 🌐 Step 3: Render.com par Deploy karein (3 Minute)

1. Browser me **[https://render.com](https://render.com)** kholein aur **"Sign In with GitHub"** karein.
2. Top bar me **"New +"** button par click karein aur **"Web Service"** select karein.
3. **"Build and deploy from a Git repository"** chunein aur Next karein.
4. Apni GitHub repository `online-shopping-system` select karke **"Connect"** par click karein.
5. Form me ye details bharein:
   - **Name**: `shophub-store` (ya apni pasand ka koi bhi naam)
   - **Region**: Singapore (jo TiDB ke paas ho)
   - **Branch**: `main`
   - **Runtime**: `Docker` (Render apne aap Dockerfile detect kar lega)
   - **Instance Type**: **Free** ($0/month)
6. Ab niche scroll karein aur **"Advanced"** par click karein:
   - **Health Check Path**: `/health` likhein.
   - **Auto-Deploy**: `Yes`
7. Ab **"Add Environment Variable"** par click karke ye variables add karein:

| Key | Value (Aapke TiDB Connect Screen se) |
| :--- | :--- |
| **`DB_HOST`** | `gateway01.ap-southeast-1.prod.aws.tidbcloud.com` (apna TiDB host) |
| **`DB_PORT`** | `4000` |
| **`DB_USER`** | `xxxx.root` (apna TiDB user) |
| **`DB_PASSWORD`** | (Aapka TiDB generated password) |
| **`DB_NAME`** | `test` |

8. Niche **"Create Web Service"** button par click karein!

Render Maven se project build karega aur Tomcat container start karega.
Aapko Logs me dikhega:
```
INFO: Database connection verified and HikariCP initialized successfully.
INFO: No existing tables detected. Starting automatic database provisioning...
INFO: Database provisioning completed successfully: schema and initial data loaded.
```

2 se 3 minute me status **"Live"** ho jayega aur aapko aapka URL mil jayega:
👉 `https://shophub-store.onrender.com`

---

## ⏰ Step 4: Render ko Hamesha Live Rakhne Ka Trick (Never Sleep 24/7)

Render ka Free tier 15 minute tak koi user na aane par service ko "Sleep" kar deta hai. Jab koi 15 min baad aata hai toh website khulne me 50 seconds lagte hain.

**Hamesha Live (0 seconds lag) rakhne ka 100% Free solution:**

1. Browser me **[https://uptimerobot.com](https://uptimerobot.com)** kholein aur Free Account banayein (Google se 1 click).
2. Dashboard me **"Add New Monitor"** button dabayein:
   - **Monitor Type**: `HTTP(s)`
   - **Friendly Name**: `ShopHub Render Ping`
   - **URL (or IP)**: `https://shophub-store.onrender.com/health` (apna Render URL daalein)
   - **Monitoring Interval**: `5 minutes`
3. **"Create Monitor"** par click karein.

### 🎯 Ye kaise kaam karta hai?
- UptimeRobot har 5 minute me aapke Render URL par `/health` request bhejta hai.
- Render dekhta hai ki server active hai, isliye Render ka 15-minute ka inactivity timer **kabhi trigger hi nahi hota**!
- Aapka server **24 ghante, 365 din hamesha ON** rahega, kabhi sleep nahi karega, aur Render ke 750 free hours me hi rahega (1 month = 744 hours, isliye ₹0 charge)!

---

## 🔑 Default Login Credentials (Testing ke liye)

### 👨‍💼 Admin Panel:
- **URL**: `https://your-app.onrender.com/admin/login`
- **Username**: `admin`
- **Password**: `Admin@123`

### 🛒 Customer Login:
- **URL**: `https://your-app.onrender.com/login`
- **Email**: `john.doe@example.com`
- **Password**: `Customer@123`
- *(Naya user Register bhi kar sakte hain, instant login kaam karega).*

---

## ❓ Troubleshooting & FAQs

1. **Kya mujhe koi credit card add karna hoga?**
   Nahi! Render, TiDB Cloud aur UptimeRobot teeno bina credit card ke free tier offer karte hain.
2. **Kya data restart hone par delete hoga?**
   Nahi! TiDB Cloud ek standalone cloud database hai, isliye aapka users, orders aur products ka sara data permanently safe rahega.
3. **Agar mujhe code me koi change karna ho?**
   Aap local computer me code change karein, `git commit` aur `git push` karein. Render automatically naya code detect karke re-deploy kar dega!
