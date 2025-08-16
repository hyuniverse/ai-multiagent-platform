# Stage 1: Build the application
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :app:build -x test

# Stage 2: Create the final image
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]