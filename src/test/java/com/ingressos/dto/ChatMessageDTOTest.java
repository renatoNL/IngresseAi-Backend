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

class ChatMessageDTOTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void devePassarComMensagemValida() {
        ChatMessageDTO dto = new ChatMessageDTO("Olá, quais são os próximos eventos?");
        Set<ConstraintViolation<ChatMessageDTO>> violacoes = validator.validate(dto);
        assertTrue(violacoes.isEmpty());
    }

    @Test
    void deveFalharComMensagemEmBranco() {
        ChatMessageDTO dto = new ChatMessageDTO("");
        Set<ConstraintViolation<ChatMessageDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }

    @Test
    void deveFalharComMensagemMuitoGrande() {
        String mensagemGigante = "a".repeat(1001);
        ChatMessageDTO dto = new ChatMessageDTO(mensagemGigante);
        Set<ConstraintViolation<ChatMessageDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }
}