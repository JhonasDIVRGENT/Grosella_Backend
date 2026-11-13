package com.grosella.pedidos.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Verifica el token en cada petición a /api/** (excepto el login). */
@Component
public class SesionInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_SESION = "usuarioSesion";

    private final SesionService sesionService;

    public SesionInterceptor(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true; // petición previa de CORS
        }
        UsuarioSesion sesion = sesionService.validar(extraerToken(request));
        request.setAttribute(ATRIBUTO_SESION, sesion);
        return true;
    }

    public static String extraerToken(HttpServletRequest request) {
        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            return cabecera.substring(7).trim();
        }
        return null;
    }
}
