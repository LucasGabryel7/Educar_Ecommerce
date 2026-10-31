package br.unitins.repository;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.model.AulaTeorica;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AulaTeoricaRepository implements PanacheRepository<AulaTeorica> {

    public List<AulaTeorica> findByNome(String nome) {
        return find("upper(nome) LIKE upper(?1)", "%" + nome + "%").list();
    }

    public List<AulaTeorica> findByFaixaPreco(BigDecimal min, BigDecimal max) {
        return find("preco BETWEEN ?1 AND ?2", min, max).list();
    }
}