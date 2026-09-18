package com.ingressos.service;

import com.ingressos.dto.EventoRequestDTO;
import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Evento;
import com.ingressos.model.Ingresso;
import com.ingressos.repository.EventoRepository;
import com.ingressos.repository.IngressoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final IngressoRepository ingressoRepository;

    @Autowired
    public EventoService(EventoRepository eventoRepository, IngressoRepository ingressoRepository) {
        this.eventoRepository = eventoRepository;
        this.ingressoRepository = ingressoRepository;
    }

    private Long getVendedorLogadoId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(userId);
    }

    @Transactional
    public Evento criarEvento(EventoRequestDTO dto, Long vendedorId) {
        if (eventoRepository.countByVendedorId(vendedorId) >= 50) {
            throw new RegraNegocioException("Erro fatal: Limite máximo de 50 eventos atingido.");
        }

        Evento evento = new Evento();
        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setDataEvento(dto.getDataEvento());
        evento.setVendedorId(vendedorId);
        
        Evento eventoSalvo = eventoRepository.save(evento);

        Ingresso ingresso = new Ingresso();
        ingresso.setEventoId(eventoSalvo.getId());
        ingresso.setQuantidade(dto.getQuantidadeIngressos());
        ingresso.setValor(dto.getValorIngresso());
        ingressoRepository.save(ingresso);

        return eventoSalvo;
    }

    public List<Evento> listarEventos(Long vendedorId) {
        return eventoRepository.findByVendedorId(vendedorId);
    }

    public List<Evento> listarEventosDoVendedor() {
        return listarEventos(getVendedorLogadoId());
    }

    @Transactional
    public void cancelarEvento(Long id) {
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado."));
        
        List<Ingresso> ingressos = ingressoRepository.findByEventoIdOrderByIdAsc(id);
        ingressoRepository.deleteAll(ingressos);
        
        eventoRepository.delete(evento);
    }

    public List<Map<String, Object>> listarOfertas() {
        List<Evento> eventos = eventoRepository.findAll();
        return eventos.stream().map(evento -> {
            Map<String, Object> oferta = new HashMap<>();
            oferta.put("id", evento.getId());
            oferta.put("titulo", evento.getTitulo());
            oferta.put("descricao", evento.getDescricao());
            oferta.put("tipoEvento", evento.getTipoEvento());
            oferta.put("dataEvento", evento.getDataEvento());
            
            List<Ingresso> ingressos = ingressoRepository.findByEventoIdOrderByIdAsc(evento.getId());
            if (!ingressos.isEmpty()) {
                Ingresso ingresso = ingressos.get(0);
                oferta.put("ingressoId", ingresso.getId());
                oferta.put("valorIngresso", ingresso.getValor());
                oferta.put("quantidadeDisponivel", ingresso.getQuantidade());
            } else {
                oferta.put("valorIngresso", 0.0);
                oferta.put("quantidadeDisponivel", 0);
            }
            
            return oferta;
        }).collect(Collectors.toList());
    }
}