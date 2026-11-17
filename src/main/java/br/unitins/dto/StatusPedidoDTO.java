package br.unitins.dto;

import br.unitins.model.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record StatusPedidoDTO(
    @NotNull(message = "O status do pedido é obrigatório.")
    StatusPedido status
) {}
