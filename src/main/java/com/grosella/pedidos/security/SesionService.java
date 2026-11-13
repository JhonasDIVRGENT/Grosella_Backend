package com.grosella.pedidos.security;

import com.grosella.pedidos.exception.NoAutorizadoException;
import com.grosella.pedidos.exception.SinPermisoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Login sencillo de la V01: al iniciar sesión se entrega un token aleatorio que el
 * frontend envía en la cabecera "Authorization: Bearer ...". Los tokens se guardan
 * en memoria (se pierden al reiniciar el backend).
 * En la siguiente versión se reemplazará por Spring Security + JWT.
 */
@Component
public class SesionService {

    private final Map<String, UsuarioSesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final long duracionHoras;

    public SesionService(@Value("${app.sesion.duracion-horas:8}") long duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public String crear(Integer idUsuario, String nombres, String usuario, String rol, Integer idSede, String sede) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sesiones.put(token, new UsuarioSesion(idUsuario, nombres, usuario, rol, idSede, sede,
                LocalDateTime.now().plusHours(duracionHoras)));
        return token;
    }

    public UsuarioSesion validar(String token) {
        UsuarioSesion sesion = token == null ? null : sesiones.get(token);
        if (sesion == null) {
            throw new NoAutorizadoException("Su sesión no es válida. Inicie sesión nuevamente.");
        }
        if (sesion.expira().isBefore(LocalDateTime.now())) {
            sesiones.remove(token);
            throw new NoAutorizadoException("Su sesión expiró. Inicie sesión nuevamente.");
        }
        return sesion;
    }

    public void cerrar(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    public static void verificarPermiso(UsuarioSesion sesion, Accion accion) {
        if (!accion.permitidaPara(sesion.rol())) {
            throw new SinPermisoException("Su perfil no tiene permiso para esta operación.");
        }
    }
}
