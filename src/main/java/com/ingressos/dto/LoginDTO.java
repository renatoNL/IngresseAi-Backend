package com.ingressos.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    @NotBlank(message = "O CPF/CNPJ é obrigatório")
    private String cpfCnpj;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}