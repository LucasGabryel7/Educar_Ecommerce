package br.unitins.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaHabilitacaoDTO(

    @NotBlank(message = "O nome da categoria é obrigatório.")
    @Size(min = 1, max = 5, message = "O nome deve ter entre 1 e 5 caracteres (ex: A, B, AB, C, D, E).")
    String nome,

    @Size(max = 300, message = "A descrição deve ter no máximo 300 caracteres.")
    String descricao

) {}
