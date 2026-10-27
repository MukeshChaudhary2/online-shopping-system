#!/bin/bash
# ==============================================================================
# Online Shopping System (ShopHub) - Automated AWS EC2 Setup & Deployment Script
# Target OS: Ubuntu 22.04 LTS (100% AWS Free Tier)
# ==============================================================================

set -e

echo "=========================================================="
echo " Starting ShopHub Automated Deployment on AWS EC2 (Ubuntu)"
echo "=========================================================="

# 1. Update Ubuntu Packages
echo "--> 1/7: Updating Ubuntu system packages..."
sudo apt update -y
sudo apt upgrade -y

# 2. Install OpenJDK 17 LTS
echo "--> 2/7: Installing OpenJDK 17..."
sudo apt install openjdk-17-jdk -y
java -version

# 3. Install MySQL Server 8.0
echo "--> 3/7: Installing MySQL Server 8.0..."
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo systemctl enable mysql

# 4. Configure MySQL Database & Credentials
echo "--> 4/7: Configuring MySQL database 'shopping_db' and password..."
sudo mysql <<EOF
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Mukesh';
CREATE DATABASE IF NOT EXISTS shopping_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
FLUSH PRIVILEGES;
EOF

# Import schema and sample data if present in home directory
if [ -f "/home/ubuntu/schema.sql" ]; then
    echo "Importing schema.sql..."
    mysql -u root -pMukesh shopping_db < /home/ubuntu/schema.sql
fi

if [ -f "/home/ubuntu/sample_data.sql" ]; then
    echo "Importing sample_data.sql..."
    mysql -u root -pMukesh shopping_db < /home/ubuntu/sample_data.sql
fi

# 5. Install Apache Tomcat 10.1 (Jakarta EE 10)
echo "--> 5/7: Installing Apache Tomcat 10.1..."
if [ ! -d "/opt/tomcat10" ]; then
    cd /tmp
    wget -q https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.28/bin/apache-tomcat-10.1.28.tar.gz
    sudo tar -xzf apache-tomcat-10.1.28.tar.gz -C /opt/
    sudo mv /opt/apache-tomcat-10.1.28 /opt/tomcat10
    sudo chmod +x /opt/tomcat10/bin/*.sh
fi

# 6. Deploy the Application WAR as ROOT.war
echo "--> 6/7: Deploying online-shopping-system.war as ROOT.war..."
sudo rm -rf /opt/tomcat10/webapps/ROOT /opt/tomcat10/webapps/ROOT.war

if [ -f "/home/ubuntu/online-shopping-system.war" ]; then
    sudo cp /home/ubuntu/online-shopping-system.war /opt/tomcat10/webapps/ROOT.war
    echo "Copied war to /opt/tomcat10/webapps/ROOT.war"
fi

# Ensure uploads directory has full write permissions
sudo mkdir -p /opt/tomcat10/webapps/ROOT/uploads
sudo chmod -R 777 /opt/tomcat10/webapps/

# 7. Create Systemd Service for Tomcat (Auto-start on boot)
echo "--> 7/7: Configuring Tomcat Systemd Service..."
sudo tee /etc/systemd/system/tomcat.service > /dev/null <<EOF
[Unit]
Description=Apache Tomcat 10 Web Application Container
After=network.target mysql.service

[Service]
Type=forking
User=root
Group=root
Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
Environment="CATALINA_PID=/opt/tomcat10/temp/tomcat.pid"
Environment="CATALINA_HOME=/opt/tomcat10"
Environment="CATALINA_BASE=/opt/tomcat10"
Environment="CATALINA_OPTS=-Xms256M -Xmx512M -server -XX:+UseG1GC"

ExecStart=/opt/tomcat10/bin/startup.sh
ExecStop=/opt/tomcat10/bin/shutdown.sh
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable tomcat
sudo systemctl restart tomcat

echo "=========================================================="
echo " DEPLOYMENT COMPLETE! ShopHub is LIVE on AWS EC2."
echo " Open: http://$(curl -s http://checkip.amazonaws.com):8080/"
echo "=========================================================="
