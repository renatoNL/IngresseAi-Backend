package com.ingressos.exception;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveOcultarMensagensSensiveisDoBancoDeDados() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Detalhes confidenciais do SQL");
        ResponseEntity<Map<String, String>> response = handler.handleDatabaseExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro de integridade no banco de dados: restrição violada (ex: limite de quantidade excedido).", response.getBody().get("erro"));
    }

    @Test
    void deveInformarErroAmigavelParaValorDeTransacaoInvalido() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "violates check constraint \"chk_valor_positivo\"");

        ResponseEntity<Map<String, String>> response = handler.handleDatabaseExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("O valor da transação deve ser maior que zero.", response.getBody().get("erro"));
    }

    @Test
    void deveInformarErroAmigavelParaContasDeTransacaoIguais() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "violates check constraint \"chk_contas_diferentes\"");

        ResponseEntity<Map<String, String>> response = handler.handleDatabaseExceptions(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("A conta de origem não pode ser a mesma que a conta de destino.", response.getBody().get("erro"));
    }
}