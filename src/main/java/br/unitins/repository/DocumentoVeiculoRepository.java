package br.unitins.repository;

import java.util.Optional;

import br.unitins.model.DocumentoVeiculo;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DocumentoVeiculoRepository implements PanacheRepository<DocumentoVeiculo> {

    public Optional<DocumentoVeiculo> findByNumero(String numero) {
        return find("numero", numero).firstResultOptional();
    }

    public Optional<DocumentoVeiculo> findByVeiculo(Long idVeiculo) {
        return find("veiculo.id", idVeiculo).firstResultOptional();
    }
}
