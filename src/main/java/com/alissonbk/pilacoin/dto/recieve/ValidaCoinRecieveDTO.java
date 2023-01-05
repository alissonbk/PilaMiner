package com.alissonbk.pilacoin.dto.recieve;


import com.alissonbk.pilacoin.model.PilaCoin;
import lombok.*;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidaCoinRecieveDTO {
    private String assinaturaMaster;
    private String chaveCriador;
    private Date dataCriacao;
    private String nonce;


    public PilaCoin toPilaCoin() {
        final var pilaCoin = new PilaCoin();
        pilaCoin.setDataCriacao(this.dataCriacao);
        pilaCoin.setNonce(this.nonce);
        pilaCoin.setChaveCriador(this.chaveCriador);
        return pilaCoin;
    }
}
