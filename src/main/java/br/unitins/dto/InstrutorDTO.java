package br.unitins.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record InstrutorDTO(

    @NotBlank(message = "O nome do instrutor é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    String nome,

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(
        regexp = "(^\\d{11}$)|(^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$)",
        message = "O CPF deve conter 11 dígitos numéricos ou o formato 000.000.000-00."
    )
    String cpf,

    @NotBlank(message = "O registro profissional é obrigatório.")
    @Size(min = 3, max = 50, message = "O registro profissional deve ter entre 3 e 50 caracteres.")
    String registroProfissional,

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 8, max = 20, message = "O telefone deve ter entre 8 e 20 caracteres.")
    String telefone,

    Boolean ativo,

    @NotNull(message = "O ID da autoescola é obrigatório.")
    @Positive(message = "Informe um ID válido para a autoescola.")
    Long idAutoescola,

    @NotEmpty(message = "O instrutor deve possuir pelo menos uma categoria de habilitação.")
    List<Long> idsCategorias

) {}
