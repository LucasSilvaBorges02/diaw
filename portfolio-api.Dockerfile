# Dockerfile usado pelo Render para a API do Trabalho Prático 1.
# Fica na raiz porque o Render não aceita espaço/acento no "Root Directory"
# ("TRABALHO PRÁTICO 1"); o build copia a pasta do back-end daqui.

# Etapa 1: compila o projeto com o Maven Wrapper
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY ["TRABALHO/TRABALHO PRÁTICO 1/backend/", "./"]
RUN chmod +x mvnw && ./mvnw -q package -DskipTests

# Etapa 2: imagem final, só com o Java e o .jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/portfolio-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
