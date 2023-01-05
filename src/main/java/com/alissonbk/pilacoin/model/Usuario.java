package com.alissonbk.pilacoin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public static final String NOME = "Alisson Billig Kroth";
    //public int id = null;

    @NotNull
    private String nome;

    @NotNull
    @Column(columnDefinition = "text")
    private String chavePublica;
    private byte[] chavePublicaBytes;

    @NotNull
    @Column(columnDefinition = "text")
    private String chavePrivada;
    private byte[] chavePrivadaBytes;
}
