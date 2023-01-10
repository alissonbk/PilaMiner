package com.alissonbk.pilacoin.repository;

import com.alissonbk.pilacoin.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PilaMineradoRepository extends JpaRepository<Transacao, Long> {
}
