package com.alissonbk.pilacoin.dto;

import lombok.Data;

import java.util.List;

@Data
public class BlocoDTO {
    private String chaveUsuarioMinerador;
    private String nonce;
    private String nonceBlocoAnterior;
    private Long numeroBloco;
    private List<TransacaoBlocoDTO> transacoes;
}
