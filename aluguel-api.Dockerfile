# Dockerfile do Render para a API do TP2 (fica na raiz porque o Render não aceita espaço/acento no Root Directory)
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY ["TRABALHO/TRABALHO PRÁTICO 2/backend/", "./"]
RUN chmod +x mvnw && ./mvnw -q -B package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/aluguel-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
