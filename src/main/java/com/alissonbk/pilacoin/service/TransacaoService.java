package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.model.Transacao;
import com.alissonbk.pilacoin.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;

    public TransacaoService(TransacaoRepository repository) {
        this.repository = repository;
    }

    public void savePilaMinerado(PilaCoin pilaCoin) {
        Transacao transacao = new Transacao();
        transacao.setPilaCoinBlocoJson(pilaCoin);
        transacao.setDataAcao(Instant.now());
        transacao.setNonce(pilaCoin.getNonce());
        transacao.setChaveCriador(pilaCoin.getChaveCriador());
        this.repository.save(transacao);
    }
}
