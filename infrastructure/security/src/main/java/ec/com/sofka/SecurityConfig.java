package ec.com.sofka;

import ec.com.sofka.user.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(
            ServerHttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            ReactiveAuthenticationManager authManager)
    throws Exception {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Deshabilitar CSRF
                .authorizeExchange(exchanges -> exchanges
                        // Permitir acceso a Swagger UI, documentación y recursos estáticos
                        .pathMatchers("/swagger-ui.html", "/swagger-ui/**", "/webjars/**", "/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/v1/user/create").permitAll()
                        .pathMatchers("/api/v1/user/authenticate").permitAll()

                        // Solo permitir acceso al admin en las siguientes rutas
                        .pathMatchers("/accounts").hasRole(String.valueOf(Role.ADMIN))
                        .anyExchange().authenticated()
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(authManager)
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
