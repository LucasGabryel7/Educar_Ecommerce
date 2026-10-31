package br.unitins.repository;

import br.unitins.model.OfertaAula;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OfertaAulaRepository implements PanacheRepository<OfertaAula> {
}
