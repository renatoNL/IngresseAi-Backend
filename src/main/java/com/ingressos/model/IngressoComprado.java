package com.ingressos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

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

    @Column(name = "valor_unitario", precision = 12, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "status")
    private String status = "CONFIRMADA";

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

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
}