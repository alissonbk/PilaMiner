package com.alissonbk.pilacoin.service;

import com.alissonbk.pilacoin.http.BlocoClientHttp;
import com.alissonbk.pilacoin.http.TransferenciaClientHttp;
import com.alissonbk.pilacoin.http.UsuarioClientHttp;
import com.alissonbk.pilacoin.model.Bloco;
import com.alissonbk.pilacoin.model.StatusTransferencia;
import com.alissonbk.pilacoin.model.TipoPilaBloco;
import com.alissonbk.pilacoin.model.Transferencia;
import com.alissonbk.pilacoin.repository.TransacaoRepository;
import com.alissonbk.pilacoin.repository.TransferenciaRepository;
import com.alissonbk.pilacoin.util.UtilGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class TransferenciaService {
    private final TransacaoRepository transacaoRepository;
    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaClientHttp transferenciaClientHttp;
    private final UsuarioService usuarioService;
    private final BlocoClientHttp blocoClientHttp;
    private final UsuarioClientHttp usuarioClientHttp;

    public TransferenciaService(TransacaoRepository transacaoRepository, TransferenciaRepository transferenciaRepository,
                                TransferenciaClientHttp http, UsuarioService usuarioService, BlocoClientHttp blocoClientHttp, UsuarioClientHttp usuarioClientHttp) {
        this.transacaoRepository = transacaoRepository;
        this.transferenciaRepository = transferenciaRepository;
        this.transferenciaClientHttp = http;
        this.usuarioService = usuarioService;
        this.blocoClientHttp = blocoClientHttp;
        this.usuarioClientHttp = usuarioClientHttp;
    }

    /**
    * Pega o primeiro pilacoin que encontrar no banco e faz a transferencia
    * */
    @Transactional
    public Boolean enviarParaChaveDestino(String chaveDestino) {
        var transacao = this.transacaoRepository
                .findFirstByTipoPilaBlocoIsAndStatusTransferenciaIs(TipoPilaBloco.PILA_COIN,
                        StatusTransferencia.LIVRE);
        if (transacao == null) return false;
        List<Bloco> blocos = blocoClientHttp.getAllBlocos();

        var transferencia = new Transferencia();
        transferencia.setChaveUsuarioDestino(chaveDestino);
        transferencia.setChaveUsuarioOrigem(KeyGeneratorService.getPublicKeyString());
        transferencia.setStatus(null); //FIXME
        transferencia.setNoncePila(transacao.getNonce());
        transferencia.setIdBloco(blocos.get(0).getNumeroBloco());
        transferencia.setDataTransacao(Date.from(Instant.now()));
        final String transferenciaJson = UtilGenerators.generateJSON(transferencia);
        transferencia.setAssinatura(UtilGenerators.generateSignature(transferenciaJson));
        final String jsonFinal = UtilGenerators.generateJSON(transferencia);

        if (this.transferenciaClientHttp.transferir(jsonFinal)) {
            transferencia.setUsuario(this.usuarioService.getLoggedUser());
            this.transferenciaRepository.save(transferencia);
            transacao.setStatusTransferencia(StatusTransferencia.TRANSFERIDO);
            this.transacaoRepository.save(transacao);
            return true;
        }
        return false;
    }

    public String validarChave(String chave) {
        return this.usuarioClientHttp.getUsuarioByChave(chave).getNome();
    }

}
