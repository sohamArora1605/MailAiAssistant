# Use lightweight Java image
FROM eclipse-temurin:17-jdk

# Set working directory in container
WORKDIR /app

# Copy the JAR file
COPY target/emailwritersb-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional but good for info)
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
