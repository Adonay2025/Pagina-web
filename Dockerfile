# Etapa 1: compilar tu proyecto con Maven
FROM maven:3.8.7-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests clean package

# Etapa 2: instalar Tomcat manualmente y desplegar el WAR
FROM openjdk:8-jdk-alpine
RUN apk add --no-cache curl unzip

ENV TOMCAT_VERSION=9.0.82
RUN curl -O https://downloads.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.zip \
    && unzip apache-tomcat-${TOMCAT_VERSION}.zip \
    && mv apache-tomcat-${TOMCAT_VERSION} /tomcat \
    && rm apache-tomcat-${TOMCAT_VERSION}.zip

COPY --from=build /app/target/calculadora.war /tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["/tomcat/bin/catalina.sh", "run"]
