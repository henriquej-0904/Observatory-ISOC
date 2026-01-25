FROM eclipse-temurin:17-jre

# COPY observatory
WORKDIR /observatory
COPY ./target/observatory-jar-with-dependencies.jar /observatory/observatory.jar

ENTRYPOINT [ "java", "-jar", "/observatory/observatory.jar" ]
