package com.ingressos.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class ChatAiService {

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;

    public ChatAiService(ObjectMapper objectMapper,
                         @Value("${chat.ai.api-key:}") String apiKey,
                         @Value("${chat.ai.model:gemini-2.0-flash}") String model) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String responder(String mensagem) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("sua-chave-api") || apiKey.equals("${GEMINI_API_KEY}")) {
            return "O chat está pronto. Configure GEMINI_API_KEY para ativar as respostas da IA.";
        }

        try {
            String corpo = objectMapper.writeValueAsString(Map.of(
                    "contents", new Object[]{Map.of("parts", new Object[]{Map.of("text", mensagem)})}));

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                    .build();

            HttpResponse<String> resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());

            if (resposta.statusCode() >= 400) {
                throw new IllegalStateException("A API de IA retornou HTTP " + resposta.statusCode());
            }

            return extrairTexto(resposta.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("A requisição para a IA foi interrompida.", exception);
        } catch (IOException exception) {
            throw new IllegalStateException("Não foi possível consultar a IA.", exception);
        }
    }

    private String extrairTexto(String corpo) throws IOException {
        JsonNode raiz = objectMapper.readTree(corpo);
        JsonNode texto = raiz.at("/candidates/0/content/parts/0/text");
        if (texto.isMissingNode()) {
            throw new IllegalStateException("A resposta da IA não contém texto.");
        }
        return texto.asText();
    }
}