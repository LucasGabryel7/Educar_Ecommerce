package br.unitins.dto;

import br.unitins.model.AulaPratica;
import br.unitins.model.AulaTeorica;
import br.unitins.model.ItemPacoteAula;

public record ItemPacoteAulaResponseDTO(
    Long id,
    Long idOferta,
    String nomeOferta,
    String tipoOferta,
    Integer quantidade
) {
    public static ItemPacoteAulaResponseDTO fromEntity(ItemPacoteAula item) {
        if (item == null) return null;
        String tipo = "OUTRO";
        if (item.getOferta() instanceof AulaPratica) {
            tipo = "PRATICA";
        } else if (item.getOferta() instanceof AulaTeorica) {
            tipo = "TEORICA";
        }
        return new ItemPacoteAulaResponseDTO(
            item.getId(),
            item.getOferta() != null ? item.getOferta().getId() : null,
            item.getOferta() != null ? item.getOferta().getNome() : null,
            tipo,
            item.getQuantidade()
        );
    }
}
