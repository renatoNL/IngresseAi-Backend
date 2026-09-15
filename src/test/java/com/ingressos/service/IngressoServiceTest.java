package com.ingressos.service;

import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Ingresso;
import com.ingressos.repository.IngressoCompradoRepository;
import com.ingressos.repository.IngressoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngressoServiceTest {
    private IngressoRepository ingressoRepository;
    private IngressoCompradoRepository ingressoCompradoRepository;
    private IngressoService service;

    @BeforeEach
    void setup() {
        ingressoRepository = mock(IngressoRepository.class);
        ingressoCompradoRepository = mock(IngressoCompradoRepository.class);
        service = new IngressoService(ingressoRepository, ingressoCompradoRepository);
    }

    @Test
    void deveRejeitarCompraSeQuantidadeForMaiorQueOEstoqueDisponivel() {
        Ingresso ingressoMock = new Ingresso();
        ingressoMock.setQuantidade(2);
        when(ingressoRepository.findById(1L)).thenReturn(Optional.of(ingressoMock));

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> service.comprarIngresso(1L, 3, 10L));
        assertEquals("Quantidade de ingressos insuficiente.", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoAoComprarIngressoInexistente() {
        when(ingressoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.comprarIngresso(99L, 2, 10L));
    }

    @Test
    void deveLancarExcecaoAoTentarCancelarCompraInexistente() {
        when(ingressoCompradoRepository.findById(99L)).thenReturn(Optional.empty());
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> service.cancelarCompra(99L));
        assertEquals("Compra não encontrada.", exception.getMessage());
    }
}