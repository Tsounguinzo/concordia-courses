FROM openjdk:17-jdk-slim

EXPOSE 8080

# Install Python and pip
RUN apt-get update && \
    apt-get install -y python3 python3-pip && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set up PYTHON_EXECUTABLE environment variable
ENV PYTHON_EXECUTABLE=python3

# Set the working directory in the container
WORKDIR /app

# Copy the application's jar file and the Python scripts
COPY target/*.jar app.jar
COPY src/main/resources/rmpScraping/*.py /app/rmpScraping/
COPY src/main/resources/rmpScraping/*.json /app/rmpScraping/

# Run the jar file
CMD ["java", "-jar", "/app/app.jar"]