package com.ingressos.service;

import com.ingressos.dto.UsuarioRequestDTO;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Usuario;
import com.ingressos.repository.UsuarioRepository;
import com.ingressos.security.SensitiveDataCrypto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SensitiveDataCrypto sensitiveDataCrypto;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          SensitiveDataCrypto sensitiveDataCrypto) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.sensitiveDataCrypto = sensitiveDataCrypto;
    }

    public Usuario cadastrarUsuario(UsuarioRequestDTO dto) {
        String cpfCnpj = normalizarCpfCnpj(dto.getCpfCnpj());
        if (!"COMPRADOR".equals(dto.getTipo()) && !"VENDEDOR".equals(dto.getTipo())) {
            throw new RegraNegocioException("Tipo de usuário não reconhecido.");
        }
        if ("VENDEDOR".equals(dto.getTipo()) && (cpfCnpj == null || !cpfCnpj.matches("\\d{14}") || cpfCnpj.matches("(\\d)\\1{13}"))) {
            throw new RegraNegocioException("CNPJ inválido.");
        }
        if ("COMPRADOR".equals(dto.getTipo()) && (cpfCnpj == null || !cpfCnpj.matches("\\d{11}") || cpfCnpj.matches("(\\d)\\1{10}"))) {
            throw new RegraNegocioException("CPF inválido.");
        }

        if (buscarPorDocumento(cpfCnpj).isPresent()) {
            throw new RegraNegocioException("CPF/CNPJ já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setCpfCnpj(cpfCnpj);
        usuario.setCpfCnpjIndice(sensitiveDataCrypto.index(cpfCnpj));
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTipo(dto.getTipo());

        return usuarioRepository.save(usuario);
    }

    public String autenticar(String cpfCnpj, String senha) {
        String documentoNormalizado = normalizarCpfCnpj(cpfCnpj);
        Usuario usuario = buscarPorDocumento(documentoNormalizado)
                .orElseThrow(() -> new RegraNegocioException("Credenciais inválidas."));

        if (usuario.getCpfCnpjIndice() == null) {
            usuario.setCpfCnpjIndice(sensitiveDataCrypto.index(documentoNormalizado));
            usuarioRepository.save(usuario);
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RegraNegocioException("Credenciais inválidas.");
        }

        String role = usuario.getTipo().startsWith("ROLE_") ? usuario.getTipo() : "ROLE_" + usuario.getTipo();

        return Jwts.builder()
                .setSubject(usuario.getId().toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private String normalizarCpfCnpj(String cpfCnpj) {
        return cpfCnpj == null ? null : cpfCnpj.replaceAll("\\D", "");
    }

    private java.util.Optional<Usuario> buscarPorDocumento(String documentoNormalizado) {
        String indice = sensitiveDataCrypto.index(documentoNormalizado);
        java.util.Optional<Usuario> usuario = usuarioRepository.findByCpfCnpjIndice(indice);
        if (usuario.isPresent()) {
            return usuario;
        }

        return usuarioRepository.findAll().stream()
                .filter(candidato -> documentoNormalizado.equals(normalizarCpfCnpj(candidato.getCpfCnpj())))
                .findFirst();
    }
}