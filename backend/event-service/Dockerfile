FROM eclipse-temurin:23-jdk
LABEL authors="v_yamancheva"
WORKDIR /app
COPY impl/build/libs/event-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
