package br.unitins.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import br.unitins.model.StatusAgendamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AgendamentoAulaDTO(

    @NotNull(message = "A data da aula é obrigatória.")
    LocalDate dataAula,

    @NotNull(message = "O horário de início é obrigatório.")
    LocalTime horarioInicio,

    @NotNull(message = "O horário de término é obrigatório.")
    LocalTime horarioTermino,

    StatusAgendamento status,

    @NotNull(message = "O ID do aluno é obrigatório.")
    @Positive(message = "Informe um ID válido para o aluno.")
    Long idAluno,

    @NotNull(message = "O ID do instrutor é obrigatório.")
    @Positive(message = "Informe um ID válido para o instrutor.")
    Long idInstrutor,

    @NotNull(message = "O ID do veículo é obrigatório.")
    @Positive(message = "Informe um ID válido para o veículo.")
    Long idVeiculo,

    @NotNull(message = "O ID da aula prática é obrigatório.")
    @Positive(message = "Informe um ID válido para a aula prática.")
    Long idAulaPratica

) {}
