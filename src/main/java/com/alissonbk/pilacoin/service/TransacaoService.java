package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.dto.NumPilasDTO;
import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.model.TipoPilaBloco;
import com.alissonbk.pilacoin.model.TipoTransacao;
import com.alissonbk.pilacoin.model.Transacao;
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
        webSocketServerService.notifyPilaMinerado(UtilGenerators.generateJSON(pilaCoin));
        Transacao transacao = new Transacao();
        transacao.setPilaCoinBlocoJson(pilaCoin);
        transacao.setDataAcao(Instant.now());
        transacao.setNonce(pilaCoin.getNonce());
        transacao.setChaveCriador(pilaCoin.getChaveCriador());
        transacao.setTipoPilaBloco(TipoPilaBloco.PILA_COIN);
        transacao.setTipoTransacao(TipoTransacao.MINERACAO);
        this.repository.save(transacao);
    }

    public NumPilasDTO getNumPilas() {
        var numPilas = new NumPilasDTO();
        numPilas.setValor(this.repository.countDistinctByIdGreaterThanEqual(0L));
        return numPilas;
    }
}
