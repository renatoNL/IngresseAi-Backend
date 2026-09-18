package com.ingressos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ingressos.exception.IntegracaoIaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;

@Service
public class ChatAiService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;

    public ChatAiService(ObjectMapper objectMapper,
                         @Value("${chat.ai.api-key:}") String apiKey,
                         @Value("${chat.ai.model:gemini-3.6-flash}") String model) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String responder(String mensagem) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("sua-chave-api") || apiKey.equals("${GEMINI_API_KEY}")) {
            throw new IntegracaoIaException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Serviço de IA indisponível: GEMINI_API_KEY não configurada.");
        }

        try {
            String corpo = objectMapper.writeValueAsString(Map.of(
                    "contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", mensagem)})}));

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();

            HttpResponse<String> resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());

            validarResposta(resposta.statusCode());

            return extrairTexto(resposta.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IntegracaoIaException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A requisição para a IA foi interrompida.", exception);
        } catch (HttpTimeoutException exception) {
            throw new IntegracaoIaException(HttpStatus.GATEWAY_TIMEOUT,
                    "A IA demorou demais para responder.", exception);
        } catch (IOException exception) {
            throw new IntegracaoIaException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível conectar ao serviço de IA.", exception);
        }
    }

    private void validarResposta(int statusCode) {
        if (statusCode == 401 || statusCode == 403) {
            throw new IntegracaoIaException(HttpStatus.BAD_GATEWAY,
                    "As credenciais do serviço de IA são inválidas.");
        }
        if (statusCode == 404) {
            throw new IntegracaoIaException(HttpStatus.BAD_GATEWAY,
                    "O modelo configurado da IA não está disponível.");
        }
        if (statusCode == 429) {
            throw new IntegracaoIaException(HttpStatus.TOO_MANY_REQUESTS,
                    "O limite de requisições da IA foi atingido.");
        }
        if (statusCode >= 500) {
            throw new IntegracaoIaException(HttpStatus.SERVICE_UNAVAILABLE,
                    "O serviço de IA está indisponível no momento.");
        }
        if (statusCode >= 400) {
            throw new IntegracaoIaException(HttpStatus.BAD_GATEWAY,
                    "A IA recusou a requisição.");
        }
    }

    private String extrairTexto(String corpo) throws IOException {
        JsonNode raiz = objectMapper.readTree(corpo);
        JsonNode texto = raiz.at("/candidates/0/content/parts/0/text");
        if (texto.isMissingNode()) {
            throw new IntegracaoIaException(HttpStatus.BAD_GATEWAY,
                    "A resposta da IA não contém texto.");
        }
        return texto.asText();
    }
}