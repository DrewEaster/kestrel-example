FROM openjdk:11-slim
EXPOSE 8080
CMD ./gradlew run --continuous