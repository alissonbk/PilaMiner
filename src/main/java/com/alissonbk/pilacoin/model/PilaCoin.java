package com.alissonbk.pilacoin.model;

import com.alissonbk.pilacoin.service.KeyGeneratorService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(alphabetic = true)
public class PilaCoin implements Serializable {

    private int id = 0;
    //@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="America/Sao_Paulo")
    private Date dataCriacao;
    private String chaveCriador;
    @JsonIgnore
    private String assinaturaMaster = new KeyGeneratorService().getMasterPublicKey();
    @JsonIgnore
    private BigInteger nonceNumber; //utilizar precisão de 128 bits
    private String nonce;
    @JsonIgnore
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PilaCoin pilaCoin = (PilaCoin) o;

        if (!dataCriacao.equals(pilaCoin.dataCriacao)) return false;
        if (this.nonceNumber == null) return false;
        return nonceNumber.equals(pilaCoin.nonceNumber);
    }

    @Override
    public int hashCode() {
        int result = dataCriacao.hashCode();
        result = 31 * result + nonceNumber.hashCode();
        return result;
    }

}
