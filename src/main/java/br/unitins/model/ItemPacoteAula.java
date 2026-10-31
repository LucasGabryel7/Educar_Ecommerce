package br.unitins.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_pacote_aula")
public class ItemPacoteAula extends DefaultEntity {

    @Column(nullable = false)
    private Integer quantidade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pacote_aulas_id", nullable = false)
    private PacoteAulas pacote;

    @ManyToOne(optional = false)
    @JoinColumn(name = "oferta_aula_id", nullable = false)
    private OfertaAula oferta;

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public PacoteAulas getPacote() {
        return pacote;
    }

    public void setPacote(PacoteAulas pacote) {
        this.pacote = pacote;
    }

    public OfertaAula getOferta() {
        return oferta;
    }

    public void setOferta(OfertaAula oferta) {
        this.oferta = oferta;
    }
}
