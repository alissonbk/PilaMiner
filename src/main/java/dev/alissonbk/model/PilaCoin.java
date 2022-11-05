package dev.alissonbk.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dev.alissonbk.service.KeyGeneratorService;
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
    private String assinaturaMaster = new KeyGeneratorService().getMasterPublicKey();
    private BigInteger nonce; //utilizar precisão de 128 bits
    private String status = "AG_VALIDACAO";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PilaCoin pilaCoin = (PilaCoin) o;

        if (!dataCriacao.equals(pilaCoin.dataCriacao)) return false;
        return nonce.equals(pilaCoin.nonce);
    }

    @Override
    public int hashCode() {
        int result = dataCriacao.hashCode();
        result = 31 * result + nonce.hashCode();
        return result;
    }

}
