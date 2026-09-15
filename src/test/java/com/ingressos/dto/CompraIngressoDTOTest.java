package com.ingressos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompraIngressoDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void devePassarComDadosValidos() {
        CompraIngressoDTO dto = new CompraIngressoDTO(1L, 2);
        Set<ConstraintViolation<CompraIngressoDTO>> violacoes = validator.validate(dto);
        assertTrue(violacoes.isEmpty());
    }

    @Test
    void deveFalharSemIngressoId() {
        CompraIngressoDTO dto = new CompraIngressoDTO(null, 2);
        Set<ConstraintViolation<CompraIngressoDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }

    @Test
    void deveFalharComQuantidadeMenorQueUm() {
        CompraIngressoDTO dto = new CompraIngressoDTO(1L, 0);
        Set<ConstraintViolation<CompraIngressoDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }

    @Test
    void deveFalharComQuantidadeMaiorQueQuatro() {
        CompraIngressoDTO dto = new CompraIngressoDTO(1L, 5);
        Set<ConstraintViolation<CompraIngressoDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }
}