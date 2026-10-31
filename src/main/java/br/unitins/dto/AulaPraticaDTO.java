package br.unitins.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AulaPraticaDTO(

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    String nome,

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    String descricao,

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O preço deve ter até 8 dígitos inteiros e 2 decimais.")
    BigDecimal preco,

    @NotNull(message = "A categoria de habilitação é obrigatória.")
    @Positive(message = "Informe um ID válido para a categoria de habilitação.")
    Long idCategoriaHabilitacao

) {}
