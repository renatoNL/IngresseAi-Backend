package com.ingressos.service;

import com.ingressos.model.Usuario;
import com.ingressos.repository.UsuarioRepository;
import com.ingressos.security.SensitiveDataCrypto;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioSensitiveDataMigration implements ApplicationRunner {
    private final UsuarioRepository usuarioRepository;
    private final SensitiveDataCrypto sensitiveDataCrypto;

    public UsuarioSensitiveDataMigration(UsuarioRepository usuarioRepository,
                                         SensitiveDataCrypto sensitiveDataCrypto) {
        this.usuarioRepository = usuarioRepository;
        this.sensitiveDataCrypto = sensitiveDataCrypto;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Usuario> usuarios;
        do {
            usuarios = usuarioRepository.findFirst100ByCpfCnpjIndiceIsNullAndCpfCnpjIsNotNullOrderByIdAsc();
            for (Usuario usuario : usuarios) {
                String documento = usuario.getCpfCnpj().replaceAll("\\D", "");
                usuario.setCpfCnpjIndice(sensitiveDataCrypto.index(documento));
            }
            if (!usuarios.isEmpty()) {
                usuarioRepository.saveAll(usuarios);
            }
        } while (!usuarios.isEmpty());
    }
}