package com.grosella.pedidos.service;

import com.grosella.pedidos.dto.LoginRequest;
import com.grosella.pedidos.dto.LoginResponse;
import com.grosella.pedidos.exception.NoAutorizadoException;
import com.grosella.pedidos.model.Usuario;
import com.grosella.pedidos.repository.UsuarioRepository;
import com.grosella.pedidos.security.SesionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final SesionService sesionService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository, SesionService sesionService) {
        this.usuarioRepository = usuarioRepository;
        this.sesionService = sesionService;
    }

    /** CU01 Iniciar sesión. */
    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest request) {
        Usuario u = usuarioRepository.buscarParaLogin(request.usuario().trim())
                .filter(Usuario::getActivo)
                .filter(x -> encoder.matches(request.contrasena(), x.getContrasenaHash()))
                .orElseThrow(() -> new NoAutorizadoException("Usuario o contraseña incorrectos."));

        String token = sesionService.crear(u.getIdUsuario(), u.getNombres(), u.getUsuario(),
                u.getRol().getNombre(), u.getSede().getIdSede(), u.getSede().getNombre());
        return new LoginResponse(token, u.getIdUsuario(), u.getNombres(), u.getUsuario(),
                u.getRol().getNombre(), u.getSede().getIdSede(), u.getSede().getNombre());
    }

    public void cerrarSesion(String token) {
        sesionService.cerrar(token);
    }
}
