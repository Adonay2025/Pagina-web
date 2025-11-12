# Etapa 1: construir el WAR con Maven
FROM maven:3.8.7-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# Etapa 2: desplegar en Tomcat (imagen alternativa confiable)
FROM tomcat:9.0.82-jdk8-temurin
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/calculadora.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
