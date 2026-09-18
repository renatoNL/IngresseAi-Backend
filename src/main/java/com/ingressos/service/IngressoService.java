package com.ingressos.service;

import com.ingressos.dto.IngressoCompradoResponse;
import com.ingressos.exception.AcessoNegadoException;
import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Evento;
import com.ingressos.model.Ingresso;
import com.ingressos.model.IngressoComprado;
import com.ingressos.repository.EventoRepository;
import com.ingressos.repository.IngressoRepository;
import com.ingressos.repository.IngressoCompradoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final IngressoCompradoRepository ingressoCompradoRepository;
    private final EventoRepository eventoRepository;

    public IngressoService(IngressoRepository ingressoRepository,
                           IngressoCompradoRepository ingressoCompradoRepository,
                           EventoRepository eventoRepository) {
        this.ingressoRepository = ingressoRepository;
        this.ingressoCompradoRepository = ingressoCompradoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public IngressoComprado comprarIngresso(Long ingressoId, Integer quantidade, Long compradorId) {
        validarQuantidade(quantidade);
        Ingresso ingresso = buscarIngresso(ingressoId);
        validarDisponibilidade(ingresso, quantidade);
        ingresso.setQuantidade(ingresso.getQuantidade() - quantidade);
        ingressoRepository.save(ingresso);
        return ingressoCompradoRepository.save(criarCompra(ingresso, quantidade, compradorId));
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade < 1 || quantidade > 4) {
            throw new RegraNegocioException("A quantidade deve estar entre 1 e 4 ingressos.");
        }
    }

    private Ingresso buscarIngresso(Long ingressoId) {
        return ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ingresso não encontrado."));
    }

    private void validarDisponibilidade(Ingresso ingresso, Integer quantidade) {
        if (ingresso.getQuantidade() < quantidade) {
            throw new RegraNegocioException("Quantidade de ingressos insuficiente.");
        }
    }

    private IngressoComprado criarCompra(Ingresso ingresso, Integer quantidade, Long compradorId) {
        IngressoComprado compra = new IngressoComprado();
        compra.setIngressoId(ingresso.getId());
        compra.setCompradorId(compradorId);
        compra.setQuantidadeComprada(quantidade);
        compra.setValorUnitario(BigDecimal.valueOf(ingresso.getValor()));
        compra.setStatus("CONFIRMADA");
        return compra;
    }

    public List<IngressoCompradoResponse> listarMeusIngressos(Long compradorId) {
        return ingressoCompradoRepository.findByCompradorIdAndStatusOrderByIdAsc(compradorId, "CONFIRMADA").stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelarCompra(Long compraId, Long compradorId) {
        IngressoComprado compra = ingressoCompradoRepository.findById(compraId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra não encontrada."));

        if (!compradorId.equals(compra.getCompradorId())) {
            throw new AcessoNegadoException("Este ingresso não pertence ao comprador.");
        }

        if ("CANCELADA".equals(compra.getStatus())) {
            return;
        }

        Ingresso ingresso = ingressoRepository.findById(compra.getIngressoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ingresso vinculado não encontrado."));

        ingresso.setQuantidade(ingresso.getQuantidade() + compra.getQuantidadeComprada());
        ingressoRepository.save(ingresso);
        compra.setStatus("CANCELADA");
        ingressoCompradoRepository.save(compra);
        }

        private IngressoCompradoResponse toResponse(IngressoComprado compra) {
        Ingresso ingresso = ingressoRepository.findById(compra.getIngressoId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Ingresso vinculado não encontrado."));
        Evento evento = eventoRepository.findById(ingresso.getEventoId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Evento vinculado não encontrado."));
        BigDecimal valorUnitario = compra.getValorUnitario() != null
            ? compra.getValorUnitario()
            : BigDecimal.valueOf(ingresso.getValor());
        String status = compra.getStatus() == null ? "CONFIRMADA" : compra.getStatus();
        BigDecimal valorTotal = valorUnitario.multiply(BigDecimal.valueOf(compra.getQuantidadeComprada()));
        return new IngressoCompradoResponse(
            compra.getId(), evento.getId(), evento.getTitulo(), evento.getDataEvento(),
            compra.getQuantidadeComprada(), valorUnitario, valorTotal, status);
    }
}