FROM openjdk:17-alpine
LABEL authors="aksavadogo"

ENTRYPOINT ["top", "-b"]