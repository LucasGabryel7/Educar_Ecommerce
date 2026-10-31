package br.unitins.repository;

import java.util.List;
import java.util.Optional;

import br.unitins.model.Veiculo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VeiculoRepository implements PanacheRepository<Veiculo> {

    public Optional<Veiculo> findByPlaca(String placa) {
        return find("upper(placa)", placa.toUpperCase()).firstResultOptional();
    }

    public List<Veiculo> findByAutoescola(Long idAutoescola) {
        return find("autoescola.id", idAutoescola).list();
    }

    public List<Veiculo> findByCategoria(Long idCategoria) {
        return find("categoriaHabilitacao.id", idCategoria).list();
    }

    public List<Veiculo> findByAtivo(Boolean ativo) {
        return find("ativo", ativo).list();
    }
}
