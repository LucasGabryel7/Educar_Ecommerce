package br.unitins.dto;

import br.unitins.model.Aluno;

public record AlunoResponseDTO(
    Long id,
    String nome,
    String cpf,
    String telefone,
    String email,
    Boolean ativo
) {
    public static AlunoResponseDTO fromEntity(Aluno aluno) {
        if (aluno == null) return null;
        return new AlunoResponseDTO(
            aluno.getId(),
            aluno.getNome(),
            aluno.getCpf(),
            aluno.getTelefone(),
            aluno.getEmail(),
            aluno.getAtivo()
        );
    }
}
