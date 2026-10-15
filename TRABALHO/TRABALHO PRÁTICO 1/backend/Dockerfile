# Etapa 1: compila o projeto com o Maven Wrapper
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q dependency:go-offline
COPY src src
RUN ./mvnw -q package -DskipTests

# Etapa 2: imagem final, só com o Java e o .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/portfolio-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
