package com.ingressos.service;

import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Ingresso;
import com.ingressos.model.IngressoComprado;
import com.ingressos.repository.IngressoRepository;
import com.ingressos.repository.IngressoCompradoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final IngressoCompradoRepository ingressoCompradoRepository;

    public IngressoService(IngressoRepository ingressoRepository, IngressoCompradoRepository ingressoCompradoRepository) {
        this.ingressoRepository = ingressoRepository;
        this.ingressoCompradoRepository = ingressoCompradoRepository;
    }

    @Transactional
    public IngressoComprado comprarIngresso(Long ingressoId, Integer quantidade, Long compradorId) {
        validarQuantidade(quantidade);
        Ingresso ingresso = buscarIngresso(ingressoId);
        validarDisponibilidade(ingresso, quantidade);
        ingresso.setQuantidade(ingresso.getQuantidade() - quantidade);
        ingressoRepository.save(ingresso);
        return ingressoCompradoRepository.save(criarCompra(ingressoId, quantidade, compradorId));
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

    private IngressoComprado criarCompra(Long ingressoId, Integer quantidade, Long compradorId) {
        IngressoComprado compra = new IngressoComprado();
        compra.setIngressoId(ingressoId);
        compra.setCompradorId(compradorId);
        compra.setQuantidadeComprada(quantidade);
        return compra;
    }

    public List<IngressoComprado> listarMeusIngressos(Long compradorId) {
        return ingressoCompradoRepository.findByCompradorId(compradorId);
    }

    @Transactional
    public void cancelarCompra(Long compraId) {
        IngressoComprado compra = ingressoCompradoRepository.findById(compraId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Compra não encontrada."));

        Ingresso ingresso = ingressoRepository.findById(compra.getIngressoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Ingresso vinculado não encontrado."));

        ingresso.setQuantidade(ingresso.getQuantidade() + compra.getQuantidadeComprada());
        ingressoRepository.save(ingresso);
        
        ingressoCompradoRepository.delete(compra);
    }
}