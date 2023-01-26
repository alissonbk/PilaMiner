package com.alissonbk.pilacoin.model;

import com.alissonbk.pilacoin.dto.TransacaoBlocoDTO;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Bloco {
    private long numeroBloco;
    private List<TransacaoBlocoDTO> transacoes;
}
