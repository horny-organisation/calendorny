FROM eclipse-temurin:23-jdk
LABEL authors="v_yamancheva"
WORKDIR /app
COPY build/libs/event-service-0.0.1-SNAPSHOT-plain.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
