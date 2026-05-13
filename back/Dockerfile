FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/back-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 6969

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
