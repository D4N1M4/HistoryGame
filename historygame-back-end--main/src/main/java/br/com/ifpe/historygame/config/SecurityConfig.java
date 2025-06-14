package br.com.ifpe.historygame.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    // Configuração para CORS global (se você usar essa abordagem, remova @CrossOrigin dos Controllers)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5174") // A porta do seu frontend Vue.js
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // Permitir todos os métodos necessários
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF (importante para APIs REST)
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público (sem autenticação) a endpoints específicos:
                .requestMatchers("/api/jogos").permitAll() // Permite GET /api/jogos (para listar)
                .requestMatchers("/api/jogos/{id}").permitAll() // Permite GET /api/jogos/{id} (para buscar um jogo)
                .requestMatchers("/api/jogos/{id}/acesso").permitAll() // Permite PATCH /api/jogos/{id}/acesso (para incrementar acessos)
                .requestMatchers("/api/generos").permitAll() // Permite GET /api/generos
                // Se tiver outros endpoints como /api/reviews, /api/usuarios que devem ser protegidos, coloque-os aqui
                // .requestMatchers("/api/reviews/**").authenticated() // Exemplo: reviews exigem autenticação
                // .requestMatchers("/api/usuarios/**").authenticated() // Exemplo: usuários exigem autenticação
                .anyRequest().authenticated() // Todas as outras requisições exigem autenticação
            )
            .httpBasic(Customizer.withDefaults()); // Opcional: Se você usa autenticação básica

        return http.build();
    }

    // Se você tiver um UserDetailsService ou PasswordEncoder, eles iriam aqui também.
}