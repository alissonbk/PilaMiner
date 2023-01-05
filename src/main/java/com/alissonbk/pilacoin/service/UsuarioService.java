package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public boolean saveUser(Usuario user) {
        return this.usuarioRepository.save(user).getChavePublica().equals(user.getChavePublica());
    }
}
