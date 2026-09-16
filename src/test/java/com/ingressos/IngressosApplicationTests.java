package com.ingressos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.password=root",
    "chat.ai.api-key="
})
@AutoConfigureMockMvc
class IngressosApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "1", roles = "VENDEDOR")
    void deveCriarEventoComDadosValidos() throws Exception {
        mockMvc.perform(post("/admin/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Festival\",\"descricao\":\"Evento teste\",\"tipoEvento\":\"SHOW\",\"dataEvento\":\"2026-12-20T20:00:00\",\"quantidadeIngressos\":10,\"valorIngresso\":100.00}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descricao").value("Evento teste"));
    }

    @Test
    @WithMockUser(username = "1", roles = "COMPRADOR")
    void deveResponderChatSemChaveConfigurada() throws Exception {
        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mensagem\":\"Quais eventos existem?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resposta").isNotEmpty());
    }
}