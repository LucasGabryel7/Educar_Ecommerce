package br.unitins.service;

import java.time.LocalDate;
import java.util.List;

import br.unitins.dto.AgendamentoAulaDTO;
import br.unitins.dto.AgendamentoAulaResponseDTO;
import br.unitins.model.AgendamentoAula;
import br.unitins.model.Aluno;
import br.unitins.model.AulaPratica;
import br.unitins.model.CategoriaHabilitacao;
import br.unitins.model.Instrutor;
import br.unitins.model.StatusAgendamento;
import br.unitins.model.Veiculo;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AgendamentoAulaRepository;
import br.unitins.repository.AlunoRepository;
import br.unitins.repository.AulaPraticaRepository;
import br.unitins.repository.InstrutorRepository;
import br.unitins.repository.VeiculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AgendamentoAulaService {

    @Inject
    AgendamentoAulaRepository repository;

    @Inject
    AlunoRepository alunoRepository;

    @Inject
    InstrutorRepository instrutorRepository;

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    AulaPraticaRepository aulaPraticaRepository;

    public List<AgendamentoAulaResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(AgendamentoAulaResponseDTO::fromEntity)
                .toList();
    }

    public AgendamentoAulaResponseDTO buscarPorId(Long id) {
        AgendamentoAula agendamento = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Agendamento de aula não encontrado."));
        return AgendamentoAulaResponseDTO.fromEntity(agendamento);
    }

    public List<AgendamentoAulaResponseDTO> buscarPorAluno(Long idAluno) {
        return repository.findByAluno(idAluno)
                .stream()
                .map(AgendamentoAulaResponseDTO::fromEntity)
                .toList();
    }

    public List<AgendamentoAulaResponseDTO> buscarPorInstrutor(Long idInstrutor) {
        return repository.findByInstrutor(idInstrutor)
                .stream()
                .map(AgendamentoAulaResponseDTO::fromEntity)
                .toList();
    }

    public List<AgendamentoAulaResponseDTO> buscarPorVeiculo(Long idVeiculo) {
        return repository.findByVeiculo(idVeiculo)
                .stream()
                .map(AgendamentoAulaResponseDTO::fromEntity)
                .toList();
    }

    public List<AgendamentoAulaResponseDTO> buscarPorData(LocalDate data) {
        return repository.findByData(data)
                .stream()
                .map(AgendamentoAulaResponseDTO::fromEntity)
                .toList();
    }

    public List<AgendamentoAulaResponseDTO> buscarPorStatus(StatusAgendamento status) {
        return repository.findByStatus(status)
                .stream()
                .map(AgendamentoAulaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AgendamentoAulaResponseDTO cadastrar(AgendamentoAulaDTO dto) {
        validarHorarios(dto);

        Aluno aluno = validarECarregarAluno(dto.idAluno());
        Instrutor instrutor = validarECarregarInstrutor(dto.idInstrutor());
        Veiculo veiculo = validarECarregarVeiculo(dto.idVeiculo());
        AulaPratica aulaPratica = validarECarregarAulaPratica(dto.idAulaPratica());

        validarCompatibilidadeCategorias(aulaPratica, instrutor, veiculo);
        validarConflitosHorario(dto, null);

        AgendamentoAula agendamento = new AgendamentoAula();
        agendamento.setDataAula(dto.dataAula());
        agendamento.setHorarioInicio(dto.horarioInicio());
        agendamento.setHorarioTermino(dto.horarioTermino());
        agendamento.setStatus(dto.status() != null ? dto.status() : StatusAgendamento.AGENDADO);
        agendamento.setAluno(aluno);
        agendamento.setInstrutor(instrutor);
        agendamento.setVeiculo(veiculo);
        agendamento.setAulaPratica(aulaPratica);

        repository.persist(agendamento);
        return AgendamentoAulaResponseDTO.fromEntity(agendamento);
    }

    @Transactional
    public AgendamentoAulaResponseDTO atualizar(Long id, AgendamentoAulaDTO dto) {
        AgendamentoAula agendamento = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Agendamento de aula não encontrado."));

        validarHorarios(dto);

        Aluno aluno = validarECarregarAluno(dto.idAluno());
        Instrutor instrutor = validarECarregarInstrutor(dto.idInstrutor());
        Veiculo veiculo = validarECarregarVeiculo(dto.idVeiculo());
        AulaPratica aulaPratica = validarECarregarAulaPratica(dto.idAulaPratica());

        validarCompatibilidadeCategorias(aulaPratica, instrutor, veiculo);

        // Se o status novo for CANCELADO, não é necessário validar conflito de horário
        StatusAgendamento novoStatus = dto.status() != null ? dto.status() : agendamento.getStatus();
        if (novoStatus != StatusAgendamento.CANCELADO) {
            validarConflitosHorario(dto, id);
        }

        agendamento.setDataAula(dto.dataAula());
        agendamento.setHorarioInicio(dto.horarioInicio());
        agendamento.setHorarioTermino(dto.horarioTermino());
        agendamento.setStatus(novoStatus);
        agendamento.setAluno(aluno);
        agendamento.setInstrutor(instrutor);
        agendamento.setVeiculo(veiculo);
        agendamento.setAulaPratica(aulaPratica);

        return AgendamentoAulaResponseDTO.fromEntity(agendamento);
    }

    @Transactional
    public AgendamentoAulaResponseDTO atualizarStatus(Long id, StatusAgendamento novoStatus) {
        AgendamentoAula agendamento = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Agendamento de aula não encontrado."));

        if (novoStatus == null) {
            throw new RegraNegocioException("O novo status não pode ser nulo.");
        }

        agendamento.setStatus(novoStatus);
        return AgendamentoAulaResponseDTO.fromEntity(agendamento);
    }

    @Transactional
    public void excluir(Long id) {
        AgendamentoAula agendamento = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Agendamento de aula não encontrado."));

        // Se o agendamento já foi concluído ou confirmado, cancelamento deve ser a opção preferencial
        if (agendamento.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new RegraNegocioException("Não é permitido excluir fisicamente um agendamento já concluído. "
                    + "O histórico de aulas concluídas deve ser preservado.");
        }

        repository.delete(agendamento);
        repository.flush();
    }

    private void validarHorarios(AgendamentoAulaDTO dto) {
        if (!dto.horarioTermino().isAfter(dto.horarioInicio())) {
            throw new RegraNegocioException("O horário de término deve ser posterior ao horário de início.");
        }
    }

    private Aluno validarECarregarAluno(Long id) {
        Aluno aluno = alunoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aluno com ID " + id + " não encontrado."));
        if (Boolean.FALSE.equals(aluno.getAtivo())) {
            throw new RegraNegocioException("O aluno informado está inativo no sistema.");
        }
        return aluno;
    }

    private Instrutor validarECarregarInstrutor(Long id) {
        Instrutor instrutor = instrutorRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Instrutor com ID " + id + " não encontrado."));
        if (Boolean.FALSE.equals(instrutor.getAtivo())) {
            throw new RegraNegocioException("O instrutor informado está inativo no sistema.");
        }
        return instrutor;
    }

    private Veiculo validarECarregarVeiculo(Long id) {
        Veiculo veiculo = veiculoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Veículo com ID " + id + " não encontrado."));
        if (Boolean.FALSE.equals(veiculo.getAtivo())) {
            throw new RegraNegocioException("O veículo informado está inativo no sistema.");
        }
        return veiculo;
    }

    private AulaPratica validarECarregarAulaPratica(Long id) {
        return aulaPraticaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aula prática com ID " + id + " não encontrada."));
    }

    private void validarCompatibilidadeCategorias(AulaPratica aula, Instrutor instrutor, Veiculo veiculo) {
        CategoriaHabilitacao catExigida = aula.getCategoriaHabilitacao();
        if (catExigida == null) {
            throw new RegraNegocioException("A aula prática selecionada não possui categoria de habilitação vinculada.");
        }

        // Verifica se instrutor possui a categoria
        boolean instrutorApto = instrutor.getCategorias().stream()
                .anyMatch(c -> c.getId().equals(catExigida.getId()));
        if (!instrutorApto) {
            throw new RegraNegocioException("O instrutor não possui a categoria de habilitação exigida pela aula prática ("
                    + catExigida.getNome() + ").");
        }

        // Verifica se veículo pertence à categoria
        if (veiculo.getCategoriaHabilitacao() == null
                || !veiculo.getCategoriaHabilitacao().getId().equals(catExigida.getId())) {
            throw new RegraNegocioException("O veículo não é compatível com a categoria de habilitação exigida pela aula prática ("
                    + catExigida.getNome() + ").");
        }
    }

    private void validarConflitosHorario(AgendamentoAulaDTO dto, Long ignoreId) {
        // Conflito de instrutor
        List<AgendamentoAula> conflitosInstrutor = repository.findConflitosInstrutor(
                dto.idInstrutor(), dto.dataAula(), dto.horarioInicio(), dto.horarioTermino(), ignoreId);
        if (!conflitosInstrutor.isEmpty()) {
            throw new RegraNegocioException("Conflito de horário: o instrutor já possui aula agendada nesse período na data selecionada.");
        }

        // Conflito de veículo
        List<AgendamentoAula> conflitosVeiculo = repository.findConflitosVeiculo(
                dto.idVeiculo(), dto.dataAula(), dto.horarioInicio(), dto.horarioTermino(), ignoreId);
        if (!conflitosVeiculo.isEmpty()) {
            throw new RegraNegocioException("Conflito de horário: o veículo já possui aula agendada nesse período na data selecionada.");
        }

        // Conflito de aluno
        List<AgendamentoAula> conflitosAluno = repository.findConflitosAluno(
                dto.idAluno(), dto.dataAula(), dto.horarioInicio(), dto.horarioTermino(), ignoreId);
        if (!conflitosAluno.isEmpty()) {
            throw new RegraNegocioException("Conflito de horário: o aluno já possui aula agendada nesse período na data selecionada.");
        }
    }
}
