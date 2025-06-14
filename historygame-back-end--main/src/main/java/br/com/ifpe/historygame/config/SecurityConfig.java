package br.com.ifpe.historygame.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import br.com.ifpe.historygame.handler.GoogleOAuth2SuccessHandler;
import br.com.ifpe.historygame.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final GoogleOAuth2SuccessHandler successHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          GoogleOAuth2SuccessHandler successHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.successHandler = successHandler;
    }

    // Codificador de senha com BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager necessário para login manual via /api/usuarios/login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configuração da cadeia de filtros de segurança
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // desativa CSRF para uso com frontend separado
            .cors(Customizer.withDefaults()) // ativa CORS com config abaixo
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // permite uso de sessão (JSESSIONID)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/login",
                    "/oauth2/**",
                    "/api/usuarios/register",
                    "/api/usuarios/login",
                    "/api/usuarios/redefinir-senha",
                    "/api/jogos/favoritos/mais",
                    "/api/jogos/{id}",
                    "/api/jogos/buscar",
                    "/api/jogos",
                    "/api/generos",
                    "/api/generos/**",
                    "/api/jogos/genero/{nomeGenero}",
                    "/api/jogos/acesssos",

                ).permitAll()
                .requestMatchers("/admin/**", "/api/usuarios/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // não usado diretamente por frontend, mas obrigatório declarar
                .permitAll()
                .successHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                })
                .failureHandler((request, response, exception) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Credenciais inválidas");
                })
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler(successHandler)
            )
            .logout(logout -> logout
                // 1. Define a URL exata que o frontend chama para o logout
                .logoutUrl("/api/usuarios/logout")
    
                // 2. Garante que a sessão no servidor seja destruída
                .invalidateHttpSession(true)
    
                // 3. Instrui o navegador a apagar o cookie de sessão
                .deleteCookies("JSESSIONID")
    
                // 4. A correção mais importante: em vez de redirecionar, apenas retorna "200 OK"
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
            );

        return http.build();
    }

    // Configuração global de CORS para permitir requisições do frontend Vue
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true); // importante para enviar cookies (JSESSIONID)
            }
        };
    }
}
