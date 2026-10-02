package com.ingressos.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ingressos.serialization.SensitiveStringSerializer;

public record UsuarioResponseDTO(
        Long id,
        String nomeCompleto,
        @JsonSerialize(using = SensitiveStringSerializer.class) String cpfCnpj,
        String tipo) {
        public UsuarioResponseDTO(Long id, String nomeCompleto, String tipo) {
                this(id, nomeCompleto, null, tipo);
        }
}