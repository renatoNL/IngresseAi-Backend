package com.ingressos.controller;

import com.ingressos.dto.EventoRequestDTO;
import com.ingressos.model.Evento;
import com.ingressos.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/eventos")
@Tag(name = "Vendedor/Administrador", description = "Gerenciamento de eventos")
public class AdministradorController {

    private final EventoService eventoService;

    public AdministradorController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    private Long getUsuarioAutenticadoId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar um novo evento e seus ingressos")
    public Evento criarEvento(@RequestBody @Valid EventoRequestDTO dto) {
        return eventoService.criarEvento(dto, getUsuarioAutenticadoId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir um evento pelo ID")
    public void cancelarEvento(@PathVariable Long id) {
        eventoService.cancelarEvento(id);
    }

    @GetMapping
    @Operation(summary = "Listar todos os eventos")
    public List<Evento> listarEventos() {
        return eventoService.listarEventos();
    }
}