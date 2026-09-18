package com.ingressos.controller;

import com.ingressos.model.Evento;
import com.ingressos.service.EventoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eventos")
public class EventoController {
    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping("/eventos/ofertas")
    public List<Map<String, Object>> listarOfertas() {
        return eventoService.listarOfertas();
    }

    @GetMapping("/eventos/meus")
    public List<Evento> listarMeusEventos() {
        return eventoService.listarEventosDoVendedor();
    }
}