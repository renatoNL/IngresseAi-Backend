package com.ingressos.controller;

import com.ingressos.dto.CompraIngressoDTO;
import com.ingressos.dto.IngressoCompradoResponse;
import com.ingressos.model.IngressoComprado;
import com.ingressos.service.IngressoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comprador/ingressos")
public class CompradorController {
    private final IngressoService ingressoService;

    public CompradorController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    private Long getUsuarioAutenticadoId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/comprar")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Comprar ingresso usando DTO de forma segura")
    public IngressoComprado comprarIngresso(@RequestBody @Valid CompraIngressoDTO dto) {
        return ingressoService.comprarIngresso(dto.ingressoId(), dto.quantidade(), getUsuarioAutenticadoId());
    }

    @GetMapping
    @Operation(summary = "Listar ingressos do usuário logado")
    public List<IngressoCompradoResponse> listarMeusIngressos() {
        return ingressoService.listarMeusIngressos(getUsuarioAutenticadoId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancelar uma compra do comprador autenticado")
    public void cancelarIngresso(@PathVariable Long id) {
        ingressoService.cancelarCompra(id, getUsuarioAutenticadoId());
    }
}