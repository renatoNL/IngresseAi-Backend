package com.ingressos.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordEncoderStrengthTest {
    @Test
    void configuresBcryptWithStrengthTwelve() {
        var encoder = new SecurityConfig(null).passwordEncoder();

        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
        assertTrue(encoder.encode("senha-de-teste").startsWith("$2a$12$"));
    }
}