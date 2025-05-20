FROM eclipse-temurin:23-jdk
LABEL authors="v_yamancheva"

WORKDIR /app

COPY build/libs/task-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
