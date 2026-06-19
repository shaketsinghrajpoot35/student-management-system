# 🎓 Secure Student Record & Document Management System

A full-stack, production-ready web application for securely managing student records, documents, attendance, and academic information — built with **Spring Boot 3** and a **Vanilla JS frontend**.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Setup](#local-setup)
  - [Environment Variables](#environment-variables)
- [API Reference](#-api-reference)
- [Security Architecture](#-security-architecture)
- [Docker Deployment](#-docker-deployment)
- [Default Credentials](#-default-credentials)
- [Contributing](#-contributing)

---

## 🌟 Overview

EduTrack is a secure, role-based student management platform designed for schools and educational institutions. It provides admins with tools to register students, manage documents, track attendance, generate reports, and control academic records — all protected by JWT authentication and AES-256 field-level encryption for sensitive data.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔐 **JWT Authentication** | Stateless token-based login with 24-hour expiry |
| 🏫 **Multi-School Support** | Each admin manages only their own school's data |
| 👤 **Student Management** | Full CRUD with photo upload, multi-field search & pagination |
| 📄 **Document Management** | Encrypted file storage (PDF, JPG, PNG) up to 10MB per file |
| 📊 **Attendance Tracking** | Subject-wise attendance marking and history |
| 📚 **Academic Details** | Class, stream, roll number, admission number management |
| 🏦 **Bank Details** | Encrypted storage of student banking information |
| 📈 **Reports** | PDF & CSV export of student records |
| 📧 **OTP Email** | Password reset via OTP email through Brevo (Sendinblue) |
| 🔒 **AES-256 Encryption** | Field-level encryption for Samagra ID and sensitive fields |
| 🛡️ **Admin Approval Flow** | Super-admin must approve new admin registrations |

---

## 🛠 Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 3.4.5 | Core framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | ORM & database access |
| JJWT | 0.12.6 | JWT token generation & validation |
| MySQL | 8.x | Relational database |
| Lombok | Latest | Boilerplate reduction |
| OpenPDF | 2.0.3 | PDF report generation |
| Apache Commons CSV | 1.11.0 | CSV export |
| Brevo (HTTP API) | v3 | Transactional email / OTP |
| Spring Actuator | 3.x | Health checks |

### Frontend
| Technology | Purpose |
|---|---|
| HTML5 | Structure |
| Vanilla CSS | Styling |
| Vanilla JavaScript (ES6+) | Logic & API communication |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker | Containerization (multi-stage build) |
| Railway / Render | Cloud deployment |
| Maven | Build tool |

---

## 📁 Project Structure

```
SecureStudentRecordAndDocumentManagementSystem/
├── frontend/                        # Vanilla JS frontend
│   ├── index.html                   # Entry point
│   ├── app.js                       # Main application logic
│   ├── pages.js                     # Page components
│   ├── api.js                       # API client (axios-like)
│   └── style.css                    # Styles
│
├── src/main/java/com/smartstudent/main/
│   ├── config/                      # Security, CORS, bean configs
│   ├── controller/                  # REST API controllers
│   │   ├── AuthController.java      # Login, register, OTP
│   │   ├── StudentController.java   # Student CRUD
│   │   ├── StudentDocumentController.java
│   │   ├── AttendanceController.java
│   │   ├── AcademicDetailsController.java
│   │   ├── BankDetailsController.java
│   │   ├── SubjectController.java
│   │   ├── StaffController.java
│   │   ├── StudentReportController.java
│   │   └── DashboardController.java
│   ├── entity/                      # JPA entities
│   ├── dto/                         # Request & response DTOs
│   ├── repository/                  # Spring Data repositories
│   ├── service/                     # Business logic
│   ├── security/                    # JWT filter, UserDetails
│   ├── util/                        # Encryption, hashing utilities
│   ├── enums/                       # Gender, Category, Stream, etc.
│   ├── exception/                   # Global exception handling
│   └── mapper/                      # Entity ↔ DTO mappers
│
├── src/main/resources/
│   └── application.properties       # App config (env var driven)
│
├── Dockerfile                       # Multi-stage Docker build
├── railway.json                     # Railway deployment config
├── run.bat                          # Windows local run script
└── pom.xml                          # Maven dependencies
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+**
- **Maven 3.9+**
- **MySQL 8.x** running locally
- **IntelliJ IDEA** (recommended) or any Java IDE

### Local Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/shaketsinghrajpoot35/student-management-system.git
   cd student-management-system
   ```

2. **Create the MySQL database**
   ```sql
   CREATE DATABASE student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Set environment variables** (see below)

4. **Run the application**
   ```bash
   # Using Maven Wrapper
   ./mvnw spring-boot:run

   # Or on Windows
   run.bat
   ```

5. **Access the app**
   - Backend API: `http://localhost:8081`
   - Frontend: Open `frontend/index.html` in your browser or serve it via any static server

---

### Environment Variables

All sensitive configuration is externalized via environment variables. **Never hardcode secrets.**

#### IntelliJ IDEA Setup
Go to **Run → Edit Configurations → Environment Variables** and paste:

```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/student_management;SPRING_DATASOURCE_USERNAME=root;SPRING_DATASOURCE_PASSWORD=your_password;SPRING_JPA_HIBERNATE_DDL_AUTO=update;PORT=8081;JWT_SECRET=your_jwt_secret_min_32_chars;DB_ENCRYPTION_KEY=your_base64_32byte_key=;FILE_ENCRYPTION_KEY=your_base64_32byte_key=;MAIL_FROM=your@email.com;MAIL_USERNAME=your_brevo_smtp_user;MAIL_PASSWORD=your_brevo_api_key
```

#### Full Variable Reference

| Variable | Description | Example |
|---|---|---|
| `SPRING_DATASOURCE_URL` | MySQL JDBC connection URL | `jdbc:mysql://localhost:3306/student_management` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `your_password` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Schema strategy (`update` / `none`) | `update` |
| `PORT` | Server port | `8081` |
| `JWT_SECRET` | JWT signing secret (min 32 chars) | `MySecretKey...` |
| `DB_ENCRYPTION_KEY` | AES-256 key for DB fields (Base64, 32 bytes) | `vS2N4vB9...=` |
| `FILE_ENCRYPTION_KEY` | AES-256 key for file encryption (Base64, 32 bytes) | `zR5vT2yX...=` |
| `MAIL_FROM` | Sender email address | `noreply@yourdomain.com` |
| `MAIL_USERNAME` | Brevo SMTP username | `your@smtp-brevo.com` |
| `MAIL_PASSWORD` | Brevo API key | `xkeysib-...` |

> 💡 **Generating a 32-byte Base64 key** (for encryption keys):
> ```bash
> # Linux/Mac
> openssl rand -base64 32
> # Windows PowerShell
> [Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Max 256 }))
> ```

---

## 📡 API Reference

All endpoints are prefixed with `/api`. Protected endpoints require `Authorization: Bearer <token>` header.

### Authentication — `/api/auth`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/auth/login` | ❌ Public | Login with username & password |
| `POST` | `/api/auth/register` | ❌ Public | Register a new admin (pending approval) |
| `GET` | `/api/auth/me` | ✅ Required | Get current admin profile |
| `POST` | `/api/auth/forgot-password` | ❌ Public | Send OTP to registered email |
| `POST` | `/api/auth/verify-otp` | ❌ Public | Verify the OTP |
| `POST` | `/api/auth/reset-password` | ❌ Public | Reset password after OTP verification |

### Students — `/api/students`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/students/register` | Register new student (multipart: JSON + files) |
| `GET` | `/api/students` | Search & paginate students |
| `GET` | `/api/students/{id}` | Get student by ID |
| `GET` | `/api/students/{id}/full-details` | Get complete student profile |
| `PUT` | `/api/students/{id}` | Update student (multipart: JSON + files) |
| `DELETE` | `/api/students/{id}` | Delete student (ADMIN role only) |

**Search Parameters:** `name`, `samagraId`, `className`, `rollNumber`, `admNo`, `stream`, `page`, `size`, `sortBy`, `sortDir`

### Documents — `/api/students/{id}/documents`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/students/{id}/documents` | Upload document(s) |
| `GET` | `/api/students/{id}/documents` | List all documents |
| `GET` | `/api/students/{id}/documents/{docId}/download` | Download encrypted document |
| `DELETE` | `/api/students/{id}/documents/{docId}` | Delete a document |

### Attendance — `/api/attendance`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/attendance` | Mark attendance |
| `GET` | `/api/attendance/student/{id}` | Get attendance by student |

### Reports — `/api/students/reports`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/students/reports/pdf` | Export all students as PDF |
| `GET` | `/api/students/reports/csv` | Export all students as CSV |

### Other Endpoints

| Prefix | Description |
|---|---|
| `/api/subjects` | Subject CRUD |
| `/api/staff` | Staff management |
| `/api/dashboard` | Dashboard statistics |

---

## 🔒 Security Architecture

```
Request → JWT Filter → Spring Security → Controller
                ↓
         Token Validation
         (HMAC-SHA256)
                ↓
         Admin Loaded from DB
         (per-request, scoped to school)
```

- **Authentication**: JWT tokens (HMAC-SHA256), 24-hour expiry
- **Authorization**: Role-based (`SUPER_ADMIN`, `ADMIN`)
- **Data Isolation**: Each admin only sees their own school's students
- **Field Encryption**: Samagra ID and bank details encrypted with AES-256-GCM
- **File Encryption**: Uploaded files stored encrypted on disk
- **Password Hashing**: BCrypt with Spring Security's `PasswordEncoder`
- **OTP**: 6-digit, 10-minute expiry, single-use

---

## 🐳 Docker Deployment

```bash
# Build the image
docker build -t edutrack-app .

# Run with environment variables
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/student_management \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  -e DB_ENCRYPTION_KEY=your_db_key \
  -e FILE_ENCRYPTION_KEY=your_file_key \
  -e MAIL_FROM=noreply@yourdomain.com \
  -e MAIL_USERNAME=your_brevo_smtp \
  -e MAIL_PASSWORD=your_brevo_key \
  -e PORT=8081 \
  edutrack-app
```

> The Docker image is optimized for **512MB RAM** environments (Railway free tier):
> - `-Xmx160m` heap, `-XX:MaxMetaspaceSize=160m`
> - Serial GC for low memory footprint
> - JVM TieredStopAtLevel=1 for faster startup

---

## 🔑 Default Credentials

On first run, a default **super admin** account is seeded automatically:

| Field | Value |
|---|---|
| Username | `admin` |
| Password | `admin@123` |
| Email | `admin@smartstudent.com` |

> ⚠️ **Change the default password immediately after first login in production!**

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'feat: add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project is for educational and personal use. All rights reserved © 2024 EduTrack / SmartStudent.

---

<div align="center">
  <p>Built with ❤️ using Spring Boot 3 & Java 17</p>
  <p>
    <a href="https://github.com/shaketsinghrajpoot35/student-management-system">GitHub</a> •
    <a href="#-api-reference">API Docs</a> •
    <a href="#-getting-started">Getting Started</a>
  </p>
</div>
