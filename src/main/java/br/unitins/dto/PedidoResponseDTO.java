package br.unitins.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.unitins.model.Pedido;
import br.unitins.model.StatusPedido;

public record PedidoResponseDTO(
    Long id,
    Long idAluno,
    LocalDateTime dataCriacao,
    BigDecimal valorTotal,
    StatusPedido status,
    List<ItemPedidoResponseDTO> itens
) {

    public static PedidoResponseDTO fromEntity(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        List<ItemPedidoResponseDTO> itensDTO = pedido.getItens() != null
                ? pedido.getItens().stream().map(ItemPedidoResponseDTO::fromEntity).toList()
                : List.of();

        return new PedidoResponseDTO(
            pedido.getId(),
            pedido.getAluno() != null ? pedido.getAluno().getId() : null,
            pedido.getDataCriacao(),
            pedido.getValorTotal(),
            pedido.getStatus(),
            itensDTO
        );
    }

    public static PedidoResponseDTO valueOf(Pedido pedido) {
        return fromEntity(pedido);
    }
}
