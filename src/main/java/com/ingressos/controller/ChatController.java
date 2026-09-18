package com.ingressos.controller;

import com.ingressos.dto.ChatMessageDTO;
import com.ingressos.service.ChatAiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatAiService chatAiService;

    public ChatController(ChatAiService chatAiService) {
        this.chatAiService = chatAiService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> conversar(@Valid @RequestBody ChatMessageDTO request) {
        String resposta = chatAiService.responder(request.mensagem());
        return ResponseEntity.ok(Map.of("resposta", resposta));
    }
}