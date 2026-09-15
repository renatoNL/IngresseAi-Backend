package com.ingressos.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ingressos")
public class Ingresso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evento_id")
    private Long eventoId;

    private Integer quantidade;
    private Double valor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
}