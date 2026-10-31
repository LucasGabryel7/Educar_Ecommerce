package br.unitins.dto;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.model.PacoteAulas;

public record PacoteAulasResponseDTO(
    Long id,
    String nome,
    String descricao,
    BigDecimal preco,
    Integer quantidadeAulas,
    List<ItemPacoteAulaResponseDTO> itens
) {

    public static PacoteAulasResponseDTO fromEntity(PacoteAulas pacote) {
        List<ItemPacoteAulaResponseDTO> itensDTO = pacote.getItens() != null
                ? pacote.getItens().stream().map(ItemPacoteAulaResponseDTO::fromEntity).toList()
                : List.of();

        return new PacoteAulasResponseDTO(
            pacote.getId(),
            pacote.getNome(),
            pacote.getDescricao(),
            pacote.getPreco(),
            pacote.getQuantidadeAulas(),
            itensDTO
        );
    }
}
