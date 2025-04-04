FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/item-service-0.0.1-SNAPSHOT.jar item-service.jar
ENTRYPOINT ["java", "-jar", "item-service.jar"]
