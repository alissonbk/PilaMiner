package com.alissonbk.pilacoin.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class),
})
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Instant dataAcao;

    @Type(type = "jsonb")
    @Column(columnDefinition = "json")
    private PilaCoin pilaCoinBlocoJson;

    @Column(columnDefinition = "text")
    private String nonce;

    @Column(columnDefinition = "text")
    private String chaveCriador;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPilaBloco tipoPilaBloco;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTransacao tipoTransacao;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusTransferencia statusTransferencia;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Transacao transacao = (Transacao) o;
        return id != null && Objects.equals(id, transacao.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
