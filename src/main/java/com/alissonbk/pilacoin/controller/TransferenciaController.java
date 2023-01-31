package com.alissonbk.pilacoin.controller;

import com.alissonbk.pilacoin.dto.ResponseDTO;
import com.alissonbk.pilacoin.service.TransferenciaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/v1/transferencia")
public class TransferenciaController {
    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping("transferir")
    public ResponseEntity<ResponseDTO<String>> enviarParaChaveDestino(@RequestBody ResponseDTO<String> chave) {
        final String msg = this.transferenciaService.enviarParaChaveDestino(chave.getValor());
        var response = new ResponseDTO<String>();
        response.setValor(msg);
        if (msg.equals("sucesso")) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("validarChave")
    public ResponseEntity<String> validarChave(@RequestBody ResponseDTO<String> chave) {
        final String nomeUsuario = this.transferenciaService.validarChave(chave.getValor());
        if (nomeUsuario != null && nomeUsuario.length() > 0) {
            return ResponseEntity.status(HttpStatus.OK).body("Usuário encontrado: " + nomeUsuario);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chave inválida!");
    }
}
