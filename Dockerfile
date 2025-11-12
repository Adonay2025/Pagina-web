# Etapa 1: construir el WAR con Maven
FROM maven:3.8.7-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# Etapa 2: desplegar en Tomcat usando imagen estable
FROM bitnami/tomcat:10.1.16-debian-11-r0
COPY --from=build /app/target/calculadora.war /opt/bitnami/tomcat/webapps/ROOT.war
EXPOSE 8080
