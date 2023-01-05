package com.alissonbk.pilacoin.repository;

import com.alissonbk.pilacoin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
