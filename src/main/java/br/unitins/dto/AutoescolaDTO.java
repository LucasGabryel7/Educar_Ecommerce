package br.unitins.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AutoescolaDTO(

    @NotBlank(message = "O nome da autoescola é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    String nome,

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(
        regexp = "(^\\d{14}$)|(^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$)",
        message = "O CNPJ deve conter 14 dígitos numéricos ou o formato 00.000.000/0000-00."
    )
    String cnpj,

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 8, max = 20, message = "O telefone deve ter entre 8 e 20 caracteres.")
    String telefone,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado é inválido.")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
    String email,

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(min = 5, max = 255, message = "O endereço deve ter entre 5 e 255 caracteres.")
    String endereco

) {}
