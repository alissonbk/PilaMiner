package com.alissonbk.pilacoin.repository;

import com.alissonbk.pilacoin.model.StatusTransferencia;
import com.alissonbk.pilacoin.model.TipoPilaBloco;
import com.alissonbk.pilacoin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    /**
     * Total de pilacoins minerados
     * */
    Long countDistinctByIdGreaterThanEqual(Long number);

    /**
     * Total de pilacoins com {@link StatusTransferencia}
     * */
    Long countDistinctByIdGreaterThanEqualAndStatusTransferenciaIs(Long n, StatusTransferencia s);

    /**
     * Pega o primeiro pila ou bloco {@link TipoPilaBloco}
     * Com status Transferencia LIVRE {@link StatusTransferencia}
     * */
    Transacao findFirstByTipoPilaBlocoIsAndStatusTransferenciaIs(TipoPilaBloco tipoPilaBloco,
                                                                  StatusTransferencia statusTransferencia);
}
