package com.alissonbk.pilacoin.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TransacaoBlocoDTO {
    private String assinatura;
    private String chaveUsuarioDestino;
    private String chaveUsuarioOrigem;
    private Date dataTransacao;
    private Long id;
    private Long idBloco;
    private String noncePila;
    private String status;
}
