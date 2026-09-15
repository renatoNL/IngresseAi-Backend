package com.ingressos.controller;

import com.ingressos.service.EventoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.username=user_ingressos",
    "spring.datasource.password=senha_forte_123",
    "jwt.secret=chave-de-teste-com-tamanho-suficiente-123456"
})
@AutoConfigureMockMvc
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventoService eventoService;

    @Test
    void devePermitirAcessoPublicoAosEventosSemAutenticacao() throws Exception {
        when(eventoService.listarOfertas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/eventos")
                .header("X-Forwarded-For", "ip-teste-evento-1"))
                .andExpect(status().isOk()); 
    }
}