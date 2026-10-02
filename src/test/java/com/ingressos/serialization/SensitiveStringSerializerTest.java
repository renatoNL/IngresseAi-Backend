package com.ingressos.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingressos.dto.UsuarioResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SensitiveStringSerializerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void masksCpfAndDoesNotSerializePassword() throws Exception {
        String json = objectMapper.writeValueAsString(
                new UsuarioResponseDTO(1L, "Renato", "123.456.789-09", "COMPRADOR"));

        assertTrue(json.contains("***.***.***-09"));
        assertFalse(json.contains("senha"));
        assertFalse(json.contains("123.456.789-09"));
    }

    @Test
    void masksEmailLocalPart() throws Exception {
        String json = objectMapper.writeValueAsString(
                new UsuarioResponseDTO(1L, "Renato", "renato@email.com", "COMPRADOR"));

        assertTrue(json.contains("r***o@email.com"));
        assertFalse(json.contains("renato@email.com"));
    }
}