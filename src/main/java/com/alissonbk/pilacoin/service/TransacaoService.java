package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.NumPilasDTO;
import com.alissonbk.pilacoin.model.*;
import com.alissonbk.pilacoin.repository.TransacaoRepository;
import com.alissonbk.pilacoin.util.UtilGenerators;
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
        Transacao transacao = new Transacao();
        transacao.setPilaCoinBlocoJson(pilaCoin);
        transacao.setDataAcao(Instant.now());
        transacao.setNonce(pilaCoin.getNonce());
        transacao.setChaveCriador(pilaCoin.getChaveCriador());
        transacao.setTipoPilaBloco(TipoPilaBloco.PILA_COIN);
        transacao.setTipoTransacao(TipoTransacao.MINERACAO);
        transacao.setStatusTransferencia(StatusTransferencia.LIVRE);
        this.repository.save(transacao);
        webSocketServerService.notifyPilaMinerado(UtilGenerators.generateJSON(pilaCoin));
    }

    public NumPilasDTO getNumPilas() {
        var numPilas = new NumPilasDTO();
        numPilas.setValor(this.repository.countDistinctByIdGreaterThanEqual(0L));
        return numPilas;
    }
}
