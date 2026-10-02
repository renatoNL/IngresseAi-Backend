package com.ingressos.service;

import com.ingressos.model.Usuario;
import com.ingressos.repository.UsuarioRepository;
import com.ingressos.security.SensitiveDataCrypto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioSensitiveDataMigrationTest {
    private static final String TEST_KEY = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @Test
    void backfillsDocumentIndexAndPersistsLegacyDocument() {
        UsuarioRepository repository = mock(UsuarioRepository.class);
        SensitiveDataCrypto crypto = new SensitiveDataCrypto(TEST_KEY, TEST_KEY);
        UsuarioSensitiveDataMigration migration = new UsuarioSensitiveDataMigration(repository, crypto);
        Usuario usuario = new Usuario();
        usuario.setCpfCnpj("123.456.789-09");
        when(repository.findFirst100ByCpfCnpjIndiceIsNullAndCpfCnpjIsNotNullOrderByIdAsc())
                .thenReturn(List.of(usuario), List.of());

        migration.run(null);

        assertEquals(crypto.index("12345678909"), usuario.getCpfCnpjIndice());
        verify(repository).saveAll(List.of(usuario));
    }
}