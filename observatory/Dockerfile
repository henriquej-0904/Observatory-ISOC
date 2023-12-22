FROM openjdk:19-slim

# COPY observatory
WORKDIR /observatory
COPY ./target/observatory-jar-with-dependencies.jar /observatory/observatory.jar

ENTRYPOINT [ "java", "-jar", "/observatory/observatory.jar" ]
