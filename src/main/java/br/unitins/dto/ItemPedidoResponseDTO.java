package br.unitins.dto;

import java.math.BigDecimal;

import br.unitins.model.ItemPedido;

public record ItemPedidoResponseDTO(
    Long id,
    Long idOferta,
    String nomeOferta,
    BigDecimal precoUnitario,
    Integer quantidade,
    BigDecimal subtotal
) {

    public static ItemPedidoResponseDTO fromEntity(ItemPedido item) {
        if (item == null) {
            return null;
        }

        return new ItemPedidoResponseDTO(
            item.getId(),
            item.getOferta() != null ? item.getOferta().getId() : null,
            item.getNomeOferta(),
            item.getPrecoUnitario(),
            item.getQuantidade(),
            item.getSubtotal()
        );
    }

    public static ItemPedidoResponseDTO valueOf(ItemPedido item) {
        return fromEntity(item);
    }
}
