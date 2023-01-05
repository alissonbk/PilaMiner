package com.alissonbk.pilacoin.dto.send;


import lombok.*;

import java.io.Serializable;

@Data
public class ValidaCoinSendDTO implements Serializable {
    private String assinatura;
    private String chavePublica;
    private String hashPilaBloco;
    private String nonce;
    private String tipo = "PILA";
}
