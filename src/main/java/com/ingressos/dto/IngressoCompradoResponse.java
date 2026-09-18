package com.ingressos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record IngressoCompradoResponse(
        Long id,
        Long eventoId,
        String titulo,
        LocalDateTime dataEvento,
        Integer quantidade,
        BigDecimal valorIngresso,
        BigDecimal valorTotal,
        String status) {
}
