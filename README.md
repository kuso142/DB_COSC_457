# Food Delivery Database System
### COSC 457 — Database Management Systems

A multi-vendor food delivery platform database system built for a single city. The system connects customers, independent food businesses, and delivery personnel through a centralized database, with a Java Swing GUI for interacting with the data.

---
## Project Structure
```
DB_COSC_457/
├── src/                        # Java source files (Swing GUI + DB logic)
├── food_delivery.sql           # Main database schema
├── restuarant_sampledata.sql   # Sample/seed data
├── restuarantoperations.sql    # Database operations and queries
└── pom.xml                     # Maven build configuration
```
## Tech Stack

- **Java** — Swing GUI frontend
- **SQL** — Database schema, queries, and operations
- **Maven** — Build and dependency management
- **JDBC** — Java-to-database connectivity

---

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven
- MySQL

### Database Setup
Open your SQL client (MySQL Workbench, DBeaver, etc.) and run:
```sql
source food_delivery.sql;
source restuarant_sampledata.sql;
source restuarantoperations.sql;
```

### Running the Application
```bash
git clone https://github.com/kuso142/DB_COSC_457.git
cd DB_COSC_457
mvn clean install
mvn exec:java
```

---

## System Scope

**In scope:**
- Single city operation
- Customer, restaurant, and delivery personnel management
- Order placement and tracking
- Menu and inventory management

**Out of scope:** real-time GPS tracking, payment gateway integration, AI/recommendation features.

