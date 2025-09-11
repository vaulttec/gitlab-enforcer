# Build stage
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

# Copy maven wrapper and pom.xml first for better layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copy source and .git directory for git-commit-id-plugin
COPY src ./src
COPY .git ./.git

# Build the application
RUN ./mvnw clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Add a non-root user to run the application
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the JAR file from the builder stage
COPY --from=builder /build/target/gitlab-enforcer.jar /app/app.jar

# Set ownership of the application files
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Configure JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Expose the application port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set the entry point
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]