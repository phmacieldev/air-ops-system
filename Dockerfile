# ── Build ──────────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies before copying source
COPY pom.xml .
RUN mvn dependency:go-offline -q

COPY src src
RUN mvn package -DskipTests -q

# ── Runtime ────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
