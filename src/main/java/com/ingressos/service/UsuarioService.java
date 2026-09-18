package com.ingressos.service;

import com.ingressos.dto.UsuarioRequestDTO;
import com.ingressos.exception.RegraNegocioException;
import com.ingressos.model.Usuario;
import com.ingressos.repository.UsuarioRepository;
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

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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

        if (usuarioRepository.findByCpfCnpj(cpfCnpj).isPresent()) {
            throw new RegraNegocioException("CPF/CNPJ já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dto.getNomeCompleto());
        usuario.setCpfCnpj(cpfCnpj);
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setTipo(dto.getTipo());

        return usuarioRepository.save(usuario);
    }

    public String autenticar(String cpfCnpj, String senha) {
        Usuario usuario = usuarioRepository.findByCpfCnpj(normalizarCpfCnpj(cpfCnpj))
                .orElseThrow(() -> new RegraNegocioException("Credenciais inválidas."));

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
}