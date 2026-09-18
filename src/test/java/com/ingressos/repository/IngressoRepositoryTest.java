package com.ingressos.repository;

import com.ingressos.AbstractIntegrationTest;
import com.ingressos.model.IngressoComprado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IngressoCompradoRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private IngressoCompradoRepository ingressoCompradoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveRetornarListaDeIngressosCompradosPorCompradorId() {
        IngressoComprado compra1 = new IngressoComprado();
        compra1.setCompradorId(1L);
        compra1.setIngressoId(10L);
        compra1.setQuantidadeComprada(2);
        entityManager.persist(compra1);

        IngressoComprado compra2 = new IngressoComprado();
        compra2.setCompradorId(1L);
        compra2.setIngressoId(11L);
        compra2.setQuantidadeComprada(1);
        entityManager.persist(compra2);
        
        entityManager.flush();

        List<IngressoComprado> compras = ingressoCompradoRepository.findByCompradorIdOrderByIdAsc(1L);
        assertEquals(2, compras.size());
    }

    @Test
    void deveRetornarListaVaziaSeCompradorNaoTiverCompras() {
        List<IngressoComprado> compras = ingressoCompradoRepository.findByCompradorIdOrderByIdAsc(99L);
        assertTrue(compras.isEmpty());
    }
}