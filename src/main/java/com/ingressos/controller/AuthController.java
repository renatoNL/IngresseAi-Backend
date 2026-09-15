package com.ingressos.controller;

import com.ingressos.dto.LoginDTO;
import com.ingressos.dto.UsuarioRequestDTO;
import com.ingressos.model.Usuario;
import com.ingressos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Gerenciamento de acesso e registro")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar um novo usuário no sistema")
    public Usuario registrar(@RequestBody @Valid UsuarioRequestDTO dto) {
        return usuarioService.cadastrarUsuario(dto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Autenticar usuário e gerar token JWT")
    public Map<String, String> login(@RequestBody @Valid LoginDTO dto) {
        String token = usuarioService.autenticar(dto.getCpfCnpj(), dto.getSenha());
        
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}