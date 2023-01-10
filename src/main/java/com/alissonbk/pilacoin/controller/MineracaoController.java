package com.alissonbk.pilacoin.controller;

import com.alissonbk.pilacoin.service.MineracaoService;
import com.alissonbk.pilacoin.service.TransacaoService;
import com.alissonbk.pilacoin.service.UsuarioService;
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
    void startMineracaoLoop() throws InterruptedException {
        mineracaoService.startStopLoop(usuarioService.getLoggedUser(), transacaoService);
    }
}
