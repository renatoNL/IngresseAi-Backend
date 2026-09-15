package com.ingressos.repository;

import com.ingressos.AbstractIntegrationTest;
import com.ingressos.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsuarioRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deveEncontrarUsuarioPorCpfCnpj() {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Usuario Teste");
        usuario.setCpfCnpj("12345678909");
        usuario.setSenha("senha123");
        usuario.setTipo("COMPRADOR");
        entityManager.persist(usuario);
        entityManager.flush();

        Optional<Usuario> resultado = usuarioRepository.findByCpfCnpj("12345678909");

        assertTrue(resultado.isPresent());
        assertEquals("Usuario Teste", resultado.get().getNomeCompleto());
    }

    @Test
    void naoDeveEncontrarUsuarioComCpfCnpjInexistente() {
        Optional<Usuario> resultado = usuarioRepository.findByCpfCnpj("00000000000");
        assertFalse(resultado.isPresent());
    }
}