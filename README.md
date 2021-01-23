# Social Network #
A RESTful API service for a social network application. The service allow users to:
- sign up to the social network.
- verify their email.
- update their profile (first name and last name).
- update and delete their profile picture.
- verify that their profile picture is a valid selfie.
- update their password.
- create and retrieve posts.
- search other users using first name, last name, or email.

## Requirements ##
For building and running the application, you need:
- Java 1.8
- Maven 3
- Docker


## Running the Application ##
To build and run the application locally:
- Build the application using Maven:
	```console
	mvn clean package
	```
- Set the following environment variables:
    - DATASOURCE_URL
    - DATASOURCE_USER
    - DATASOURCE_PASSWORD
    - MAIL_SMTP_USER
    - MAIL_SMTP_PASSWORD
    - GOOGLE_APPLICATION_CREDENTIALS 
- Build and run the Docker image:
	```console
	docker build -t socialnetwork .
	docker run -p 8080:8080 socialnetwork
	```
- Navigate to the API contract page to explore the available endpoints: http://localhost:8080/api/swagger-ui.html

## Copyright ##
Shaimaa Sabry. 2021.

