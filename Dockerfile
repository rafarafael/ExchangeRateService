# Use uma imagem Java como base
FROM openjdk:17-jdk-slim
# Configura o diretório de trabalho no container
WORKDIR /app
# Copia o jar da aplicação para o diretório de trabalho
COPY target/exchange-rate-service-0.0.1-SNAPSHOT.jar app.jar
# Expõe a porta do Spring Boot (por exemplo, 8080)
EXPOSE 8080
# Executa o aplicativo
ENTRYPOINT ["java", "-jar", "app.jar"]
