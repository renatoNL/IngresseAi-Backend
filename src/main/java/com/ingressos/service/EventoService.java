package com.ingressos.service;

import com.ingressos.dto.EventoRequestDTO;
import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Evento;
import com.ingressos.model.Ingresso;
import com.ingressos.repository.EventoRepository;
import com.ingressos.repository.IngressoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final IngressoRepository ingressoRepository;
    private static final long LIMITE_MAXIMO_EVENTOS = 50;

    public EventoService(EventoRepository eventoRepository, IngressoRepository ingressoRepository) {
        this.eventoRepository = eventoRepository;
        this.ingressoRepository = ingressoRepository;
    }

    @Transactional
    public Evento criarEvento(EventoRequestDTO dto, Long vendedorId) {
        validarLimiteDeEventos();
        Evento eventoSalvo = eventoRepository.save(criarEntidadeEvento(dto, vendedorId));
        criarLoteDeIngressos(eventoSalvo, dto);
        return eventoSalvo;
    }

    private void validarLimiteDeEventos() {
        if (eventoRepository.count() >= LIMITE_MAXIMO_EVENTOS) {
            throw new RegraNegocioException("Erro fatal: Limite máximo de " + LIMITE_MAXIMO_EVENTOS + " eventos atingido.");
        }
    }

    private Evento criarEntidadeEvento(EventoRequestDTO dto, Long vendedorId) {
        Evento evento = new Evento();
        evento.setVendedorId(vendedorId);
        evento.setTitulo(dto.getTitulo());
        evento.setDescricao(dto.getDescricao());
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setDataEvento(dto.getDataEvento());
        return evento;
    }

    private void criarLoteDeIngressos(Evento evento, EventoRequestDTO dto) {
        Ingresso ingresso = new Ingresso();
        ingresso.setEventoId(evento.getId());
        ingresso.setQuantidade(dto.getQuantidadeIngressos());
        ingresso.setValor(dto.getValorIngresso());
        ingressoRepository.save(ingresso);
    }

    @Transactional
    public void cancelarEvento(Long id) {
        buscarEvento(id);
        eventoRepository.deleteById(id);
    }

    private Evento buscarEvento(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado."));
    }

    public List<Evento> listarEventos() {
        return eventoRepository.findAll();
    }

    public List<Map<String, Object>> listarOfertas() {
        return eventoRepository.findAll().stream()
                .map(evento -> {
                Ingresso ingresso = ingressoRepository.findByEventoIdOrderByIdAsc(evento.getId()).stream()
                    .filter(item -> item.getQuantidade() != null && item.getQuantidade() > 0)
                    .findFirst()
                    .orElse(null);
                    Map<String, Object> oferta = new HashMap<>();
                    oferta.put("id", evento.getId());
                    oferta.put("titulo", evento.getTitulo());
                    oferta.put("descricao", evento.getDescricao());
                    oferta.put("tipoEvento", evento.getTipoEvento());
                    oferta.put("dataEvento", evento.getDataEvento());
                    oferta.put("ingressoId", ingresso == null ? 0L : ingresso.getId());
                    oferta.put("valorIngresso", ingresso == null ? 0D : ingresso.getValor());
                    oferta.put("quantidadeDisponivel", ingresso == null ? 0 : ingresso.getQuantidade());
                    return oferta;
                })
                .collect(Collectors.toList());
    }
}