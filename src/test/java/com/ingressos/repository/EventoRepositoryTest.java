package com.ingressos.repository;

import com.ingressos.AbstractIntegrationTest;
import com.ingressos.model.Evento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EventoRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveSalvarEBuscarEvento() {
        Evento evento = new Evento();
        evento.setTitulo("Evento Teste");
        evento.setDescricao("Descricao Teste");
        evento.setTipoEvento("SHOW");
        evento.setDataEvento(LocalDateTime.now().plusDays(10));
        evento.setVendedorId(1L);
        
        Evento eventoSalvo = entityManager.persistAndFlush(evento);

        Optional<Evento> resultado = eventoRepository.findById(eventoSalvo.getId());

        assertTrue(resultado.isPresent());
        assertEquals("Evento Teste", resultado.get().getTitulo());
    }
}