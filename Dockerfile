# ================================
# FLOWER MANAGER API - Dockerfile
# Multi-stage build for production
# ================================

# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for layer caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Fix line endings for Windows/Linux compatibility
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies (cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="FlowerCorner Team"
LABEL description="Flower Manager API - Spring Boot Application"

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Create uploads directory
RUN mkdir -p /app/uploads && chown -R spring:spring /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Health check - comment out if not using actuator
# HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
#     CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application with production profile
# Set default profile to 'prod' - can be overridden by SPRING_PROFILES_ACTIVE env var
ENV SPRING_PROFILES_ACTIVE=prod

# Java options for cloud deployment
ENV JAVA_OPTS="-Xmx384m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} -jar app.jar"]

