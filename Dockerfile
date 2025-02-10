# Use an official OpenJDK 23 image as the base image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Define a build argument for the JAR file path (default to build/libs)
ARG JAR_PATH="build/libs/power-plant-0.0.1-SNAPSHOT.jar"

# Copy the JAR file from the specified path (default or custom)
COPY ${JAR_PATH} /app/power-plant.jar

# Expose the port the application will run on
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "/app/power-plant.jar"]
