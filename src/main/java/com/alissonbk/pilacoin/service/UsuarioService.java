package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public boolean saveUser(Usuario user) {
        return this.usuarioRepository.save(user).equals(new Usuario());
    }
}
