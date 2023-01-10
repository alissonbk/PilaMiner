package com.alissonbk.pilacoin.controller;

import com.alissonbk.pilacoin.service.MineracaoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("mineracao")
public class MineracaoController {
    @PostMapping("/startMineracao")
    void startMineracaoLoop() {
        MineracaoService.MINERACAO_IS_RUNNING = true;
    }

    @PostMapping("/endMineracao")
    void endMineracaoLoop() {
        MineracaoService.MINERACAO_IS_RUNNING = false;
    }
}
