# Use an official JDK image to build and run the app
FROM gradle:8.4.0-jdk17 AS build

WORKDIR /app

# Copy build files first (for better caching)
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle/ gradle/

# Download dependencies (this layer will be cached unless build files change)
RUN gradle dependencies --no-daemon

# Copy source code (this layer will be rebuilt when source changes)
COPY src/ src/

# Build the application
RUN gradle build --no-daemon

# Use a smaller JRE image for running
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/build/libs/ExpenseTracker-all.jar /app/app.jar

# Copy application.conf into the final image
COPY src/main/resources/application.conf /app/config/application.conf

# Expose the port your Ktor app runs on (matches application.conf)
EXPOSE 8081

# Set environment variables for configuration (can be overridden)
ENV DB_JDBC_URL=jdbc:postgresql://db:5432/expensetracker
ENV DB_USER=postgres
ENV DB_PASSWORD=password
ENV JWT_SECRET=dev-secret
ENV JWT_ISSUER=expensetracker
ENV JWT_AUDIENCE=expensetracker-users
ENV JWT_REALM=expensetracker-app
ENV INITIALIZE_TEST_DATA=false

# Run the application with config file
CMD ["java", "-Dktor.config=/app/config/application.conf", "-jar", "/app/app.jar"] 