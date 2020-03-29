FROM gradle:6.3-jdk11
WORKDIR /usr/src/app
COPY . .
RUN gradle wrapper
RUN ./gradlew build

FROM openjdk:11-jre-slim
COPY --from=0 /usr/src/app/build/libs/apsi-enrollment-be-0.0.1-SNAPSHOT.jar /usr/src/app/apsi-enrollment-be-0.0.1-SNAPSHOT.jar

EXPOSE 8080
CMD [ "java", "-jar", "/usr/src/app/apsi-enrollment-be-0.0.1-SNAPSHOT.jar" ]
