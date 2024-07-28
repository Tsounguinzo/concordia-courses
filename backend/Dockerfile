#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /home/app/pom.xml clean package


#
# Package stage
#
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Install Python
RUN apt-get update && \
    apt-get install -y python3 && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*


COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]