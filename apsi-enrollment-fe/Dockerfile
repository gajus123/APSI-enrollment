FROM node:13

WORKDIR /usr/src/app

COPY package.json .
RUN npm install
RUN npm install -g @angular/cli

COPY . .
RUN ng build --prod

WORKDIR /usr/src/app/apsi-enrollment-fe/dist/apsi-enrollment-fe
EXPOSE 4200
CMD [ "ng", "serve", "--host", "0.0.0.0" ]
