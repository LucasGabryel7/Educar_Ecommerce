package br.unitins.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PedidoDTO(

    @NotNull(message = "O ID do aluno é obrigatório.")
    @Positive(message = "Informe um ID válido para o aluno.")
    Long idAluno,

    @NotEmpty(message = "O pedido deve possuir pelo menos um item.")
    List<@Valid ItemPedidoDTO> itens

) {}
