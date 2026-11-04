# Online Shopping System (BCSP-064 Final Year BCA Project)

An enterprise-grade, full-stack E-Commerce web application developed strictly using **Core Java 17 (JDK 17)**, **Jakarta Servlets 6.0**, **JavaServer Pages (JSP 3.1)**, **JSTL 3.0**, **MySQL 8.0**, and **HikariCP** connection pooling, built with **Apache Maven** for deployment on **Apache Tomcat 10+**.

---

## 1. Project Technology Stack

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Java 17 (JDK 17 LTS) | Modern LTS Core Java |
| **Architecture** | MVC + DAO Pattern | Clean separation of Model, View, Controller, and DAO |
| **Server** | Apache Tomcat 10.1+ | Jakarta EE 10 Servlet Container |
| **Build Tool** | Apache Maven 3.9+ | Dependency management and WAR packager |
| **Database** | MySQL 8.0 (InnoDB) | Relational DB with FKs, cascades, check constraints, indexes |
| **Connection Pool** | HikariCP 5.1.0 | Fast, production-ready JDBC connection pooling |
| **Security** | jBCrypt 0.4 | Salted BCrypt password hashing (workload cost 12) |
| **Security Filter**| Custom Jakarta AuthFilter | Route guards for `/admin/*` and customer protected paths |
| **CSRF Defense** | CSRFUtil | Synchronized session tokens preventing forgery |
| **Frontend** | HTML5, CSS3, Vanilla JS | Mobile-first responsive CSS Grid & Flexbox, no heavy frameworks |
| **Testing** | JUnit 5 (Jupiter) | Automated unit & integration tests |

---

## 2. Directory Structure

```text
online-shopping-system/
├── pom.xml                                <-- Maven configuration file
├── README.md                              <-- Project documentation & setup guide
├── database/
│   ├── schema.sql                         <-- Full MySQL 8 DDL schema (11 tables)
│   └── sample_data.sql                    <-- Pre-seeded data with BCrypt hashes
├── src/
│   ├── main/
│   │   ├── java/com/shopping/
│   │   │   ├── dao/                       <-- Data Access Layer (11 DAOs)
│   │   │   │   ├── AdminDAO.java
│   │   │   │   ├── CartDAO.java
│   │   │   │   ├── CategoryDAO.java
│   │   │   │   ├── FeedbackDAO.java
│   │   │   │   ├── OrderDAO.java          <-- ACID multi-table checkout transaction
│   │   │   │   ├── PaymentDAO.java
│   │   │   │   ├── ProductDAO.java
│   │   │   │   ├── RatingDAO.java         <-- Verified purchaser rule enforcement
│   │   │   │   ├── ReportDAO.java
│   │   │   │   ├── TrackingDAO.java
│   │   │   │   └── UserDAO.java
│   │   │   ├── filter/                    <-- Security Filters
│   │   │   │   ├── AuthFilter.java        <-- URL & Role authorization guard
│   │   │   │   └── EncodingFilter.java    <-- UTF-8 encoding filter
│   │   │   ├── model/                     <-- JavaBean Entity POJOs (11 Models)
│   │   │   │   ├── Admin.java
│   │   │   │   ├── CartItem.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Feedback.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   ├── Payment.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Rating.java
│   │   │   │   ├── TrackingDetail.java
│   │   │   │   └── User.java
│   │   │   ├── servlet/                   <-- Jakarta HTTP Controllers
│   │   │   │   ├── AdminCategoryServlet.java
│   │   │   │   ├── AdminDashboardServlet.java
│   │   │   │   ├── AdminFeedbackServlet.java
│   │   │   │   ├── AdminLoginServlet.java
│   │   │   │   ├── AdminOrderServlet.java
│   │   │   │   ├── AdminProductServlet.java
│   │   │   │   ├── AdminReportServlet.java   <-- Tabular reports & CSV exporter
│   │   │   │   ├── CartServlet.java
│   │   │   │   ├── CheckoutServlet.java
│   │   │   │   ├── ContactServlet.java
│   │   │   │   ├── HomeServlet.java
│   │   │   │   ├── LoginServlet.java
│   │   │   │   ├── LogoutServlet.java
│   │   │   │   ├── OrderServlet.java
│   │   │   │   ├── ProductCatalogServlet.java
│   │   │   │   ├── ProductDetailServlet.java
│   │   │   │   ├── ProfileServlet.java
│   │   │   │   ├── RegisterServlet.java
│   │   │   │   └── ReviewServlet.java
│   │   │   └── util/                      <-- Utility Classes
│   │   │       ├── AppContextListener.java
│   │   │       ├── CSRFUtil.java
│   │   │       ├── DBConnectionUtil.java
│   │   │       ├── PasswordUtil.java
│   │   │       └── ValidationUtil.java
│   │   ├── resources/
│   │   │   └── db.properties              <-- MySQL credentials & pool parameters
│   │   └── webapp/
│   │       ├── index.jsp                  <-- Root welcome entry point
│   │       ├── error-404.jsp              <-- Custom 404 view
│   │       ├── error-500.jsp              <-- Custom 500 view
│   │       ├── assets/
│   │       │   ├── css/
│   │       │   │   ├── style.css          <-- Storefront responsive styling
│   │       │   │   └── admin.css          <-- Admin dashboard styling
│   │       │   └── js/
│   │       │       └── main.js            <-- AJAX add-to-cart, toast alerts, modals
│   │       ├── uploads/                   <-- Product images repository
│   │       └── WEB-INF/
│   │           ├── web.xml                <-- Deployment descriptor
│   │           └── views/
│   │               ├── admin/             <-- Admin dashboard & management JSPs
│   │               ├── common/            <-- Header and footer fragments
│   │               └── customer/          <-- Storefront browsing & checkout JSPs
│   └── test/java/com/shopping/            <-- Automated JUnit 5 tests
```

