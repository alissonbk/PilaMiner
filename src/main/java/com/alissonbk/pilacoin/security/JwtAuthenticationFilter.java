package com.alissonbk.pilacoin.security;


import com.alissonbk.pilacoin.repository.UsuarioRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository userRepo;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   UsuarioRepository userRepo) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    @NotNull HttpServletResponse resp,
                                    FilterChain filterChain) throws IOException, ServletException {
        if (!req.getRequestURI().contains("login")) {
            Optional.ofNullable(req.getHeader(HttpHeaders.AUTHORIZATION))
                    .map(String::trim)
                    .filter(header -> header.startsWith("Bearer "))
                    .map(h -> h.replaceFirst("Bearer ", ""))
                    .map(this.jwtTokenProvider::parseToken)
                    .filter(claims -> claims.getExpiration().after(new Date())) // filtrar token com data expirada
                    .ifPresent(token -> {
                        final var u = this.userRepo.findByEmailIgnoreCase(token.getSubject())
                                .orElseThrow(() -> new IllegalStateException("token criado com usuário inválido"));
                        final var userPrincipal = new UserPrincipal(u);
                        final var auth = new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                userPrincipal.getPassword(),
                                userPrincipal.getAuthorities()
                        );
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });
        }

        filterChain.doFilter(req, resp);
    }
}

