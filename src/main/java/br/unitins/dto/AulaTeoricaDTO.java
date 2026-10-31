package br.unitins.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AulaTeoricaDTO(

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    String nome,

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    String descricao,

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O preço deve ter até 8 dígitos inteiros e 2 decimais.")
    BigDecimal preco,

    @NotNull(message = "A carga horária é obrigatória.")
    @Min(value = 1, message = "A carga horária deve ser de no mínimo 1 hora.")
    Integer cargaHoraria

) {}
