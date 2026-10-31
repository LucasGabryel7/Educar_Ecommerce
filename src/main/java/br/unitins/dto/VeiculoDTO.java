package br.unitins.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VeiculoDTO(

    @NotBlank(message = "A placa é obrigatória.")
    @Pattern(
        regexp = "^[A-Za-z]{3}-?[0-9][A-Za-z0-9][0-9]{2}$",
        message = "A placa deve seguir o padrão brasileiro tradicional (ABC-1234) ou Mercosul (ABC1D23)."
    )
    String placa,

    @NotBlank(message = "A marca é obrigatória.")
    @Size(min = 2, max = 50, message = "A marca deve ter entre 2 e 50 caracteres.")
    String marca,

    @NotBlank(message = "O modelo é obrigatório.")
    @Size(min = 2, max = 50, message = "O modelo deve ter entre 2 e 50 caracteres.")
    String modelo,

    @NotNull(message = "O ano de fabricação é obrigatório.")
    @Min(value = 1900, message = "O ano de fabricação deve ser igual ou superior a 1900.")
    Integer anoFabricacao,

    Boolean ativo,

    @NotNull(message = "O ID da autoescola é obrigatório.")
    @Positive(message = "Informe um ID válido para a autoescola.")
    Long idAutoescola,

    @NotNull(message = "O ID da categoria de habilitação é obrigatório.")
    @Positive(message = "Informe um ID válido para a categoria de habilitação.")
    Long idCategoriaHabilitacao,

    @NotNull(message = "Os dados do documento do veículo são obrigatórios.")
    @Valid
    DocumentoVeiculoDTO documento

) {}
