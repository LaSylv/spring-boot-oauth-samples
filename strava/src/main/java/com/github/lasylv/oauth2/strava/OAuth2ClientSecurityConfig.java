package com.github.lasylv.oauth2.strava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.http.converter.OAuth2ErrorHttpMessageConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

import java.util.function.Consumer;

@Configuration
@EnableWebSecurity
@EnableJdbcHttpSession
public class OAuth2ClientSecurityConfig {


    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Value("spring.security.oauth2.client.registration.strava.scope")
    private String stravaScopes;

	@Bean
	OAuth2AuthorizedClientService authorizedClientService(JdbcTemplate jdbcTemplate, ClientRegistrationRepository clientRegistrationRepository) {
		return new JdbcOAuth2AuthorizedClientService(jdbcTemplate,
				clientRegistrationRepository
		);
	}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(a -> a
                        // In this example configuration, all GET request do not requires authentication, while any other requests require to be authenticated
                        .requestMatchers(HttpMethod.GET)
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(config -> {
                    // See https://github.com/spring-projects/spring-boot/issues/15398 for why you need this when using oauth2 with Strava
                    config.authorizationEndpoint(authorization -> authorization
                            .authorizationRequestResolver(
                                    authorizationRequestResolver(this.clientRegistrationRepository)
                            )
                    );
                })
                // WARNING: You should enable it in production
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((customizer) -> customizer.sessionCreationPolicy(SessionCreationPolicy.ALWAYS));
        return http.build();
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                authorizationRequestCustomizer());

        return  authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer
                .additionalParameters(params -> params.put("scope", stravaScopes));
    }

    @Bean
    public HttpMessageConverter<OAuth2Error> customOAuth2ErrorHttpMessageConverter() {
        return new CustomOAuth2ErrorHttpMessageConverter();
    }

    // Define your custom OAuth2ErrorHttpMessageConverter implementation
    static class CustomOAuth2ErrorHttpMessageConverter extends OAuth2ErrorHttpMessageConverter {
        // Override methods as needed
    }
}