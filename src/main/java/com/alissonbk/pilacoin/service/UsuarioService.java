package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.LoginDTO;
import com.alissonbk.pilacoin.model.LoginLog;
import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.repository.LoginLogRepository;
import com.alissonbk.pilacoin.repository.UsuarioRepository;
import com.alissonbk.pilacoin.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final LoginLogRepository loginLogRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    public static Usuario USUARIO_LOGADO;


    public UsuarioService(UsuarioRepository usuarioRepository,
                          LoginLogRepository loginLogRepository, AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.loginLogRepository = loginLogRepository;
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
                    if (bcrypt.matches(credentials.getPassword(), u.getPassword())) {
                        final var authToken = new UsernamePasswordAuthenticationToken(
                                u.getEmail(),
                                credentials.getPassword()
                        );
                        final var auth = this.authenticationManager.authenticate(authToken);
                        SecurityContextHolder.clearContext(); //limpa o context da sessão
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        u.setAccessToken(this.jwtTokenProvider.createToken(u));
                        this.saveLogs(u);
                        USUARIO_LOGADO = u;
                        return u;
                    }
                    throw new SecurityException("Falha ao autenticar usuario");
                })
                .orElseThrow(() -> new SecurityException("Falha ao autenticar usuario"));
    }

    public boolean verifyUserExistsOnDB(Usuario u) {
        return usuarioRepository.findByEmailIgnoreCase(u.getEmail()).isPresent();
    }

    public Usuario getLoggedUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            return this.usuarioRepository.findUsuarioByEmail(username);
        } else {
            String username = principal.toString();
            return this.usuarioRepository.findUsuarioByEmail(username);
        }
    }

    private void saveLogs(Usuario u) {
        var log = new LoginLog();
        log.setUsuario(u);
        log.setDataHora(Instant.now());
        loginLogRepository.save(log);
    }
}

