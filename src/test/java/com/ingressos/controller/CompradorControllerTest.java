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
class CompradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "1", roles = "COMPRADOR")
    void deveBloquearCompraMaiorQueQuatroIngressosNaValidacaoDoDTO() throws Exception {
        mockMvc.perform(post("/comprador/ingressos/comprar")
                .header("X-Forwarded-For", "ip-teste-compra-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ingressoId\": 1, \"quantidade\": 5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantidade").value("O limite máximo por usuário é de 4 ingressos."));
    }

    @Test
    @WithMockUser(username = "1", roles = "COMPRADOR")
    void deveBloquearCompraComQuantidadeZero() throws Exception {
        mockMvc.perform(post("/comprador/ingressos/comprar")
                .header("X-Forwarded-For", "ip-teste-compra-2")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ingressoId\": 1, \"quantidade\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantidade").value("A quantidade mínima é 1 ingresso."));
    }
    
    @Test
    @WithMockUser(username = "1", roles = "COMPRADOR")
    void deveBloquearCompraSemInformarOIdDoIngresso() throws Exception {
        mockMvc.perform(post("/comprador/ingressos/comprar")
                .header("X-Forwarded-For", "ip-teste-compra-3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantidade\": 2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ingressoId").value("O ID do ingresso é obrigatório."));
    }
}