package com.ingressos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatAiServiceTest {
    private ChatAiService chatAiService;
    private HttpClient httpClientMock;

    @BeforeEach
    void setup() {
        chatAiService = new ChatAiService(new ObjectMapper(), "fake-key", "gemini-2.0-flash");
        httpClientMock = mock(HttpClient.class);
        ReflectionTestUtils.setField(chatAiService, "httpClient", httpClientMock);
    }

    @Test
    void deveLancarExcecaoEmCasoDeQuedaDeInternet() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Sem rede"));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> chatAiService.responder("Ola"));
        assertTrue(exception.getMessage().contains("Não foi possível consultar a IA."));
    }

    @Test
    void deveLancarExcecaoEmCasoDeInternetLentaTimeout() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new HttpTimeoutException("Timeout"));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> chatAiService.responder("Ola"));
        assertTrue(exception.getMessage().contains("Não foi possível consultar a IA."));
    }
}