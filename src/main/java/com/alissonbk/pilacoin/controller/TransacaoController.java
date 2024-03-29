package com.alissonbk.pilacoin.controller;

import com.alissonbk.pilacoin.dto.ResponseDTO;
import com.alissonbk.pilacoin.service.TransacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transacao")
public class TransacaoController {
    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @GetMapping("get-pilas")
    public ResponseEntity<ResponseDTO<Long>> getNumPilas() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transacaoService.getNumPilas());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("get-pilas-transferiveis")
    public ResponseEntity<ResponseDTO<Long>> getNumPilasTransferiveis() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transacaoService.getNumPilasTransferiveis());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
