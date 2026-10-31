package br.unitins.dto;

import java.time.LocalDate;

import br.unitins.model.DocumentoVeiculo;

public record DocumentoVeiculoResponseDTO(
    Long id,
    String numero,
    LocalDate dataValidade
) {
    public static DocumentoVeiculoResponseDTO fromEntity(DocumentoVeiculo doc) {
        if (doc == null) return null;
        return new DocumentoVeiculoResponseDTO(
            doc.getId(),
            doc.getNumero(),
            doc.getDataValidade()
        );
    }
}
