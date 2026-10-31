package br.unitins.dto;

import java.math.BigDecimal;

import br.unitins.model.AulaTeorica;

public record AulaTeoricaResponseDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    Integer cargaHoraria
) {

    public static AulaTeoricaResponseDTO fromEntity(AulaTeorica aula) {
        return new AulaTeoricaResponseDTO(
            aula.getId(),
            aula.getNome(),
            aula.getDescricao(),
            aula.getPreco(),
            aula.getCargaHoraria()
        );
    }
}
