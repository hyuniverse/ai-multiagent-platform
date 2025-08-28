# Stage 1: Build the application (변경 없음)
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :app:build -x test

# Stage 2: Create the final image
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/app/build/libs/*.jar app.jar
COPY entrypoint.sh .

RUN chmod +x entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["./entrypoint.sh"]