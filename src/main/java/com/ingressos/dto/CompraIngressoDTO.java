package com.ingressos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CompraIngressoDTO(
    @NotNull(message = "O ID do ingresso é obrigatório.")
    Long ingressoId,

    @NotNull(message = "A quantidade de ingressos é obrigatória.")
    @Min(value = 1, message = "A quantidade mínima é 1 ingresso.")
    @Max(value = 4, message = "O limite máximo por usuário é de 4 ingressos.")
    Integer quantidade
) {}