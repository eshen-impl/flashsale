FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/account-service-0.0.1-SNAPSHOT.jar account-service.jar
ENTRYPOINT ["java", "-jar", "account-service.jar"]
