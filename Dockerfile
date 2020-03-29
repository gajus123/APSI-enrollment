FROM node:13 AS build_fe

WORKDIR /usr/src/app
COPY apsi-enrollment-fe/package.json apsi-enrollment-fe/
WORKDIR /usr/src/app/apsi-enrollment-fe
RUN npm install
RUN npm install -g @angular/cli

COPY apsi-enrollment-fe .
RUN ng build --prod


FROM gradle:6.3-jdk11 AS build_be

WORKDIR /usr/src/app
COPY apsi-enrollment-be .

COPY --from=build_fe /usr/src/app/apsi-enrollment-fe/dist/apsi-enrollment-fe /usr/src/app/src/main/resources/static

RUN gradle wrapper
RUN ./gradlew build

FROM openjdk:11-jre-slim
COPY --from=build_be /usr/src/app/build /usr/src/app

EXPOSE 8080
CMD [ "java", "-jar", "/usr/src/app/libs/apsi-enrollment-be-0.0.1-SNAPSHOT.jar" ]
