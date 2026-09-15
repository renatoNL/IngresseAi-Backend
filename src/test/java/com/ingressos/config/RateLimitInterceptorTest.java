package com.ingressos.config;

import com.ingressos.exception.LimiteRequisicaoExcedidoException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateLimitInterceptorTest {

    @Test
    void deveBloquearSextaRequisicaoNoMesmoSegundo() throws Exception {
        RateLimitInterceptor interceptor = new RateLimitInterceptor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("teste-rate-limit");

        for (int requisicao = 1; requisicao <= 5; requisicao++) {
            interceptor.preHandle(request, response, new Object());
        }

        assertThrows(LimiteRequisicaoExcedidoException.class,
                () -> interceptor.preHandle(request, response, new Object()));
    }
}
