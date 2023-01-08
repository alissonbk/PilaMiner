package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.LoginDTO;
import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.repository.UsuarioRepository;
import com.alissonbk.pilacoin.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    public UsuarioService(UsuarioRepository usuarioRepository,
                          AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public boolean saveUser(Usuario user) {
        return this.usuarioRepository.save(user).getChavePublica().equals(user.getChavePublica());
    }

    @NotNull
    @Transactional
    public Usuario login(@NotNull LoginDTO credentials) {
        final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        return this.usuarioRepository
                .findByEmailIgnoreCase(credentials.getEmail())
                .map(u -> {
                    if (bcrypt.matches(credentials.getPasswd(), u.getPassword())) {
                        final var authToken = new UsernamePasswordAuthenticationToken(
                                u.getEmail(),
                                credentials.getPasswd()
                        );
                        final var auth = this.authenticationManager.authenticate(authToken);
                        SecurityContextHolder.clearContext(); //limpa o context da sessão
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        u.setAccessToken(this.jwtTokenProvider.createToken(u));

                        return u;
                    }
                    throw new SecurityException("Falha ao autenticar usuario");
                })
                .orElseThrow(() -> new SecurityException("Falha ao autenticar usuario"));
    }
}
