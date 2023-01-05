package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.model.PilaCoin;
import com.alissonbk.pilacoin.model.PilaMinerado;
import com.alissonbk.pilacoin.repository.PilaMineradoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PilaMineradoService {

    private final PilaMineradoRepository repository;

    public PilaMineradoService(PilaMineradoRepository repository) {
        this.repository = repository;
    }

    public void savePilaMinerado(PilaCoin pilaCoin) {
        PilaMinerado pilaMinerado = new PilaMinerado();
        pilaMinerado.setPilaCoinJson(pilaCoin);
        pilaMinerado.setDataAcao(Instant.now());
        pilaMinerado.setNonce(pilaCoin.getNonce());
        pilaMinerado.setChaveCriador(pilaCoin.getChaveCriador());
        this.repository.save(pilaMinerado);
    }
}
