FROM openjdk:17-alpine

EXPOSE 5555

ADD target/CloudStor-0.0.1-SNAPSHOT.jar diploma.jar

ENTRYPOINT ["java", "-jar", "diploma.jar"]