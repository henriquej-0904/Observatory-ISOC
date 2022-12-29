FROM openjdk:17-slim

# COPY observatory
WORKDIR /observatory
COPY ./target/observatory-v1.3.1-jar-with-dependencies.jar /observatory/observatory.jar

ENTRYPOINT [ "java", "-jar", "/observatory/observatory.jar" ]
