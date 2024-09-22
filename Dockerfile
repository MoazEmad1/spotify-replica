FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/spotify-0.0.1-SNAPSHOT.jar /app/spotify.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/spotify.jar"]
