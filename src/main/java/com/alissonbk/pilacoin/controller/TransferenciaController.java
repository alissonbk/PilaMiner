package com.alissonbk.pilacoin.controller;

import com.alissonbk.pilacoin.dto.ChaveDTO;
import com.alissonbk.pilacoin.service.TransferenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transferencia")
public class TransferenciaController {
    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping("transferir/{chave}")
    public ResponseEntity<?> enviarParaChaveDestino(@RequestParam String chave) {
        if (this.transferenciaService.enviarParaChaveDestino(chave)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("validarChave")
    public ResponseEntity<String> validarChave(@RequestBody ChaveDTO chave) {
        final String nomeUsuario = this.transferenciaService.validarChave(chave.getValue());
        if (nomeUsuario != null && nomeUsuario.length() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Usuário encontrado: " + nomeUsuario);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chave inválida!");
    }
}
