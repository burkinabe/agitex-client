FROM openjdk:17-alpine
LABEL authors="aksavadogo"

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]