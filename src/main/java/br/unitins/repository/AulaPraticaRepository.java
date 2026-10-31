package br.unitins.repository;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.model.AulaPratica;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AulaPraticaRepository implements PanacheRepository<AulaPratica> {

    public List<AulaPratica> findByNome(String nome) {
        return find("upper(nome) LIKE upper(?1)", "%" + nome + "%").list();
    }

    public List<AulaPratica> findByCategoria(Long idCategoria) {
        return find("categoriaHabilitacao.id = ?1", idCategoria).list();
    }

    public List<AulaPratica> findByFaixaPreco(BigDecimal min, BigDecimal max) {
        return find("preco BETWEEN ?1 AND ?2", min, max).list();
    }
}