package com.grosella.pedidos.controller;

import com.grosella.pedidos.dto.LoginRequest;
import com.grosella.pedidos.dto.LoginResponse;
import com.grosella.pedidos.security.SesionInterceptor;
import com.grosella.pedidos.security.UsuarioSesion;
import com.grosella.pedidos.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.iniciarSesion(request);
    }

    @GetMapping("/sesion")
    public UsuarioSesion sesionActual(@RequestAttribute(SesionInterceptor.ATRIBUTO_SESION) UsuarioSesion sesion) {
        return sesion;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.cerrarSesion(SesionInterceptor.extraerToken(request));
        return ResponseEntity.noContent().build();
    }
}
