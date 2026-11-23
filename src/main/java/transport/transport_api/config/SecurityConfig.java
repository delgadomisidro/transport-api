
package transport.transport_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/orders")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/*")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/orders/*/status")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders")
                        .permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
}
