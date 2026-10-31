package br.unitins.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DocumentoVeiculoDTO(

    @NotBlank(message = "O número do documento é obrigatório.")
    @Size(min = 3, max = 50, message = "O número do documento deve ter entre 3 e 50 caracteres.")
    String numero,

    @NotNull(message = "A data de validade do documento é obrigatória.")
    LocalDate dataValidade

) {}
