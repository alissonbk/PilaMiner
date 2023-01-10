package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.model.Transacao;
import com.alissonbk.pilacoin.repository.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TransacaoService {

    private final TransacaoRepository repository;
    private final WebSocketServerService webSocketServerService;

    public TransacaoService(TransacaoRepository repository, WebSocketServerService webSocketServerService) {
        this.repository = repository;
        this.webSocketServerService = webSocketServerService;
    }

    public void savePilaMinerado(PilaCoin pilaCoin) {
        webSocketServerService.notifyPilaMinerado(pilaCoin.toString());
        Transacao transacao = new Transacao();
        transacao.setPilaCoinBlocoJson(pilaCoin);
        transacao.setDataAcao(Instant.now());
        transacao.setNonce(pilaCoin.getNonce());
        transacao.setChaveCriador(pilaCoin.getChaveCriador());
        this.repository.save(transacao);
    }
}
