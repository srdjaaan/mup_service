# 1) Build stage – Maven + JDK 17
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Preuzmi zavisnosti (cache-friendly)
COPY pom-auth.xml pom.xml
RUN mvn -B -q dependency:go-offline

# Build
COPY . .
RUN mvn -B -q clean package -DskipTests -f pom-auth.xml

# 2) Run stage – manji runtime image (JRE 17)
# (alternativa: FROM eclipse-temurin:17-jdk)
FROM eclipse-temurin:17-jre
WORKDIR /app

# Kopiraj buildovani JAR
COPY --from=build /app/target/auth_service-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.main.web-application-type=servlet"]
