
package com.ingressos.service;

import com.ingressos.dto.UsuarioRequestDTO;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class UsuarioServiceTest {
    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final UsuarioService service = new UsuarioService(repository, encoder);

    @Test
    void deveBloquearCadastroComCpfComTodosDigitosIguais() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNomeCompleto("Usuário Teste");
        dto.setSenha("123456");
        dto.setTipo("COMPRADOR");
        dto.setCpfCnpj("11111111111");

        assertThrows(RegraNegocioException.class, () -> service.cadastrarUsuario(dto));
    }

    @Test
    void deveBloquearCadastroDeVendedorComCnpjInvalido() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNomeCompleto("Empresa Teste");
        dto.setSenha("123456");
        dto.setTipo("VENDEDOR");
        dto.setCpfCnpj("00000000000000");

        assertThrows(RegraNegocioException.class, () -> service.cadastrarUsuario(dto));
    }
    
    @Test
    void deveBloquearCadastroComTipoNaoReconhecido() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNomeCompleto("Invasor");
        dto.setSenha("123456");
        dto.setTipo("ADMIN_SUPREMO"); 
        dto.setCpfCnpj("07342938421");

        assertThrows(RegraNegocioException.class, () -> service.cadastrarUsuario(dto));
    }
}