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

class LoginDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void devePassarComDadosValidos() {
        LoginDTO dto = new LoginDTO();
        dto.setCpfCnpj("12345678909");
        dto.setSenha("senha123");
        Set<ConstraintViolation<LoginDTO>> violacoes = validator.validate(dto);
        assertTrue(violacoes.isEmpty());
    }

    @Test
    void deveFalharSeCpfCnpjEstiverEmBranco() {
        LoginDTO dto = new LoginDTO();
        dto.setCpfCnpj("");
        dto.setSenha("senha123");
        Set<ConstraintViolation<LoginDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }

    @Test
    void deveFalharSeSenhaEstiverEmBranco() {
        LoginDTO dto = new LoginDTO();
        dto.setCpfCnpj("12345678909");
        dto.setSenha("");
        Set<ConstraintViolation<LoginDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }
}