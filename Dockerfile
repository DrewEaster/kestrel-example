FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim
EXPOSE 8080
CMD ./gradlew run --continuous