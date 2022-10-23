package dev.alissonbk.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PilaCoin implements Serializable {

    private String idCriador;
    private Date dataCriacao;
    private byte[] chaveCriador;
    private byte[] assinaturaMaster;
    private BigInteger magicNumber; //utilizar precisão de 128 bits

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PilaCoin pilaCoin = (PilaCoin) o;

        if (!dataCriacao.equals(pilaCoin.dataCriacao)) return false;
        return magicNumber.equals(pilaCoin.magicNumber);
    }

    @Override
    public int hashCode() {
        int result = dataCriacao.hashCode();
        result = 31 * result + magicNumber.hashCode();
        return result;
    }
}
