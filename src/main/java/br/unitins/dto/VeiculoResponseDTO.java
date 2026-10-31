package br.unitins.dto;

import br.unitins.model.Veiculo;

public record VeiculoResponseDTO(
    Long id,
    String placa,
    String marca,
    String modelo,
    Integer anoFabricacao,
    Boolean ativo,
    Long idAutoescola,
    String nomeAutoescola,
    CategoriaHabilitacaoResponseDTO categoriaHabilitacao,
    DocumentoVeiculoResponseDTO documento
) {
    public static VeiculoResponseDTO fromEntity(Veiculo veiculo) {
        if (veiculo == null) return null;
        return new VeiculoResponseDTO(
            veiculo.getId(),
            veiculo.getPlaca(),
            veiculo.getMarca(),
            veiculo.getModelo(),
            veiculo.getAnoFabricacao(),
            veiculo.getAtivo(),
            veiculo.getAutoescola() != null ? veiculo.getAutoescola().getId() : null,
            veiculo.getAutoescola() != null ? veiculo.getAutoescola().getNome() : null,
            veiculo.getCategoriaHabilitacao() != null ? CategoriaHabilitacaoResponseDTO.fromEntity(veiculo.getCategoriaHabilitacao()) : null,
            DocumentoVeiculoResponseDTO.fromEntity(veiculo.getDocumentoVeiculo())
        );
    }
}
