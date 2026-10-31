package br.unitins.repository;

import java.util.List;
import java.util.Optional;

import br.unitins.model.Instrutor;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InstrutorRepository implements PanacheRepository<Instrutor> {

    public Optional<Instrutor> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public Optional<Instrutor> findByRegistroProfissional(String registroProfissional) {
        return find("registroProfissional", registroProfissional).firstResultOptional();
    }

    public List<Instrutor> findByNome(String nome) {
        return find("upper(nome) LIKE upper(?1)", "%" + nome + "%").list();
    }

    public List<Instrutor> findByAtivo(Boolean ativo) {
        return find("ativo", ativo).list();
    }

    public List<Instrutor> findByAutoescola(Long idAutoescola) {
        return find("autoescola.id", idAutoescola).list();
    }

    public List<Instrutor> findByCategoria(Long idCategoria) {
        return find("SELECT i FROM Instrutor i JOIN i.categorias c WHERE c.id = ?1", idCategoria).list();
    }
}