---

## 3. Step-by-Step Setup Instructions (Windows 10 / 11)

### Prerequisites
1. **Java Development Kit (JDK 17 LTS)** installed.
2. **Apache Tomcat 10.1+** downloaded.
3. **MySQL Server 8.0** installed and running.
4. **Apache Maven 3.9+** (or use Eclipse embedded Maven).

### Step 1: Database Setup
1. Open **MySQL Workbench** or MySQL CLI.
2. Execute the script in `database/schema.sql` to create `shopping_db` and all 11 tables.
3. Execute the script in `database/sample_data.sql` to insert categories, products, orders, and credentials.
4. Edit `src/main/resources/db.properties` with your local MySQL password:
   ```properties
   db.username=root
   db.password=YOUR_LOCAL_MYSQL_PASSWORD
   ```

### Step 2: Open and Run in Eclipse IDE
1. Open Eclipse IDE for Enterprise Java and Web Developers.
2. Select **File** $\rightarrow$ **Import...** $\rightarrow$ **Maven** $\rightarrow$ **Existing Maven Projects**.
3. Browse to the folder:
   `C:\Users\pc\.gemini\antigravity\scratch\online-shopping-system`
4. Click **Finish**. Eclipse will detect `pom.xml` and download dependencies.
5. In the **Servers** tab, click **Add New Server** $\rightarrow$ **Apache Tomcat v10.1 Server**.
6. Select your Tomcat 10 installation directory.
7. Right-click Tomcat in the Servers tab $\rightarrow$ **Add and Remove...** $\rightarrow$ Add `online-shopping-system` to the right pane.
8. Right-click the project $\rightarrow$ **Run As** $\rightarrow$ **Run on Server** (or start Tomcat).

### Step 3: Accessing the Application
* **Storefront URL:** `http://localhost:8080/online-shopping-system/`
* **Admin Login URL:** `http://localhost:8080/online-shopping-system/admin/login`

---

## 4. Default Login Credentials

