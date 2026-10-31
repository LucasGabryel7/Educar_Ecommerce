package br.unitins.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "aula_pratica")
public class AulaPratica extends OfertaAula {

    @ManyToOne
    @JoinColumn(name = "categoria_habilitacao_id", nullable = false)
    private CategoriaHabilitacao categoriaHabilitacao;

    public CategoriaHabilitacao getCategoriaHabilitacao() {
        return categoriaHabilitacao;
    }

    public void setCategoriaHabilitacao(CategoriaHabilitacao categoriaHabilitacao) {
        this.categoriaHabilitacao = categoriaHabilitacao;
    }
}