package com.github.lasylv.oauth2.strava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
public class SpringOauthStravaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringOauthStravaApplication.class, args);
    }

}
