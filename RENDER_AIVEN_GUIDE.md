# Render & Aiven Deployment Guide

This guide details how to deploy the SmartStudent Admin Portal using **Docker** on **Render** with a managed **Aiven MySQL** database.

## 1. Prerequisites
- A **Render** account ([render.com](https://render.com)).
- An **Aiven** account ([aiven.io](https://aiven.io)) with a MySQL service running.
- Latest code pushed to GitHub.

## 2. Docker Configuration
The project includes a multi-stage `Dockerfile` that handles the build and execution:
- **Build Stage**: Compiles the project using Maven 3.9 and Java 17.
- **Run Stage**: Executes the JAR in a lightweight JRE environment.

## 3. Render Deployment Steps
1. **Create a New Web Service**:
   - Choose your GitHub repository.
   - Select **Docker** as the Environment.
2. **Environment Variables**:
   Add the following variables in the Render dashboard:
   
| Key | Value (Example) | Description |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<host>:<port>/<db>?ssl-mode=REQUIRED` | Aiven MySQL Connection String |
| `SPRING_DATASOURCE_USERNAME` | `avnadmin` | Aiven Username |
| `SPRING_DATASOURCE_PASSWORD` | `<your-aiven-password>` | Aiven Password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Automatically creates tables on first run |
| `DB_ENCRYPTION_KEY` | `<32-byte-base64-key>` | Secure key for database encryption |
| `FILE_ENCRYPTION_KEY` | `<32-byte-base64-key>` | Secure key for document encryption |
| `JWT_SECRET` | `<your-secure-secret>` | Secret key for login tokens |

3. **Disk Storage (Optional but Recommended)**:
   - Since Docker containers are ephemeral, uploaded documents will be lost on restart unless you attach a **Render Disk**.
   - Mount a disk to `/app/uploads` to persist student documents.

## 4. Aiven MySQL Configuration
- Ensure your Aiven MySQL service is allowed to accept connections from the outside (check "Allow all IP addresses" or restrict to Render's IP range).
- Copy the **Service URI** from Aiven and format it as a JDBC URL for `SPRING_DATASOURCE_URL`.

## 5. Verification
Once Render finishes the build, your app will be live at `https://<your-app-name>.onrender.com`.

---
**Tip**: Check the Render logs for the message "Search index synchronization completed" to confirm the app has successfully connected to Aiven.
