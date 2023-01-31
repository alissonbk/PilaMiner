package com.alissonbk.pilacoin.controller;

import com.alissonbk.pilacoin.service.MineracaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/mineracao")
public class MineracaoController {
    private final MineracaoService mineracaoService;

    public MineracaoController(MineracaoService mineracaoService) {
        this.mineracaoService = mineracaoService;
    }

    @PostMapping("/startStopLoop")
    ResponseEntity<Void> startMineracaoLoop() throws InterruptedException {
        try {
            mineracaoService.startStopLoop();
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
