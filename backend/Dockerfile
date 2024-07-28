# Start with a base image containing Java runtime
FROM openjdk:17-jdk-slim as build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven/Gradle files and the source code
COPY pom.xml /app/
COPY src /app/src

# Build the application
RUN mvn clean package -DskipTests

# Production stage
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Install Python and pip
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set up PYTHON_EXECUTABLE environment variable
ENV PYTHON_EXECUTABLE=python3

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Copy the Python scripts and other necessary files
COPY src/main/resources/rmpScraping/*.py /app/rmpScraping/
COPY src/main/resources/rmpScraping/*.json /app/rmpScraping/

# Expose port 8080
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/app.jar"]
