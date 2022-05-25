FROM eclipse-temurin:17-jre

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /

ENTRYPOINT ["java", "-jar", "/app.jar"]
