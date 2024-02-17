# Spring Boot OAuth2 Strava Example

## Used libraries

As well as spring-security and oauth libraries, this repository also use spring jdbc to store the sessions.
This app uses HTMX and Thymeleaf to generate a simple template, but the authentication part works with everything.

## Setup

This app need the following properties to work
```properties
spring.security.oauth2.client.registration.strava.client-id=
spring.security.oauth2.client.registration.strava.client-secret=
```
You can find them at https://www.strava.com/settings/api

You need a postgresql running to save the sessions/clientRegistration

You can connect to it by setting the following properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5436/compose-postgres
spring.datasource.username=compose-postgres
spring.datasource.password=compose-postgres
```

The `LOCAL` profile allows you to run by connecting to the postgres run with the provided docker-compose at the root of this project.


