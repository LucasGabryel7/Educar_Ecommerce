package br.unitins.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacote_aulas")
public class PacoteAulas extends OfertaAula {

    /**
     * Coluna mantida para compatibilidade com o esquema existente da tabela pacote_aulas no PostgreSQL.
     * O valor é sincronizado automaticamente com a soma das quantidades dos itens antes de persistir/atualizar.
     */
    @Column(name = "quantidadeaulas", nullable = false)
    private Integer quantidadeAulas;

    @OneToMany(mappedBy = "pacote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPacoteAula> itens = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void sincronizarQuantidadeAulas() {
        this.quantidadeAulas = getQuantidadeAulas();
    }

    public Integer getQuantidadeAulas() {
        if (itens != null && !itens.isEmpty()) {
            return itens.stream()
                    .mapToInt(item -> item.getQuantidade() != null ? item.getQuantidade() : 0)
                    .sum();
        }
        return quantidadeAulas != null ? quantidadeAulas : 0;
    }

    public void setQuantidadeAulas(Integer quantidadeAulas) {
        this.quantidadeAulas = quantidadeAulas;
    }

    public List<ItemPacoteAula> getItens() {
        return itens;
    }

    public void setItens(List<ItemPacoteAula> itens) {
        this.itens = itens != null ? itens : new ArrayList<>();
        if (this.itens != null) {
            for (ItemPacoteAula item : this.itens) {
                item.setPacote(this);
            }
        }
    }

    public void adicionarItem(ItemPacoteAula item) {
        if (item != null) {
            this.itens.add(item);
            item.setPacote(this);
        }
    }

    public void removerItem(ItemPacoteAula item) {
        if (item != null) {
            this.itens.remove(item);
            item.setPacote(null);
        }
    }
}
