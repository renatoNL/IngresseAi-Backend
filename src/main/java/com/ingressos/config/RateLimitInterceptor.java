package com.ingressos.config;

import com.ingressos.exception.LimiteRequisicaoExcedidoException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, RequisicaoInfo> controleRequisicoes = new ConcurrentHashMap<>();
    private static final int LIMITE_REQUISICOES = 5;
    private static final long TEMPO_JANELA_MS = 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ipUsuario = request.getHeader("X-Forwarded-For");
        if (ipUsuario == null || ipUsuario.isEmpty()) {
            ipUsuario = request.getRemoteAddr();
        }
        
        long tempoAtual = System.currentTimeMillis();

        controleRequisicoes.compute(ipUsuario, (key, info) -> {
            if (info == null || (tempoAtual - info.tempoInicio) > TEMPO_JANELA_MS) {
                return new RequisicaoInfo(tempoAtual, new AtomicInteger(1));
            }
            if (info.contador.incrementAndGet() > LIMITE_REQUISICOES) {
                throw new LimiteRequisicaoExcedidoException("Muitas requisições (Rate Limit). Por favor, aguarde.");
            }
            return info;
        });

        return true;
    }

    private static class RequisicaoInfo {
        long tempoInicio;
        AtomicInteger contador;

        RequisicaoInfo(long tempoInicio, AtomicInteger contador) {
            this.tempoInicio = tempoInicio;
            this.contador = contador;
        }
    }
}