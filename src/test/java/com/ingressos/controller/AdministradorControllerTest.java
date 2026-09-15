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
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "1", roles = "VENDEDOR")
    void deveBloquearCriacaoDeEventoComValorNegativo() throws Exception {
        String payload = "{\"titulo\":\"Festival\",\"descricao\":\"Musica\",\"tipoEvento\":\"FESTIVAL\",\"dataEvento\":\"2026-12-20T20:00:00\",\"quantidadeIngressos\":100,\"valorIngresso\":-50.00}";
        mockMvc.perform(post("/admin/eventos")
                .header("X-Forwarded-For", "ip-teste-admin-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valorIngresso").exists());
    }

    @Test
    @WithMockUser(username = "1", roles = "VENDEDOR")
    void deveBloquearCriacaoDeEventoSemData() throws Exception {
        String payload = "{\"titulo\":\"Palestra\",\"descricao\":\"Tech\",\"tipoEvento\":\"TEATRO\",\"quantidadeIngressos\":50,\"valorIngresso\":0.00}";
        mockMvc.perform(post("/admin/eventos")
                .header("X-Forwarded-For", "ip-teste-admin-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dataEvento").exists());
    }
}