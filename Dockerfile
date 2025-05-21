FROM eclipse-temurin:23-jdk
VOLUME /tmp

ARG JAR_FILE=build/libs/mobileapp-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app.jar"]