FROM maven:3.8.6-eclipse-temurin-18 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests


FROM eclipse-temurin:18-jre
WORKDIR /app

COPY --from=build /app/target/my-app-name-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar","app.jar"]