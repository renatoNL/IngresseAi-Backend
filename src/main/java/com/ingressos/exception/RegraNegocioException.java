package com.ingressos.exception;

public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) { 
        super(mensagem); 
    }
}