package com.alissonbk.pilacoin.repository;

import com.alissonbk.pilacoin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
}
