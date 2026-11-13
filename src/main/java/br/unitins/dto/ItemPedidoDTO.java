package br.unitins.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoDTO(

    @NotNull(message = "O ID da oferta é obrigatório.")
    @Positive(message = "Informe um ID válido para a oferta.")
    Long idOferta,

    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade do item deve ser no mínimo 1.")
    Integer quantidade

) {}
