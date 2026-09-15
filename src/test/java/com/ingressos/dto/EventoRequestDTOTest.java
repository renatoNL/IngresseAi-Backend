package com.ingressos.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventoRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void devePassarComDadosValidos() {
        EventoRequestDTO dto = new EventoRequestDTO();
        dto.setTitulo("Festival de Música");
        dto.setDescricao("Evento de teste");
        dto.setTipoEvento("FESTIVAL");
        dto.setDataEvento(LocalDateTime.now().plusDays(30));
        dto.setQuantidadeIngressos(500);
        dto.setValorIngresso(150.0);

        Set<ConstraintViolation<EventoRequestDTO>> violacoes = validator.validate(dto);
        assertTrue(violacoes.isEmpty());
    }

    @Test
    void deveFalharSeValorDoIngressoForNegativo() {
        EventoRequestDTO dto = new EventoRequestDTO();
        dto.setTitulo("Festival de Música");
        dto.setDescricao("Evento de teste");
        dto.setTipoEvento("FESTIVAL");
        dto.setDataEvento(LocalDateTime.now().plusDays(30));
        dto.setQuantidadeIngressos(500);
        dto.setValorIngresso(-10.0);

        Set<ConstraintViolation<EventoRequestDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }

    @Test
    void deveFalharSeTituloEstiverNuloOuEmBranco() {
        EventoRequestDTO dto = new EventoRequestDTO();
        dto.setDescricao("Evento de teste");
        dto.setTipoEvento("FESTIVAL");
        dto.setDataEvento(LocalDateTime.now().plusDays(30));
        dto.setQuantidadeIngressos(500);
        dto.setValorIngresso(150.0);

        Set<ConstraintViolation<EventoRequestDTO>> violacoes = validator.validate(dto);
        assertFalse(violacoes.isEmpty());
    }
}