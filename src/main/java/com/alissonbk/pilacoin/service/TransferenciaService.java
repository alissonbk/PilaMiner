package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.http.TransferenciaClientHttp;
import com.alissonbk.pilacoin.model.TipoPilaBloco;
import com.alissonbk.pilacoin.model.Transferencia;
import com.alissonbk.pilacoin.repository.TransacaoRepository;
import com.alissonbk.pilacoin.repository.TransferenciaRepository;
import com.alissonbk.pilacoin.util.UtilGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
public class TransferenciaService {
    private final TransacaoRepository transacaoRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaClientHttp http;

    public TransferenciaService(TransacaoRepository transacaoRepository, TransferenciaRepository transferenciaRepository,
                                TransferenciaClientHttp http) {
        this.transacaoRepository = transacaoRepository;
        this.transferenciaRepository = transferenciaRepository;
        this.http = http;
    }

    /**
    * Pega o primeiro pilacoin que encontrar no banco e faz a transferencia
    * */
    @Transactional
    public Boolean enviarParaChaveDestino(String chaveDestino) {
        var transacao = this.transacaoRepository
                .findFirstByTipoPilaBlocoIs(TipoPilaBloco.PILA_COIN);

        var transferencia = new Transferencia();
        transferencia.setChaveUsuarioDestino(chaveDestino);
        transferencia.setChaveUsuarioOrigem(KeyGeneratorService.getPublicKeyString());
        transferencia.setStatus(null); //FIXME
        transferencia.setNoncePila(transacao.getNonce());
        transferencia.setIdBloco(0L); //FIXME
        transferencia.setDataTransacao(Date.from(Instant.now()));
        final String transferenciaJson = UtilGenerators.generateJSON(transferencia);
        transferencia.setAssinatura(UtilGenerators.generateSignature(transferenciaJson));


        return false;
    }

}
