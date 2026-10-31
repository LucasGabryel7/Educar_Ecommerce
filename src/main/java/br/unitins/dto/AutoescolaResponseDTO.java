package br.unitins.dto;

import br.unitins.model.Autoescola;

public record AutoescolaResponseDTO(
    Long id,
    String nome,
    String cnpj,
    String telefone,
    String email,
    String endereco
) {
    public static AutoescolaResponseDTO fromEntity(Autoescola autoescola) {
        if (autoescola == null) return null;
        return new AutoescolaResponseDTO(
            autoescola.getId(),
            autoescola.getNome(),
            autoescola.getCnpj(),
            autoescola.getTelefone(),
            autoescola.getEmail(),
            autoescola.getEndereco()
        );
    }
}
