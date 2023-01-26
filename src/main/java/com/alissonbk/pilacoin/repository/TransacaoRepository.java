package com.alissonbk.pilacoin.repository;

import com.alissonbk.pilacoin.model.StatusTransferencia;
import com.alissonbk.pilacoin.model.TipoPilaBloco;
import com.alissonbk.pilacoin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    Long countDistinctByIdGreaterThanEqual(Long number);

    Transacao findFirstByTipoPilaBlocoIsAndStatusTransferenciaIs(TipoPilaBloco tipoPilaBloco,
                                                                  StatusTransferencia statusTransferencia);
}
