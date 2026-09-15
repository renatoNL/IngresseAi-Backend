package com.ingressos.dto;

public record UsuarioResponseDTO(
    Long id,
    String nomeCompleto,
    String tipo
) {}