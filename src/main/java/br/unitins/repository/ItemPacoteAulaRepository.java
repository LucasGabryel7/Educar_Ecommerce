package br.unitins.repository;

import java.util.List;

import br.unitins.model.ItemPacoteAula;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemPacoteAulaRepository implements PanacheRepository<ItemPacoteAula> {

    public List<ItemPacoteAula> findByPacote(Long pacoteId) {
        return find("pacote.id", pacoteId).list();
    }
}
