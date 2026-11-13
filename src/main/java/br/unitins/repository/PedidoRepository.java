package br.unitins.repository;

import java.util.List;

import br.unitins.model.Pedido;
import br.unitins.model.StatusPedido;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PedidoRepository implements PanacheRepository<Pedido> {

    public List<Pedido> findByAluno(Long alunoId) {
        return find("aluno.id", alunoId).list();
    }

    public List<Pedido> findByStatus(StatusPedido status) {
        return find("status", status).list();
    }
}
