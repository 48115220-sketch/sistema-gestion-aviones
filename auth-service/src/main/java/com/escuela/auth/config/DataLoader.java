package com.escuela.auth.config;

import com.escuela.auth.model.Usuario;
import com.escuela.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        crearUsuarioSiNoExiste("admin", "admin777", "admin@escuela.com", "ROOT");
        crearUsuarioSiNoExiste("preceptor", "123456", "preceptor@escuela.com", "PRECEPTOR");
        crearUsuarioSiNoExiste("docente", "123456", "docente@escuela.com", "DOCENTE");
        crearUsuarioSiNoExiste("director", "123456", "director@escuela.com", "DIRECTOR");
        crearUsuarioSiNoExiste("administrativo", "123456", "adminis@escuela.com", "ADMINISTRATIVO");
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
        }
    }
}
