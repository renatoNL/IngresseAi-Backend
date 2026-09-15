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

class UsuarioRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void devePassarComDadosValidos() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNomeCompleto("João da Silva");
        dto.setCpfCnpj("12345678909");
        dto.setSenha("senha123");
        dto.setTipo("COMPRADOR");

        Set<ConstraintViolation<UsuarioRequestDTO>> violacoes = validator.validate(dto);
        assertTrue(violacoes.isEmpty());
    }

    @Test
    void deveFalharSeSenhaForMenorQue6Caracteres() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNomeCompleto("João da Silva");
        dto.setCpfCnpj("12345678909");
        dto.setSenha("12345");
        dto.setTipo("COMPRADOR");

        Set<ConstraintViolation<UsuarioRequestDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }

    @Test
    void deveFalharSeNomeEstiverEmBranco() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNomeCompleto("");
        dto.setCpfCnpj("12345678909");
        dto.setSenha("senha123");
        dto.setTipo("COMPRADOR");

        Set<ConstraintViolation<UsuarioRequestDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }
}