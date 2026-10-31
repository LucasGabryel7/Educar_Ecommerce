package br.unitins.service;

import java.util.List;

import br.unitins.dto.AlunoDTO;
import br.unitins.dto.AlunoResponseDTO;
import br.unitins.model.Aluno;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AgendamentoAulaRepository;
import br.unitins.repository.AlunoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AlunoService {

    @Inject
    AlunoRepository repository;

    @Inject
    AgendamentoAulaRepository agendamentoRepository;

    public List<AlunoResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(AlunoResponseDTO::fromEntity)
                .toList();
    }

    public AlunoResponseDTO buscarPorId(Long id) {
        Aluno aluno = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));
        return AlunoResponseDTO.fromEntity(aluno);
    }

    public List<AlunoResponseDTO> buscarPorNome(String nome) {
        return repository.findByNome(nome)
                .stream()
                .map(AlunoResponseDTO::fromEntity)
                .toList();
    }

    public AlunoResponseDTO buscarPorCpf(String cpf) {
        String cpfNormalizado = normalizarDocumento(cpf);
        Aluno aluno = repository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new NotFoundException("Aluno com CPF informado não encontrado."));
        return AlunoResponseDTO.fromEntity(aluno);
    }

    public AlunoResponseDTO buscarPorEmail(String email) {
        Aluno aluno = repository.findByEmail(email.trim())
                .orElseThrow(() -> new NotFoundException("Aluno com e-mail informado não encontrado."));
        return AlunoResponseDTO.fromEntity(aluno);
    }

    public List<AlunoResponseDTO> buscarPorAtivo(Boolean ativo) {
        return repository.findByAtivo(ativo)
                .stream()
                .map(AlunoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AlunoResponseDTO cadastrar(AlunoDTO dto) {
        String cpfNormalizado = normalizarDocumento(dto.cpf());
        if (repository.findByCpf(cpfNormalizado).isPresent()) {
            throw new RegraNegocioException("CPF já cadastrado para outro aluno.");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(dto.nome().trim());
        aluno.setCpf(cpfNormalizado);
        aluno.setTelefone(dto.telefone().trim());
        aluno.setEmail(dto.email().trim().toLowerCase());
        aluno.setAtivo(dto.ativo() != null ? dto.ativo() : true);

        repository.persist(aluno);
        return AlunoResponseDTO.fromEntity(aluno);
    }

    @Transactional
    public AlunoResponseDTO atualizar(Long id, AlunoDTO dto) {
        Aluno aluno = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        String cpfNormalizado = normalizarDocumento(dto.cpf());
        repository.findByCpf(cpfNormalizado).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("CPF já cadastrado para outro aluno.");
            }
        });

        aluno.setNome(dto.nome().trim());
        aluno.setCpf(cpfNormalizado);
        aluno.setTelefone(dto.telefone().trim());
        aluno.setEmail(dto.email().trim().toLowerCase());
        if (dto.ativo() != null) {
            aluno.setAtivo(dto.ativo());
        }

        return AlunoResponseDTO.fromEntity(aluno);
    }

    @Transactional
    public void excluir(Long id) {
        Aluno aluno = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        long totalAgendamentos = agendamentoRepository.countByAluno(id);
        if (totalAgendamentos > 0) {
            throw new RegraNegocioException("Não é possível excluir o aluno pois existem "
                    + totalAgendamentos + " agendamentos vinculados ao seu histórico. "
                    + "Para manter a rastreabilidade, desative o cadastro (ativo = false).");
        }

        repository.delete(aluno);
        repository.flush();
    }

    private String normalizarDocumento(String doc) {
        return doc != null ? doc.replaceAll("\\D", "") : null;
    }
}
