package com.grosella.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GestionPedidosApplication {

    public static void main(String[] args) {
        // Las fechas y horas de recojo se manejan con la hora de Perú
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        SpringApplication.run(GestionPedidosApplication.class, args);
    }
}
