package br.unitins.repository;

import java.util.List;

import br.unitins.model.CategoriaHabilitacao;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoriaHabilitacaoRepository implements PanacheRepository<CategoriaHabilitacao> {

    public List<CategoriaHabilitacao> findByNome(String nome) {
        return find("upper(nome) LIKE upper(?1)", "%" + nome + "%").list();
    }
}
