package com.alissonbk.pilacoin.security;


import com.alissonbk.pilacoin.configuration.ApiConfiguration;
import com.alissonbk.pilacoin.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final long duration;
    private final Key key;

    @Autowired
    public JwtTokenProvider(ApiConfiguration config) {
        final var jwtConfig = config.getSecurity().getJwt();

        this.duration = jwtConfig.getDuration().toMillis();
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey()
                .getBytes(StandardCharsets.UTF_8));
    }

    @NonNull
    public String createToken(@NonNull Usuario u) {
        final var claims = Jwts.claims().setSubject(u.getEmail());
        claims.put("userId", u.getId());

        final var now = new Date();

        claims.setIssuedAt(now);
        claims.setExpiration(new Date(now.getTime() + duration));

        return Jwts.builder()
                .signWith(this.key)
                .setClaims(claims)
                .compact();
    }

    public Claims parseToken(@NonNull String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
