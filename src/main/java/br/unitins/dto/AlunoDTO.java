package br.unitins.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlunoDTO(

    @NotBlank(message = "O nome do aluno é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    String nome,

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(
        regexp = "(^\\d{11}$)|(^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$)",
        message = "O CPF deve conter 11 dígitos numéricos ou o formato 000.000.000-00."
    )
    String cpf,

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 8, max = 20, message = "O telefone deve ter entre 8 e 20 caracteres.")
    String telefone,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O e-mail informado é inválido.")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
    String email,

    Boolean ativo

) {}
