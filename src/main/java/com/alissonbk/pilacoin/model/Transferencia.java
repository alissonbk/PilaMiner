package com.alissonbk.pilacoin.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@JsonPropertyOrder(alphabetic = true)
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private byte[] assinatura;
    @Column(columnDefinition = "text")
    private String chaveUsuarioDestino;
    @Column(columnDefinition = "text")
    private String chaveUsuarioOrigem;
    private Date dataTransacao;
    private Long idBloco;
    @Column(columnDefinition = "text")
    private String noncePila;
    private String status;

    @JsonIgnore
    @ManyToOne()
    private Usuario usuario;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transferencia that = (Transferencia) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
