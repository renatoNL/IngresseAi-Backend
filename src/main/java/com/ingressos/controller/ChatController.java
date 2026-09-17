package com.ingressos.controller;

import com.ingressos.service.ChatAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Substitua "*" pela URL do seu frontend em produção
public class ChatController {

    private final ChatAiService chatAiService;

    public ChatController(ChatAiService chatAiService) {
        this.chatAiService = chatAiService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> conversar(@RequestBody Map<String, String> request) {
        String mensagem = request.get("mensagem");
        String resposta = chatAiService.responder(mensagem);
        return ResponseEntity.ok(Map.of("resposta", resposta));
    }
}