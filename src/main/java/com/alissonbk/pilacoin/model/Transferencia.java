package com.alissonbk.pilacoin.model;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private byte[] assinatura;
    private String chaveUsuarioDestino;
    private String chaveUsuarioOrigem;
    private Date dataTransacao;
    private Long idBloco;
    private String noncePila;
    private String status;

}
