package com.ingressos.security;

import com.ingressos.AbstractIntegrationTest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
    "spring.datasource.username=user_ingressos",
    "spring.datasource.password=senha_forte_123",
    "jwt.secret=chave-de-teste-com-tamanho-suficiente-123456"
})
@AutoConfigureMockMvc
class SecurityEdgeCasesTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void deveRejeitarJwtComAssinaturaInvalida() throws Exception {
        String tokenFraude = Jwts.builder()
                .setSubject("1")
                .claim("role", "ROLE_COMPRADOR")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor("chave-falsa-com-tamanho-suficiente-123456".getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/comprador/ingressos")
                .header("Authorization", "Bearer " + tokenFraude))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRejeitarJwtExpirado() throws Exception {
        String tokenExpirado = Jwts.builder()
                .setSubject("1")
                .claim("role", "ROLE_COMPRADOR")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        mockMvc.perform(get("/comprador/ingressos")
                .header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void deveRetornarErroCustomizadoSemVazarStackTraceEmRotaInexistente() throws Exception {
        mockMvc.perform(get("/endpoint-que-nao-existe")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.trace").doesNotExist());
    }
}