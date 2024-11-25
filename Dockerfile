# Etapa 1: Construcción
FROM maven:3.8.1-openjdk-17 AS compile
WORKDIR /usr/src/app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM openjdk:17-jdk-slim
WORKDIR /usr/app
COPY --from=compile /usr/src/app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
