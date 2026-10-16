# 1. Java 17 aur Tomcat 10 ka environment setup
FROM tomcat:10-jdk17

# 2. Pehle se maujood default Tomcat apps ko hatayein
RUN rm -rf /usr/local/tomcat/webapps/*

# 3. Apne WAR file ko ROOT.war ke naam se Tomcat mein daalein
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war

# 4. Tomcat port 8080 par start karein
EXPOSE 8080
CMD ["catalina.sh", "run"]