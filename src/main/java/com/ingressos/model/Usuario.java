package com.ingressos.model;

import com.ingressos.security.AesGcmStringConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomeCompleto;
    @Convert(converter = AesGcmStringConverter.class)
    @Column(name = "cpf_cnpj", length = 255)
    private String cpfCnpj;
    @Column(name = "cpf_cnpj_indice", length = 64, unique = true)
    private String cpfCnpjIndice;
    private String senha;
    private String tipo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
    public String getCpfCnpjIndice() { return cpfCnpjIndice; }
    public void setCpfCnpjIndice(String cpfCnpjIndice) { this.cpfCnpjIndice = cpfCnpjIndice; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}