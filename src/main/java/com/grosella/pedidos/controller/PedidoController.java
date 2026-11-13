package com.grosella.pedidos.controller;

import com.grosella.pedidos.dto.CambioEstadoRequest;
import com.grosella.pedidos.dto.CancelacionRequest;
import com.grosella.pedidos.dto.PagoRequest;
import com.grosella.pedidos.dto.PedidoDetalleResponse;
import com.grosella.pedidos.dto.PedidoRequest;
import com.grosella.pedidos.dto.PedidoResumenResponse;
import com.grosella.pedidos.dto.ResumenEstadoResponse;
import com.grosella.pedidos.model.EstadoPedido;
import com.grosella.pedidos.security.SesionInterceptor;
import com.grosella.pedidos.security.UsuarioSesion;
import com.grosella.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** CU08 Registrar pedido */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDetalleResponse registrar(@Valid @RequestBody PedidoRequest request,
                                           @RequestAttribute(SesionInterceptor.ATRIBUTO_SESION) UsuarioSesion sesion) {
        return pedidoService.registrar(request, sesion);
    }

    /** CU02 Consultar pedidos por estado: GET /api/pedidos?estado=EN_PREPARACION&idSede=1 */
    @GetMapping
    public List<PedidoResumenResponse> porEstado(@RequestParam EstadoPedido estado,
                                                 @RequestParam(required = false) Integer idSede) {
        return pedidoService.listarPorEstado(estado, idSede);
    }

    @GetMapping("/resumen-estados")
    public List<ResumenEstadoResponse> resumen(@RequestParam(required = false) Integer idSede) {
        return pedidoService.resumenPorEstado(idSede);
    }

    /** CU15 Consultar pedidos programados */
    @GetMapping("/programados")
    public List<PedidoResumenResponse> programados(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime hasta,
            @RequestParam(required = false) Integer idSede) {
        return pedidoService.listarProgramados(fecha, desde, hasta, idSede);
    }

    @GetMapping("/proximos")
    public List<PedidoResumenResponse> proximos(@RequestParam(required = false) Integer idSede,
                                                @RequestParam(defaultValue = "8") int limite) {
        return pedidoService.listarProximos(idSede, Math.min(Math.max(limite, 1), 50));
    }

    /** CU17 Buscar pedido: GET /api/pedidos/P-000128 */
    @GetMapping("/{numero}")
    public PedidoDetalleResponse buscar(@PathVariable String numero) {
        return pedidoService.buscarPorNumero(numero);
    }

    /** CU12 Confirmar pedido / CU14 Actualizar estado de preparación */
    @PatchMapping("/{idPedido}/estado")
    public PedidoDetalleResponse cambiarEstado(@PathVariable Integer idPedido,
                                               @Valid @RequestBody CambioEstadoRequest request,
                                               @RequestAttribute(SesionInterceptor.ATRIBUTO_SESION) UsuarioSesion sesion) {
        return pedidoService.cambiarEstado(idPedido, request.estado(), sesion);
    }

    /** CU10 Registrar pago del pedido */
    @PostMapping("/{idPedido}/pagos")
    public PedidoDetalleResponse registrarPago(@PathVariable Integer idPedido, @Valid @RequestBody PagoRequest request,
                                               @RequestAttribute(SesionInterceptor.ATRIBUTO_SESION) UsuarioSesion sesion) {
        return pedidoService.registrarPago(idPedido, request.monto(), sesion);
    }

    /** CU11 Registrar entrega del pedido */
    @PostMapping("/{idPedido}/entrega")
    public PedidoDetalleResponse registrarEntrega(@PathVariable Integer idPedido,
                                                  @RequestAttribute(SesionInterceptor.ATRIBUTO_SESION) UsuarioSesion sesion) {
        return pedidoService.registrarEntrega(idPedido, sesion);
    }

    /** CU13 Cancelar pedido */
    @PostMapping("/{idPedido}/cancelacion")
    public PedidoDetalleResponse cancelar(@PathVariable Integer idPedido, @Valid @RequestBody CancelacionRequest request,
                                          @RequestAttribute(SesionInterceptor.ATRIBUTO_SESION) UsuarioSesion sesion) {
        return pedidoService.cancelar(idPedido, request.motivo(), sesion);
    }
}
