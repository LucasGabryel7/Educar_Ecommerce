package br.unitins.dto;

import br.unitins.model.CategoriaHabilitacao;

public record CategoriaHabilitacaoResponseDTO(
    Long id,
    String nome,
    String descricao
) {

    public static CategoriaHabilitacaoResponseDTO fromEntity(CategoriaHabilitacao categoria) {
        return new CategoriaHabilitacaoResponseDTO(
            categoria.getId(),
            categoria.getNome(),
            categoria.getDescricao()
        );
    }
}
