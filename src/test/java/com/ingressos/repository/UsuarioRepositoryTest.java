package com.ingressos.repository;

import com.ingressos.AbstractIntegrationTest;
import com.ingressos.model.Usuario;
import com.ingressos.security.SensitiveDataCrypto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SensitiveDataCrypto.class)
class UsuarioRepositoryTest extends AbstractIntegrationTest {
    private static final String TEST_KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SensitiveDataCrypto sensitiveDataCrypto;

    @Test
    void deveEncontrarUsuarioPorCpfCnpj() {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Usuario Teste");
        usuario.setCpfCnpj("12345678909");
        usuario.setCpfCnpjIndice(sensitiveDataCrypto.index("12345678909"));
        usuario.setSenha("senha123");
        usuario.setTipo("COMPRADOR");
        entityManager.persist(usuario);
        entityManager.flush();

        Optional<Usuario> resultado = usuarioRepository.findByCpfCnpjIndice(sensitiveDataCrypto.index("12345678909"));

        assertTrue(resultado.isPresent());
        assertEquals("Usuario Teste", resultado.get().getNomeCompleto());
    }

    @Test
    void naoDeveEncontrarUsuarioComCpfCnpjInexistente() {
        Optional<Usuario> resultado = usuarioRepository.findByCpfCnpjIndice(sensitiveDataCrypto.index("00000000000"));
        assertFalse(resultado.isPresent());
    }
}