package com.ingressos.service;

import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.exception.AcessoNegadoException;
import com.ingressos.model.IngressoComprado;
import com.ingressos.repository.EventoRepository;
import com.ingressos.model.Ingresso;
import com.ingressos.repository.IngressoCompradoRepository;
import com.ingressos.repository.IngressoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class IngressoServiceTest {
    private IngressoRepository ingressoRepository;
    private IngressoCompradoRepository ingressoCompradoRepository;
    private EventoRepository eventoRepository;
    private IngressoService service;

    @BeforeEach
    void setup() {
        ingressoRepository = mock(IngressoRepository.class);
        ingressoCompradoRepository = mock(IngressoCompradoRepository.class);
        eventoRepository = mock(EventoRepository.class);
        service = new IngressoService(ingressoRepository, ingressoCompradoRepository, eventoRepository);
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
        RecursoNaoEncontradoException exception = assertThrows(RecursoNaoEncontradoException.class, () -> service.cancelarCompra(99L, 10L));
        assertEquals("Compra não encontrada.", exception.getMessage());
    }

    @Test
    void deveRestaurarEstoqueEMarcarCompraComoCancelada() {
        Ingresso ingresso = new Ingresso();
        ingresso.setId(10L);
        ingresso.setQuantidade(8);
        IngressoComprado compra = new IngressoComprado();
        compra.setId(15L);
        compra.setCompradorId(3L);
        compra.setIngressoId(10L);
        compra.setQuantidadeComprada(2);
        compra.setValorUnitario(new BigDecimal("59.90"));
        compra.setStatus("CONFIRMADA");
        when(ingressoCompradoRepository.findById(15L)).thenReturn(Optional.of(compra));
        when(ingressoRepository.findById(10L)).thenReturn(Optional.of(ingresso));

        service.cancelarCompra(15L, 3L);

        assertEquals(10, ingresso.getQuantidade());
        assertEquals("CANCELADA", compra.getStatus());
        verify(ingressoRepository).save(ingresso);
        verify(ingressoCompradoRepository).save(compra);
    }

    @Test
    void naoDevePermitirCancelamentoDeCompraDeOutroComprador() {
        IngressoComprado compra = new IngressoComprado();
        compra.setCompradorId(3L);
        compra.setStatus("CONFIRMADA");
        when(ingressoCompradoRepository.findById(15L)).thenReturn(Optional.of(compra));

        assertThrows(AcessoNegadoException.class, () -> service.cancelarCompra(15L, 4L));
        verify(ingressoRepository, never()).save(any(Ingresso.class));
    }

    @Test
    void naoDeveRestaurarEstoqueDuasVezesParaCompraCancelada() {
        IngressoComprado compra = new IngressoComprado();
        compra.setCompradorId(3L);
        compra.setStatus("CANCELADA");
        when(ingressoCompradoRepository.findById(15L)).thenReturn(Optional.of(compra));

        service.cancelarCompra(15L, 3L);

        verify(ingressoRepository, never()).findById(anyLong());
        verify(ingressoRepository, never()).save(any(Ingresso.class));
    }
}