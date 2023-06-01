package hotstreams.orderservice.configuration;

import hotstreams.orderservice.security.JwtAuthenticationFilter;
import hotstreams.orderservice.security.JwtTokenProvider;
import hotstreams.orderservice.security.JwtTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
public class WebSecurityConfig {
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider provider, JwtTokenValidator validator) {
        return new JwtAuthenticationFilter(validator, provider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter filter) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .anyRequest().authenticated();
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
