package com.ingressos.repository;

import com.ingressos.AbstractIntegrationTest;
import com.ingressos.model.Evento;
import com.ingressos.model.Ingresso;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IngressoRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IngressoRepository ingressoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveEncontrarIngressoPorEventoId() {
        Evento evento = new Evento();
        evento.setTitulo("Evento para ingresso");
        evento.setDescricao("Descricao");
        evento.setTipoEvento("SHOW");
        evento.setDataEvento(java.time.LocalDateTime.now().plusDays(1));
        evento.setVendedorId(1L);
        evento = eventoRepository.saveAndFlush(evento);

        Ingresso ingresso = new Ingresso();
        ingresso.setEventoId(evento.getId());
        ingresso.setQuantidade(100);
        ingresso.setValor(50.0);
        
        entityManager.persistAndFlush(ingresso);

        Optional<Ingresso> resultado = ingressoRepository.findByEventoId(evento.getId());
        
        assertTrue(resultado.isPresent());
        assertEquals(100, resultado.get().getQuantidade());
    }

    @Test
    void naoDeveEncontrarIngressoParaEventoSemIngresso() {
        Optional<Ingresso> resultado = ingressoRepository.findByEventoId(99L);
        assertFalse(resultado.isPresent());
    }
}