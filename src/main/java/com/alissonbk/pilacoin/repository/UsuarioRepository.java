package com.alissonbk.pilacoin.repository;

import com.alissonbk.pilacoin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(@NotNull String email);
}
