package com.ingressos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
public class IngressoComprado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comprador_id")
    private Long compradorId;

    @Column(name = "ingresso_id")
    private Long ingressoId;

    @Column(name = "quantidade_comprada")
    private Integer quantidadeComprada;

    @Column(name = "data_compra", insertable = false, updatable = false)
    private LocalDateTime dataCompra;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCompradorId() { return compradorId; }
    public void setCompradorId(Long compradorId) { this.compradorId = compradorId; }

    public Long getIngressoId() { return ingressoId; }
    public void setIngressoId(Long ingressoId) { this.ingressoId = ingressoId; }

    public Integer getQuantidadeComprada() { return quantidadeComprada; }
    public void setQuantidadeComprada(Integer quantidadeComprada) { this.quantidadeComprada = quantidadeComprada; }

    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
}