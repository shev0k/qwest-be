# Build stage
FROM openjdk:17-jdk-slim AS build
WORKDIR /workspace/app
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src
RUN ./gradlew bootJar

# Package stage
FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=build /workspace/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
