package br.unitins.repository;

import java.util.List;

import br.unitins.model.ItemPedido;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemPedidoRepository implements PanacheRepository<ItemPedido> {

    public List<ItemPedido> findByPedido(Long pedidoId) {
        return find("pedido.id", pedidoId).list();
    }
}
