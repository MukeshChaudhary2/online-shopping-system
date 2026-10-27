# ==============================================================================
# Multi-Stage Dockerfile for Railway.app, Render, and Container Deployments
# Java 17 + Apache Tomcat 10.1 (Jakarta EE 10)
# ==============================================================================

# Stage 1: Build WAR application with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Deploy to Apache Tomcat 10.1 Container
FROM tomcat:10.1.28-jdk17

# Remove default ROOT and examples
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy built WAR as ROOT.war (Serves application on root URL "/")
COPY --from=builder /build/target/online-shopping-system.war /usr/local/tomcat/webapps/ROOT.war

# Create uploads directory and set permissions
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/uploads && chmod -R 777 /usr/local/tomcat/webapps/

# Default port 8080 or Railway dynamic PORT
EXPOSE 8080

# Configure dynamic PORT binding for Railway/Render and start Tomcat
CMD sh -c 'sed -i "s/port=\"8080\"/port=\"${PORT:-8080}\"/g" /usr/local/tomcat/conf/server.xml && catalina.sh run'
