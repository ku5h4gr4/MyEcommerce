# 1. Build stage: build with Maven
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Package / Runtime stage: run the JAR
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
