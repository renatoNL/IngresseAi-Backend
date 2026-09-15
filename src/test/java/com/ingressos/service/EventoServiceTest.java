package com.ingressos.service;

import com.ingressos.dto.EventoRequestDTO;
import com.ingressos.exception.RecursoNaoEncontradoException;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Evento;
import com.ingressos.repository.EventoRepository;
import com.ingressos.repository.IngressoRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
}