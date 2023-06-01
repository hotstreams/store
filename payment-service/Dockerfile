FROM maven:3.8.5-openjdk-17-slim AS build

RUN mkdir /app
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:resolve-plugins dependency:resolve

COPY . .
RUN mvn package -Dmaven.test.skip

FROM openjdk:17-alpine
COPY --from=build /app/target/payment-service-0.0.1-SNAPSHOT.jar /app/payment-service-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/payment-service-0.0.1-SNAPSHOT.jar"]
