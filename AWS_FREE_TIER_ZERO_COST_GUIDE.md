# AWS EC2 100% Free Tier Deployment Guide (Zero Cost Guarantee)

This guide guarantees that your **Online Shopping System (ShopHub)** deployment on Amazon Web Services (AWS) costs **₹0 / $0**.

---

## 🛡️ 4 Golden Rules to Stay 100% Free on AWS

| # | AWS Service | What to Select (FREE) | What to AVOID (CHARGES MONEY) |
|---|---|---|---|
| **1** | **EC2 Instance Type** | **`t2.micro`** (or `t3.micro` if region requires) - labeled *"Free tier eligible"* | `t2.small`, `t2.medium`, `t3.medium` |
| **2** | **OS Image (AMI)** | **Ubuntu Server 22.04 LTS (HVM)** - labeled *"Free tier eligible"* | RedHat Enterprise, SUSE, Windows Server |
| **3** | **Database** | **Install MySQL inside EC2** (Included in our script) | Separate AWS RDS (can incur charges if misconfigured) |
| **4** | **Storage (EBS)** | **8 GB to 20 GB gp3** (AWS free tier covers up to 30 GB) | > 30 GB or io1/io2 provisioned IOPS |
| **5** | **IP Address** | Use the **default Auto-assigned Public IPv4** | Do NOT allocate separate Elastic IPs |

---

## 🚀 3 Simple Steps to Deploy (Using 1-Click Script)

### Step 1: Upload Files from Your Windows Laptop to EC2
Open **PowerShell** on your laptop and run:
```powershell
# Navigate to your project folder
cd C:\Users\pc\.gemini\antigravity\scratch\online-shopping-system

# Upload WAR, SQL scripts, and automated installer script
scp -i "C:\Users\pc\Downloads\shopping-key.pem" target\online-shopping-system.war database\schema.sql database\sample_data.sql deploy_ec2.sh ubuntu@<YOUR-EC2-PUBLIC-IP>:/home/ubuntu/
```

### Step 2: Connect to EC2
In PowerShell:
```powershell
ssh -i "C:\Users\pc\Downloads\shopping-key.pem" ubuntu@<YOUR-EC2-PUBLIC-IP>
```

### Step 3: Run the 1-Click Automated Deployment Script
Inside EC2 terminal, simply type:
```bash
chmod +x deploy_ec2.sh
./deploy_ec2.sh
```

**That's it!** The script will automatically:
1. Update system packages
2. Install OpenJDK 17 LTS
3. Install & secure MySQL Server 8.0 (password: `Mukesh`, matching `db.properties`)
4. Create `shopping_db` and import all tables & sample data
5. Install Apache Tomcat 10.1 (Jakarta EE 10)
6. Deploy your `online-shopping-system.war` as `ROOT.war` (runs directly on root `/`)
7. Create a background systemd service (`tomcat.service`) so Tomcat automatically starts on server reboots

---

## 🌐 Verify Live Store
Open in your browser:
- **Storefront:** `http://<YOUR-EC2-PUBLIC-IP>:8080/`
- **Admin Portal:** `http://<YOUR-EC2-PUBLIC-IP>:8080/admin/login`
  - Username: `admin`
  - Password: `Admin@123`
- **Demo Customer:**
  - Email: `john.doe@example.com`
  - Password: `Customer@123`

---

## 🔔 How to Enable Free Tier Billing Alerts (Extra Safety)
1. Go to AWS Console & search for **Billing and Cost Management**.
2. Click **Budgets** on the left menu -> **Create budget**.
3. Choose **Zero spend budget** (or $1 alert).
4. Enter your email address.
   AWS will instantly email you if you ever approach or exceed free tier limits!
