package com.ingressos.service;

import com.ingressos.dto.EventoRequestDTO;
import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Evento;
import com.ingressos.repository.EventoRepository;
import com.ingressos.repository.IngressoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventoServiceTest {
    private final EventoRepository eventoRepository = mock(EventoRepository.class);
    private final IngressoRepository ingressoRepository = mock(IngressoRepository.class);
    private final EventoService service = new EventoService(eventoRepository, ingressoRepository);

    @Test
    void deveBloquearCriacaoDeEventoSeLimiteMaximoAtingido() {
        when(eventoRepository.count()).thenReturn(50L);
        EventoRequestDTO dto = new EventoRequestDTO();
        dto.setTitulo("Show de Rock");

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> service.criarEvento(dto, 1L));
        assert(exception.getMessage().contains("Limite máximo"));
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    void deveLancarExcecaoAoTentarCancelarEventoInexistente() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNaoEncontradoException.class, () -> service.cancelarEvento(99L));
        verify(eventoRepository, never()).deleteById(anyLong());
    }

    @Test
    void deveListarApenasEventosDoVendedorInformado() {
        Evento evento = new Evento();
        evento.setId(10L);
        evento.setVendedorId(7L);
        when(eventoRepository.findByVendedorIdOrderByIdAsc(7L)).thenReturn(List.of(evento));

        List<Evento> resultado = service.listarEventos(7L);

        assertEquals(1, resultado.size());
        assertEquals(7L, resultado.get(0).getVendedorId());
        verify(eventoRepository).findByVendedorIdOrderByIdAsc(7L);
        verify(eventoRepository, never()).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoVendedorNaoPossuiEventos() {
        when(eventoRepository.findByVendedorIdOrderByIdAsc(8L)).thenReturn(List.of());

        List<Evento> resultado = service.listarEventos(8L);

        assertTrue(resultado.isEmpty());
    }
}