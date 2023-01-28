package com.alissonbk.pilacoin;

import com.alissonbk.pilacoin.model.Transferencia;
import com.alissonbk.pilacoin.util.UtilGenerators;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;



public class GeneratorsTest {
    @Test
    public void testSignatureGeneration() {
        var transferencia = new Transferencia();
        transferencia.setStatus("fodase");
        transferencia.setNoncePila("123214534124135446457651243");
        transferencia.setChaveUsuarioOrigem("asdjasodjiosadjoiasjdosajiodjiosadjoisajdoasiodj");
        transferencia.setChaveUsuarioDestino("99923sdsdjasodjiosadjoiasjdosajiodjiosadjoisajdoasiodj");
        final String json = UtilGenerators.generateJSON(transferencia);
        final byte[] signature = UtilGenerators.generateSignature(json);
        transferencia.setAssinatura(signature);
        final String finalJson = UtilGenerators.generateJSON(transferencia);
        System.out.println(finalJson);
        assert finalJson.contains(Base64.encodeBase64String(signature));
    }


}
