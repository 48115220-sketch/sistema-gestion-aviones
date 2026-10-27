package com.terminal.auth.config;

import com.terminal.auth.model.Usuario;
import com.terminal.auth.repository.UsuarioRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando verificación y carga de usuarios iniciales...");
        crearUsuarioSiNoExiste("admin", "admin777", "admin@terminal.com", "ROOT");
        crearUsuarioSiNoExiste("piloto", "123456", "piloto@terminal.com", "PILOTO");
        crearUsuarioSiNoExiste("azafata", "123456", "azafata@terminal.com", "AZAFATA");
        log.info("Carga inicial de usuarios finalizada.");
    }

    private void crearUsuarioSiNoExiste(String nombreUsuario, String clave, String correo, String rol) {
        if (!usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            Usuario usuario = Usuario.builder()
                    .nombreUsuario(nombreUsuario)
                    .clave(passwordEncoder.encode(clave))
                    .correo(correo)
                    .rol(rol)
                    .build();
            usuarioRepository.save(usuario);
            log.info("Usuario inicial creado: {} (Rol: {})", nombreUsuario, rol);
        }
    }
}