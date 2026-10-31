package br.unitins.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import br.unitins.model.AgendamentoAula;
import br.unitins.model.StatusAgendamento;

public record AgendamentoAulaResponseDTO(
    Long id,
    LocalDate dataAula,
    LocalTime horarioInicio,
    LocalTime horarioTermino,
    StatusAgendamento status,
    AlunoResponseDTO aluno,
    InstrutorResponseDTO instrutor,
    VeiculoResponseDTO veiculo,
    AulaPraticaResponseDTO aulaPratica
) {
    public static AgendamentoAulaResponseDTO fromEntity(AgendamentoAula agendamento) {
        if (agendamento == null) return null;
        return new AgendamentoAulaResponseDTO(
            agendamento.getId(),
            agendamento.getDataAula(),
            agendamento.getHorarioInicio(),
            agendamento.getHorarioTermino(),
            agendamento.getStatus(),
            AlunoResponseDTO.fromEntity(agendamento.getAluno()),
            InstrutorResponseDTO.fromEntity(agendamento.getInstrutor()),
            VeiculoResponseDTO.fromEntity(agendamento.getVeiculo()),
            AulaPraticaResponseDTO.fromEntity(agendamento.getAulaPratica())
        );
    }
}
