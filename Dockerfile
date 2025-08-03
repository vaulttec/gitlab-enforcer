FROM eclipse-temurin:11-jre-focal

WORKDIR /app

# Add a non-root user to run the application
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the JAR file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Set ownership of the application files
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Configure JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Expose the application port
EXPOSE 8080

# Set the entry point
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]