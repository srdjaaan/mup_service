# 1) Build stage – Maven + JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Preuzmi zavisnosti (cache-friendly)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Build
COPY . .
RUN mvn -B -q clean package -DskipTests

# 2) Run stage – manji runtime image (JRE 17)
# (alternativa: FROM eclipse-temurin:17-jdk)
FROM eclipse-temurin:17-jre
WORKDIR /app

# Kopiraj buildovani JAR
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
