package com.ingressos.service;


import com.ingressos.AbstractIntegrationTest;
import com.ingressos.model.Evento;
import com.ingressos.model.Ingresso;
import com.ingressos.model.Usuario;
import com.ingressos.repository.EventoRepository;
import com.ingressos.repository.IngressoRepository;
import com.ingressos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class IngressoServiceConcurrencyTest extends AbstractIntegrationTest {

    @Autowired
    private IngressoService ingressoService;

    @Autowired
    private IngressoRepository ingressoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void devePrevenirVendaDuplaComOptimisticLocking() throws InterruptedException {
        Usuario comprador = new Usuario();
        comprador.setNomeCompleto("Comprador de teste");
        comprador.setCpfCnpj("9" + System.currentTimeMillis());
        comprador.setSenha("senha");
        comprador.setTipo("COMPRADOR");
        comprador = usuarioRepository.saveAndFlush(comprador);
        final Long compradorId = comprador.getId();

        Evento evento = new Evento();
        evento.setTitulo("Evento de concorrencia");
        evento.setDescricao("Descricao");
        evento.setTipoEvento("SHOW");
        evento.setDataEvento(java.time.LocalDateTime.now().plusDays(1));
        evento.setVendedorId(comprador.getId());
        evento = eventoRepository.saveAndFlush(evento);

        Ingresso ingresso = new Ingresso();
        ingresso.setEventoId(evento.getId());
        ingresso.setQuantidade(1);
        ingresso.setValor(50.0);
        ingresso = ingressoRepository.save(ingresso);
        
        Long ingressoId = ingresso.getId();
        
        int totalThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);
        CountDownLatch latch = new CountDownLatch(totalThreads);
        
        AtomicInteger comprasComSucesso = new AtomicInteger(0);
        AtomicInteger falhasPorLockOtimista = new AtomicInteger(0);

        for (int i = 0; i < totalThreads; i++) {
            executor.execute(() -> {
                try {
                    ingressoService.comprarIngresso(ingressoId, 1, compradorId);
                    comprasComSucesso.incrementAndGet();
                } catch (Exception e) {
                        if (e instanceof ObjectOptimisticLockingFailureException
                            || e.getCause() instanceof ObjectOptimisticLockingFailureException
                            || e instanceof CannotAcquireLockException
                            || e instanceof DeadlockLoserDataAccessException) {
                        falhasPorLockOtimista.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, comprasComSucesso.get());
        assertTrue(falhasPorLockOtimista.get() > 0);
    }
}