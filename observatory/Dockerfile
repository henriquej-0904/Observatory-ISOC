FROM maven:3-openjdk-11 AS build

WORKDIR /observatory

COPY pom.xml /observatory/pom.xml
COPY src/ /observatory/src/

# Update and install
RUN apt update && \
apt full-upgrade -y && \
rm -r /var/lib/apt/lists/* && \
mvn compile assembly:single

FROM openjdk:11-jre-slim

# COPY observatory
WORKDIR /observatory
COPY --from=build /observatory/target/observatory-1.0-jar-with-dependencies.jar /observatory/observatory.jar

ENTRYPOINT [ "java", "-jar", "/observatory/observatory.jar" ]