package com.alissonbk.pilacoin.dto;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@JsonPropertyOrder(alphabetic=true)
public class ValidaCoinSendDTO implements Serializable {
    private String assinatura;
    private String chavePublica;
    private byte[] hashPilaBloco;
    private String nonce;
    private String tipo = "PILA";


    public ValidaCoinSendDTO() { }

}
