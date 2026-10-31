package br.unitins.dto;

import java.util.List;

import br.unitins.model.Instrutor;

public record InstrutorResponseDTO(
    Long id,
    String nome,
    String cpf,
    String registroProfissional,
    String telefone,
    Boolean ativo,
    Long idAutoescola,
    String nomeAutoescola,
    List<CategoriaHabilitacaoResponseDTO> categorias
) {
    public static InstrutorResponseDTO fromEntity(Instrutor instrutor) {
        if (instrutor == null) return null;
        List<CategoriaHabilitacaoResponseDTO> categoriasDTO = instrutor.getCategorias() != null
                ? instrutor.getCategorias().stream().map(CategoriaHabilitacaoResponseDTO::fromEntity).toList()
                : List.of();

        return new InstrutorResponseDTO(
            instrutor.getId(),
            instrutor.getNome(),
            instrutor.getCpf(),
            instrutor.getRegistroProfissional(),
            instrutor.getTelefone(),
            instrutor.getAtivo(),
            instrutor.getAutoescola() != null ? instrutor.getAutoescola().getId() : null,
            instrutor.getAutoescola() != null ? instrutor.getAutoescola().getNome() : null,
            categoriasDTO
        );
    }
}
