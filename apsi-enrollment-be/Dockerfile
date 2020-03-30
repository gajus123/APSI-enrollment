FROM gradle:6.3-jdk11
WORKDIR /usr/src/app
COPY . .
RUN gradle wrapper
RUN ./gradlew build

FROM openjdk:11-jre-slim
COPY --from=0 /usr/src/app/build /usr/src/app
COPY --from=0 /usr/src/app/build/libs/*.jar /usr/src/app/libs/apsi-enrollment.jar

EXPOSE 8080
CMD [ "java", "-jar", "/usr/src/app/libs/apsi-enrollment.jar" ]
