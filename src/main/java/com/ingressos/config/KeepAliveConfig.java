package com.seuprojeto.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class KeepAliveConfig {

    private final String RENDER_URL = "COLE_AQUI_A_URL_DO_SEU_BACKEND_NO_RENDER/ping";

    @Scheduled(fixedRate = 600000) 
    public void keepAlive() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(RENDER_URL, String.class);
        } catch (Exception e) {
            System.out.println("Erro no ping: " + e.getMessage());
        }
    }
}

@RestController
class PingController {
    @GetMapping("/ping")
    public String ping() {
        return "Ativo";
    }
}