# Conocrdia.courses



Welcome to the Course Review Platform, a comprehensive solution designed to empower students to share and discover reviews about courses they've attended. This platform is built using Svelte for the frontend, providing a modern and reactive user interface, and Spring Boot for the backend, ensuring a robust and scalable API.

## Table of Contents

<img src="https://github.com/Tsounguinzo/concordia-courses/blob/main/ui/static/logo.png" align="right"
alt="concordia.courses logo">

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture Overview](#architecture-overview)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
    - [Running the Application](#running-the-application)
- [Development](#development)
    - [Backend Development](#backend-development)
    - [Frontend Development](#frontend-development)
- [Hosting](#hosting)
    - [Frontend Hosting on Vercel](#frontend-hosting-on-vercel)
    - [MongoDB Hosting on MongoDB Atlas](#mongodb-hosting-on-mongodb-atlas)
    - [Spring Boot Backend Hosting on a VPS](#spring-boot-backend-hosting-on-a-vps)
    - [Redis Hosting on Redis Cloud](#redis-hosting-on-redis-cloud)
- [Deployment Process](#deployment-process)
- [Contributing](#contributing)
- [Contact](#contact)
- [Mentions](#mentions)

## Features

- **Course Reviews:** Users can post reviews for courses they've attended, providing ratings and experience.
- **Search and Filter:** Easily search for courses or filter them based on reviews, difficulty level, and more.
- **Authentication and Authorization:** Secure user registration and login functionality.
- **Responsive Design:** A user-friendly interface that adapts to different screen sizes and devices.
- **Caching and Performance Optimization:** Utilizes Redis for caching frequently accessed data, reducing database load and improving response times.

## Technology Stack

- **Frontend:** SvelteKit
- **Backend:** Spring Boot
- **Database:** MongoDB
- **Caching and Session Management:** Redis
- **Authentication:** JWT for secure token-based authentication

## Architecture Overview

The platform adopts a microservices architecture for scalability and maintainability:

- **Frontend Service:** Built with Svelte, it communicates with the backend via RESTful APIs.
- **Backend Service:** Developed with Spring Boot, handles business logic, data processing, and server-side operations.
- **Database:** MongoDB is used for storing course information, user profiles, reviews, and more.
- **Redis:** Employed for caching and storing blacklisted JWT tokens for logout functionality.

## Getting Started

### Prerequisites

- Node.js and npm
- Java JDK 17+ and Maven
- Docker

### Installation

1. **Clone the repository**

```bash
git clone https://github.com/Tsounguinzo/concordia-courses.git
```
2. **Start MongoDB and Redis using Docker Compose**

The repository includes a docker-compose.yml file that defines the MongoDB and Redis services. Use Docker Compose to start these services:
```bash
docker-compose up -d
```

3. **Backend Setup**

Navigate to the backend directory. Before running the application, ensure it's configured to connect to the MongoDB and Redis instances started by Docker Compose. Then, run the Spring Boot application in the dev profile to initialize the database with any required seed data:
```bash
cd backend
./mvnw clean install
```

4. **Frontend Setup**

Install the necessary npm packages and start the Svelte development server:
```bash
cd ../ui
npm install
npm run build
```

### Running the Application

- **Start the Backend Service**
```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- **Serve the Frontend**
```bash
cd ../ui
npm run dev
```
The frontend application will be accessible at http://localhost:5173, and it will communicate with the backend API for operations.

### Development

#### Backend Development

When developing locally, use the dev profile for backend development to take advantage of any development-specific configurations, such as detailed logging and database seeding.

#### Frontend Development

For frontend development, the Svelte development server provides hot reloading to immediately reflect changes in the browser.

### Hosting

concordia.courses is hosted across various cloud services to leverage the strengths of each and ensure optimal performance and reliability. Here's how each component is hosted:

#### Frontend Hosting on Vercel

The Svelte-based frontend is hosted on [Vercel](https://vercel.com), which provides a seamless deployment process and excellent support for Svelte applications. Vercel offers automatic deployments from Git, enabling easy updates and rollbacks. Access the frontend at your Vercel project URL.

#### MongoDB Hosting on MongoDB Atlas

The MongoDB database is hosted on an M0 tier AWS cluster via [MongoDB Atlas](https://www.mongodb.com/cloud/atlas). This fully managed cloud database is configured for high availability and scalability. The M0 tier on AWS offers a free solution with sufficient resources for development and initial production use. Ensure your Spring Boot backend is configured with the connection string provided by MongoDB Atlas.

#### Spring Boot Backend Hosting on a VPS

The Spring Boot backend application is hosted on [Railway](https://railway.app). This setup provides the flexibility needed for server-side operations and allows for custom configurations as required. The server runs the Spring Boot JAR file, with environment variables configured for database and Redis connections.

#### Redis Hosting on Redis Cloud

Session management and caching are powered by Redis, hosted on [Redis Cloud](https://redislabs.com/redis-enterprise-cloud/overview/) via the Redis Enterprise Cloud Essentials plan. This setup offers a managed Redis instance with automatic scaling, backups, and end-to-end security. The Redis Cloud instance is configured as a cache and for storing blacklisted tokens, with connection details provided to the Spring Boot backend.

### Deployment Process

1. **Frontend Deployment to Vercel:**
- Push the latest changes to your GitHub repository connected to Vercel for automatic deployment.

2. **MongoDB Atlas Configuration:**
- Ensure your MongoDB Atlas cluster is up and running. Configure IP whitelisting and retrieve your connection string for the Spring Boot application.

3. **Spring Boot Application Deployment:**
- Push the latest changes to your GitHub repository connected to Railway for automatic deployment.
- Set environment variables or use an `application.yml` file for production configurations, including MongoDB and Redis connection strings.

4. **Redis Cloud Setup:**
- Create a new Redis database in Redis Cloud and configure your Spring Boot application to connect to this instance for caching and token management.

### Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are *greatly appreciated*.

### Contact
Beaudelaire @ Beaudelaire@tutamail.com

### Mentions
mcgill.courses have led to the inspiration of concordia.courses with regard to its functionality and design.

