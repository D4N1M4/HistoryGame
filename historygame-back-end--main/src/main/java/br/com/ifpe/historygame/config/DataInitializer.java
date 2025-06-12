package br.com.ifpe.historygame.config;

import br.com.ifpe.historygame.entity.Role;
import br.com.ifpe.historygame.entity.Usuario;
import br.com.ifpe.historygame.repository.RoleRepository;
import br.com.ifpe.historygame.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Bean
    CommandLineRunner initDefaultData(RoleRepository roleRepository, UsuarioRepository usuarioRepository) {
        return args -> {
            Role roleUser = roleRepository.findByNome("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_USER")));

            Role roleAdmin = roleRepository.findByNome("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));

            String emailAdmin = "admin@admin.com";
            if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail(emailAdmin);
                admin.setSenha(passwordEncoder.encode("14253614"));
                admin.setRoles(Set.of(roleAdmin, roleUser));
                usuarioRepository.save(admin);
                System.out.println("✅ ADMIN padrão criado: " + emailAdmin + " / 14253614");
            }
        };
    }
}
