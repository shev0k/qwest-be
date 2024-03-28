FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/qwest-be-0.0.1-SNAPSHOT.jar /app/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "qwest-be-0.0.1-SNAPSHOT.jar"]