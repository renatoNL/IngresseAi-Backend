package com.ingressos.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsuarioResponseDTOTest {
    @Test
    void deveCriarRecordCorretamente() {
        UsuarioResponseDTO dto = new UsuarioResponseDTO(1L, "Nome Teste", "COMPRADOR");
        
        assertEquals(1L, dto.id());
        assertEquals("Nome Teste", dto.nomeCompleto());
        assertEquals("COMPRADOR", dto.tipo());
    }
}