package com.alissonbk.pilacoin.controller;

import com.alissonbk.pilacoin.service.MineracaoService;
import com.alissonbk.pilacoin.service.TransacaoService;
import com.alissonbk.pilacoin.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mineracao")
public class MineracaoController {
    private final MineracaoService mineracaoService;
    private final UsuarioService usuarioService;
    private final TransacaoService transacaoService;

    public MineracaoController(MineracaoService mineracaoService, UsuarioService usuarioService, TransacaoService transacaoService) {
        this.mineracaoService = mineracaoService;
        this.usuarioService = usuarioService;
        this.transacaoService = transacaoService;
    }

    @PostMapping("/startStopLoop")
    ResponseEntity<Void> startMineracaoLoop() throws InterruptedException {
        try {
            mineracaoService.startStopLoop(usuarioService.getLoggedUser(), transacaoService);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