| Role | Username / Email | Password | Access Privileges |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin` | `Admin@123` | Dashboard analytics, Product CRUD, Categories, Orders fulfillment, Courier logistics tracking, Feedbacks, Reports (CSV Export) |
| **Sample Customer 1** | `john.doe@example.com` | `Customer@123` | Catalog browsing, Cart, Checkout, Order history, Order tracking, Reviews |
| **Sample Customer 2** | `priya.sharma@example.com` | `Customer@123` | Active customer profile & orders |
| **Sample Customer 3** | `amit.kumar@example.com` | `Customer@123` | Active customer profile & orders |

---

## 5. Test Cases Matrix (Unit, Functional, Integration, Security)

| Test ID | Module | Scenario Tested | Execution Steps | Expected Result | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-SEC-01** | Security | BCrypt Hashing | Execute `HashGeneratorTest` | Plain-text `Admin@123` generates valid `$2a$12$` hash that verifies with `PasswordUtil.checkPassword` | **PASS** |
| **TC-SEC-02** | Security | SQL Injection Prevention | Pass `' OR '1'='1` to `UserDAO.getUserByEmail` | Query safely handles parameter via `PreparedStatement`; returns null without SQL injection | **PASS** |
| **TC-SEC-03** | Security | CSRF Protection | Submit checkout form with altered/missing `csrfToken` | `CSRFUtil.isValidToken` detects mismatch and blocks with HTTP 403 Forbidden | **PASS** |
| **TC-SEC-04** | Security | Role Authorization | Unauthenticated user navigates directly to `/admin/dashboard` | `AuthFilter` intercepts request and redirects to `/admin/login` | **PASS** |
| **TC-VAL-01** | Validation | Email & Phone Formatting | Run `ValidationAndDAOTest` | Validates RFC compliant emails and 10-15 digit phone formats | **PASS** |
| **TC-VAL-02** | Validation | File Upload Restraints | Test with `.exe` file and image > 2MB | `ValidationUtil.isValidImageFile` rejects non-image formats and files > 2MB | **PASS** |
| **TC-TXN-01** | Checkout | ACID Atomic Transaction | Call `placeOrderTransaction` with 2 items | Inserts order, batch inserts line items, reduces inventory in products, initializes courier tracking, purges cart under single transaction commit | **PASS** |
| **TC-TXN-02** | Checkout | Transaction Rollback on Out of Stock | Place order requesting more units than available stock | Throws `IllegalStateException`; triggers `conn.rollback()`; zero order records created; inventory unchanged | **PASS** |
| **TC-REV-01** | Reviews | Verified Buyer Restriction | Non-purchaser attempts to call `ReviewServlet` | `RatingDAO.hasUserPurchasedProduct` returns false; review submission is rejected | **PASS** |
| **TC-EXP-01** | Reports | CSV Data Export | Trigger `/admin/reports/export?type=sales_daily` | Streams RFC-4180 compliant CSV file with `Content-Disposition: attachment` | **PASS** |

---

## 6. Screenshots Checklist for Project Report (BCSP-064)

1. **Figure 1:** Database Schema (Entity-Relationship Diagram from MySQL Workbench)
2. **Figure 2:** Homepage with Featured Products and Category Grid
3. **Figure 3:** Product Catalog with Multi-Filter Sidebar (Category, Price Range, Sort)
4. **Figure 4:** Product Detail Page with Verified Buyer Star Ratings and In-Stock Status
5. **Figure 5:** Shopping Cart with Dynamic Quantity Controls and Subtotal Calculation
6. **Figure 6:** Checkout Page with Shipping Address and Payment Method Selection (COD/Online)
7. **Figure 7:** Order Confirmation Screen with Order ID, Payment Status, and Waybill Tracking Code
8. **Figure 8:** Customer "My Orders" History Screen
9. **Figure 9:** Order Tracking Timeline (5-Stage Visual Delivery Progress)
10. **Figure 10:** Admin Login Portal
11. **Figure 11:** Admin Dashboard Analytics with KPI Stat Cards and Recent Orders Table
12. **Figure 12:** Admin Product Management with Multipart Image Upload Modal
13. **Figure 13:** Admin Category Management with Duplicate Prevention
14. **Figure 14:** Admin Order Fulfilment & Courier Tracking Update Screen
15. **Figure 15:** Admin Reports Screen (Order, Daily/Monthly Sales, Low-Stock, Customer Report with CSV Export)
