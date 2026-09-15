package com.ingressos.controller;

import com.ingressos.dto.ChatMessageDTO;
import com.ingressos.service.ChatAiService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatAiService chatAiService;

    public ChatController(ChatAiService chatAiService) {
        this.chatAiService = chatAiService;
    }

    @PostMapping
    public Map<String, String> conversar(@RequestBody @Valid ChatMessageDTO dto) {
        return Map.of("resposta", chatAiService.responder(dto.mensagem()));
    }
}