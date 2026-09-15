package com.ingressos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.username=user_ingressos",
    "spring.datasource.password=senha_forte_123",
    "jwt.secret=chave-de-teste-com-tamanho-suficiente-123456"
})
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "1", roles = "COMPRADOR")
    void deveBloquearMensagemVaziaParaAIAfimDeEconomizarRequisicoes() throws Exception {
        mockMvc.perform(post("/chat")
                .header("X-Forwarded-For", "ip-teste-chat-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\": \"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A mensagem é obrigatória."));
    }

    @Test
    @WithMockUser(username = "1", roles = "COMPRADOR")
    void deveBloquearMensagemExtensaParaPrevenirSobrecargaDeToken() throws Exception {
        String mensagemGigante = "a".repeat(1001);
        mockMvc.perform(post("/chat")
                .header("X-Forwarded-For", "ip-teste-chat-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"mensagem\": \"" + mensagemGigante + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A mensagem deve ter no máximo 1000 caracteres."));
    }
}