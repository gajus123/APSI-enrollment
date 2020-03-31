## How to run on local environment
To run the app locally you only need to execute ```gradle bootRun``` command or 
run it via your IDE. Application will create an instance of H2 database with all
changelogs in files in  ```~/.apsi``` directory. Credentials can be found in application.properties file.
Application will start on port 8080. At ```http://localhost:8080/swagger-ui.html``` 
you can find interactive API documentation.

If you encounter any troubles with the DB you can delete the files - application will 
create new files at the moment of the next start.

## How to run on production environment
Production run will require to set the following env. variables: DB_URL, DB_USERNAME, 
DB_PASSWORD, JWT_SECRET. Those should contain data of PostgreSQL DB prepared for the application
and secret key for JWT generation and validation. 

Changelogs will be loaded automatically after the start. 

Application can be started by command ```gradle bootRun -Dspring.profiles.active=prod```

 
