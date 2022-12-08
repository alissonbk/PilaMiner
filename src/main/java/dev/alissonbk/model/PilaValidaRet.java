package dev.alissonbk.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.alissonbk.service.KeyGeneratorService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PilaValidaRet {
    private String assinaturaMaster;
    private String chaveCriador;
    private Date dataCriacao;
    private String nonce;
}
