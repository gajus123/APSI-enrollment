FROM node:13

WORKDIR /usr/src/app

COPY package.json .
RUN npm install
RUN npm install -g @angular/cli

COPY . .
RUN ng build --prod

FROM httpd:alpine

COPY --from=0 /usr/src/app/dist/apsi-enrollment-fe /usr/local/apache2/htdocs/
EXPOSE 80
