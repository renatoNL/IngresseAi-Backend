package com.ingressos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public class EventoRequestDTO {
    @NotBlank
    private String titulo;
    
    @NotBlank
    private String descricao;
    
    @NotBlank
    private String tipoEvento;
    
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataEvento;
    
    @NotNull
    private Integer quantidadeIngressos;
    
    @NotNull
    @PositiveOrZero
    private Double valorIngresso;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public LocalDateTime getDataEvento() { return dataEvento; }
    public void setDataEvento(LocalDateTime dataEvento) { this.dataEvento = dataEvento; }
    public Integer getQuantidadeIngressos() { return quantidadeIngressos; }
    public void setQuantidadeIngressos(Integer quantidadeIngressos) { this.quantidadeIngressos = quantidadeIngressos; }
    public Double getValorIngresso() { return valorIngresso; }
    public void setValorIngresso(Double valorIngresso) { this.valorIngresso = valorIngresso; }
} //[cite: 3]