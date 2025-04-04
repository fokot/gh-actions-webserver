FROM openjdk:21-jdk-slim

WORKDIR /app

COPY /target/universal/stage /app

ENTRYPOINT ["bin/gh-actions-webserver"]
