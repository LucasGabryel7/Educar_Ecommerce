package br.unitins.dto;

import java.math.BigDecimal;

import br.unitins.model.AulaPratica;

public record AulaPraticaResponseDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    CategoriaHabilitacaoResponseDTO categoriaHabilitacao
) {

    public static AulaPraticaResponseDTO fromEntity(AulaPratica aula) {
        return new AulaPraticaResponseDTO(
            aula.getId(),
            aula.getNome(),
            aula.getDescricao(),
            aula.getPreco(),
            CategoriaHabilitacaoResponseDTO.fromEntity(aula.getCategoriaHabilitacao())
        );
    }
}
