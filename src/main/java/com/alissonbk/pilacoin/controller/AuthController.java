package com.alissonbk.pilacoin.controller;


import com.alissonbk.pilacoin.dto.LoginDTO;
import com.alissonbk.pilacoin.model.ErrorResponse;
import com.alissonbk.pilacoin.model.Usuario;
import com.alissonbk.pilacoin.service.UsuarioService;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthController {
    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/v1/login")
    Usuario login(@RequestBody LoginDTO loginDTO) {
        Usuario u = this.usuarioService.login(loginDTO);
        u.setChavePrivada(null);
        u.setChavePrivadaBytes(null);
        return u;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleBadCredentials(BadCredentialsException ex) {
        return new ErrorResponse(List.of(ex.getMessage()));
    }
}
