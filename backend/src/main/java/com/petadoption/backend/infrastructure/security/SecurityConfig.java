package com.petadoption.backend.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ROTAS PÚBLICAS
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // catálogo público (somente leitura)
                .requestMatchers(HttpMethod.GET, "/api/pets/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/organizations/**").permitAll()

                // ===== REGRAS PARA PETS =====
                // Qualquer usuário autenticado pode criar/alterar/excluir pets.
                // O PetService é quem garante:
                //   - só cria em nome de si mesmo
                //   - só o dono pode atualizar/excluir o próprio pet
                .requestMatchers(HttpMethod.POST, "/api/pets/**")
                    .authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/pets/**")
                    .authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/pets/**")
                    .authenticated()

                // (futuro) – pedidos de adoção: só ADOPTER cria
                .requestMatchers(HttpMethod.POST, "/api/adoptions/**")
                    .hasRole("ADOPTER")

                // TUDO O RESTO PRECISA DE LOGIN
                .anyRequest().authenticated()
            )

            // quando NÃO autenticado -> 401 (e não 403)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                })
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}