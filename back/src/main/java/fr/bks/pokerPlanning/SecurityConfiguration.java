package fr.bks.pokerPlanning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * @author Kévin Buntrock
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);

    private static final long MAX_AGE_IN_SECONDS = 365L * 24 * 60 * 60;

    @Value("${corsAllowedUrls}")
    private String corsAllowedUrls;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        LOGGER.info("CORS urls : {}", corsAllowedUrls);
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(corsAllowedUrls.split(",")));
        configuration.setAllowedMethods(List.of(HttpMethod.GET.toString(), HttpMethod.POST.toString(), HttpMethod.OPTIONS.toString()));
        // Une année. Voir :
        // https://docs.spring.io/spring-framework/docs/5.3.9/reference/html/web.html#websocket-fallback-cors
        configuration.setMaxAge(MAX_AGE_IN_SECONDS);
        configuration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll();
        http
                .csrf(csrf -> csrf
                        .ignoringAntMatchers("/api/websocket/**")
                ).cors();


    }
}
