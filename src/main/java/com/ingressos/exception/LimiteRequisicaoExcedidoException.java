package com.ingressos.exception;

public class LimiteRequisicaoExcedidoException extends RuntimeException {
    public LimiteRequisicaoExcedidoException(String mensagem) { 
        super(mensagem); 
    }
}