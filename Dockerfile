# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Environment variables with defaults (Railway/Render will override these)
ENV SERVER_PORT=${PORT:-8080}
ENV SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/student_management
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=radhe$108
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=none

# Expose the dynamic port
EXPOSE ${PORT:-8080}

# Start application with EXTREME aggressive memory optimizations
# -XX:TieredStopAtLevel=1 is the secret for running Java in small containers
ENTRYPOINT ["sh", "-c", "java -Xmx172m -Xms128m -XX:TieredStopAtLevel=1 -XX:+UseSerialGC -XX:MaxMetaspaceSize=80m -jar app.jar --server.port=${PORT:-8080}"]
