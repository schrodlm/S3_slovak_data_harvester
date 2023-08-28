package cz.trixi.schrodlm.slovakcompany.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//TODO: Security Config  - files cannot be downloaded by unauthorized person
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${usernameAuth}")
    private String username;

    @Value("${passwordAuth}")
    private String password;


    @Bean
    public SecurityFilterChain filterChain( HttpSecurity httpSecurity ) throws Exception {
        return httpSecurity
                .csrf( csrf -> csrf.disable() )
                .authorizeHttpRequests( auth -> {
                            auth
                                    .requestMatchers( "/health", "/revision" ).permitAll()
                                    .anyRequest().authenticated();

                        }
                )
                .httpBasic( Customizer.withDefaults() )
                .build();
    }

    @Bean
    // "{noop}" prefix is typically used in Spring Security to indicate that the password should be stored as plain text.
    public UserDetailsService users() {
        UserDetails user = User.builder()
                .username( username )
                .password( "{noop}" + password )
                .roles( "USER" )
                .build();
        return new InMemoryUserDetailsManager( user );
    }
}
