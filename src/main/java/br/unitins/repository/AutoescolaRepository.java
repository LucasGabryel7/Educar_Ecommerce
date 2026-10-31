package br.unitins.repository;

import java.util.List;
import java.util.Optional;

import br.unitins.model.Autoescola;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AutoescolaRepository implements PanacheRepository<Autoescola> {

    public Optional<Autoescola> findByCnpj(String cnpj) {
        return find("cnpj", cnpj).firstResultOptional();
    }

    public List<Autoescola> findByNome(String nome) {
        return find("upper(nome) LIKE upper(?1)", "%" + nome + "%").list();
    }
}
