FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
