package com.ingressos.service;

import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Usuario;
import com.ingressos.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioServiceAutenticacaoTest {

    private UsuarioRepository repository;
    private PasswordEncoder encoder;
    private UsuarioService service;

    @BeforeEach
    void setup() {
        repository = mock(UsuarioRepository.class);
        encoder = mock(PasswordEncoder.class);
        service = new UsuarioService(repository, encoder);
    }

    @Test
    void deveBloquearAutenticacaoSeCpfCnpjNaoForEncontrado() {
        when(repository.findByCpfCnpj("12345678909")).thenReturn(Optional.empty());
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> service.autenticar("12345678909", "senha123"));
        assertEquals("Credenciais inválidas.", exception.getMessage());
    }

    @Test
    void deveNormalizarMascaraDoCpfAntesDeConsultarUsuario() {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setSenha("senhaCodificada");
        when(repository.findByCpfCnpj("12345678909")).thenReturn(Optional.of(usuarioMock));
        when(encoder.matches("senha123", "senhaCodificada")).thenReturn(false);

        assertThrows(RegraNegocioException.class,
                () -> service.autenticar("123.456.789-09", "senha123"));
    }

    @Test
    void deveBloquearAutenticacaoSeSenhaEstiverIncorreta() {
        Usuario usuarioMock = new Usuario();
        usuarioMock.setSenha("senhaCodificada");
        when(repository.findByCpfCnpj("12345678909")).thenReturn(Optional.of(usuarioMock));
        when(encoder.matches("senhaErrada", "senhaCodificada")).thenReturn(false);

        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> service.autenticar("12345678909", "senhaErrada"));
        assertEquals("Credenciais inválidas.", exception.getMessage());
    }
}