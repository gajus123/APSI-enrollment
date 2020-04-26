FROM node:13

WORKDIR /usr/src/app

COPY package.json .
RUN npm install
RUN npm install -g @angular/cli

COPY . .
RUN ng build --prod

FROM nginx

COPY --from=0 /usr/src/app/dist/apsi-enrollment-fe /usr/share/nginx/html
EXPOSE 80
