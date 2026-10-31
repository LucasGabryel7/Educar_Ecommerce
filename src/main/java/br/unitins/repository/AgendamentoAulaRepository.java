package br.unitins.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import br.unitins.model.AgendamentoAula;
import br.unitins.model.StatusAgendamento;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgendamentoAulaRepository implements PanacheRepository<AgendamentoAula> {

    public List<AgendamentoAula> findByAluno(Long idAluno) {
        return find("aluno.id", idAluno).list();
    }

    public List<AgendamentoAula> findByInstrutor(Long idInstrutor) {
        return find("instrutor.id", idInstrutor).list();
    }

    public List<AgendamentoAula> findByVeiculo(Long idVeiculo) {
        return find("veiculo.id", idVeiculo).list();
    }

    public List<AgendamentoAula> findByData(LocalDate data) {
        return find("dataAula", data).list();
    }

    public List<AgendamentoAula> findByStatus(StatusAgendamento status) {
        return find("status", status).list();
    }

    public long countByAluno(Long idAluno) {
        return count("aluno.id", idAluno);
    }

    public long countByInstrutor(Long idInstrutor) {
        return count("instrutor.id", idInstrutor);
    }

    public long countByVeiculo(Long idVeiculo) {
        return count("veiculo.id", idVeiculo);
    }

    public long countByAulaPratica(Long idAulaPratica) {
        return count("aulaPratica.id", idAulaPratica);
    }

    public List<AgendamentoAula> findConflitosInstrutor(
            Long instrutorId,
            LocalDate dataAula,
            LocalTime inicio,
            LocalTime termino,
            Long ignoreId) {
        if (ignoreId != null) {
            String query = "instrutor.id = ?1 and dataAula = ?2 and status != ?3 "
                    + "and horarioInicio < ?4 and horarioTermino > ?5 and id != ?6";
            return find(query, instrutorId, dataAula, StatusAgendamento.CANCELADO, termino, inicio, ignoreId).list();
        } else {
            String query = "instrutor.id = ?1 and dataAula = ?2 and status != ?3 "
                    + "and horarioInicio < ?4 and horarioTermino > ?5";
            return find(query, instrutorId, dataAula, StatusAgendamento.CANCELADO, termino, inicio).list();
        }
    }

    public List<AgendamentoAula> findConflitosVeiculo(
            Long veiculoId,
            LocalDate dataAula,
            LocalTime inicio,
            LocalTime termino,
            Long ignoreId) {
        if (ignoreId != null) {
            String query = "veiculo.id = ?1 and dataAula = ?2 and status != ?3 "
                    + "and horarioInicio < ?4 and horarioTermino > ?5 and id != ?6";
            return find(query, veiculoId, dataAula, StatusAgendamento.CANCELADO, termino, inicio, ignoreId).list();
        } else {
            String query = "veiculo.id = ?1 and dataAula = ?2 and status != ?3 "
                    + "and horarioInicio < ?4 and horarioTermino > ?5";
            return find(query, veiculoId, dataAula, StatusAgendamento.CANCELADO, termino, inicio).list();
        }
    }

    public List<AgendamentoAula> findConflitosAluno(
            Long alunoId,
            LocalDate dataAula,
            LocalTime inicio,
            LocalTime termino,
            Long ignoreId) {
        if (ignoreId != null) {
            String query = "aluno.id = ?1 and dataAula = ?2 and status != ?3 "
                    + "and horarioInicio < ?4 and horarioTermino > ?5 and id != ?6";
            return find(query, alunoId, dataAula, StatusAgendamento.CANCELADO, termino, inicio, ignoreId).list();
        } else {
            String query = "aluno.id = ?1 and dataAula = ?2 and status != ?3 "
                    + "and horarioInicio < ?4 and horarioTermino > ?5";
            return find(query, alunoId, dataAula, StatusAgendamento.CANCELADO, termino, inicio).list();
        }
    }
}
