package br.unitins.repository;

import java.util.List;
import java.util.Optional;

import br.unitins.model.Aluno;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AlunoRepository implements PanacheRepository<Aluno> {

    public Optional<Aluno> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }

    public List<Aluno> findByNome(String nome) {
        return find("upper(nome) LIKE upper(?1)", "%" + nome + "%").list();
    }

    public Optional<Aluno> findByEmail(String email) {
        return find("lower(email)", email.toLowerCase()).firstResultOptional();
    }

    public List<Aluno> findByAtivo(Boolean ativo) {
        return find("ativo", ativo).list();
    }
}
