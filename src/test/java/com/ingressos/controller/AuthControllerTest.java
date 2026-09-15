package com.ingressos.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveBloquearRegistroComSenhaCurta() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .header("X-Forwarded-For", "ip-teste-auth-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nomeCompleto\":\"Teste\",\"cpfCnpj\":\"12345678909\",\"senha\":\"123\",\"tipo\":\"COMPRADOR\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senha").value("A senha deve ter no mínimo 6 caracteres"));
    }

    @Test
    void deveProtegerRotaPrivadaDeUsuariosNaoAutenticados() throws Exception {
        mockMvc.perform(post("/comprador/ingressos/comprar")
                .header("X-Forwarded-For", "ip-teste-auth-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ingressoId\": 1, \"quantidade\": 2}"))
                .andExpect(status().isForbidden());
    }
}