package br.unitins.service;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.dto.AulaPraticaDTO;
import br.unitins.dto.AulaPraticaResponseDTO;
import br.unitins.model.AulaPratica;
import br.unitins.model.CategoriaHabilitacao;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AgendamentoAulaRepository;
import br.unitins.repository.AulaPraticaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.ItemPacoteAulaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AulaPraticaService {

    @Inject
    AulaPraticaRepository repository;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    AgendamentoAulaRepository agendamentoRepository;

    @Inject
    ItemPacoteAulaRepository itemPacoteAulaRepository;

    public List<AulaPraticaResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(AulaPraticaResponseDTO::fromEntity)
                .toList();
    }

    public AulaPraticaResponseDTO buscarPorId(Long id) {
        AulaPratica aula = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aula prática não encontrada."));
        return AulaPraticaResponseDTO.fromEntity(aula);
    }

    public List<AulaPraticaResponseDTO> buscarPorNome(String nome) {
        return repository.findByNome(nome)
                .stream()
                .map(AulaPraticaResponseDTO::fromEntity)
                .toList();
    }

    public List<AulaPraticaResponseDTO> buscarPorCategoria(Long idCategoria) {
        return repository.findByCategoria(idCategoria)
                .stream()
                .map(AulaPraticaResponseDTO::fromEntity)
                .toList();
    }

    public List<AulaPraticaResponseDTO> buscarPorFaixaPreco(BigDecimal min, BigDecimal max) {
        return repository.findByFaixaPreco(min, max)
                .stream()
                .map(AulaPraticaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AulaPraticaResponseDTO cadastrar(AulaPraticaDTO dto) {
        CategoriaHabilitacao categoria = categoriaRepository.findByIdOptional(dto.idCategoriaHabilitacao())
                .orElseThrow(() -> new NotFoundException("Categoria de habilitação não encontrada."));

        AulaPratica aula = new AulaPratica();
        aula.setNome(dto.nome().trim());
        aula.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        aula.setPreco(dto.preco());
        aula.setCategoriaHabilitacao(categoria);

        repository.persist(aula);
        return AulaPraticaResponseDTO.fromEntity(aula);
    }

    @Transactional
    public AulaPraticaResponseDTO atualizar(Long id, AulaPraticaDTO dto) {
        AulaPratica aula = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aula prática não encontrada."));

        CategoriaHabilitacao categoria = categoriaRepository.findByIdOptional(dto.idCategoriaHabilitacao())
                .orElseThrow(() -> new NotFoundException("Categoria de habilitação não encontrada."));

        aula.setNome(dto.nome().trim());
        aula.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        aula.setPreco(dto.preco());
        aula.setCategoriaHabilitacao(categoria);

        return AulaPraticaResponseDTO.fromEntity(aula);
    }

    @Transactional
    public void excluir(Long id) {
        AulaPratica aula = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aula prática não encontrada."));

        long totalAgendamentos = agendamentoRepository.countByAulaPratica(id);
        if (totalAgendamentos > 0) {
            throw new RegraNegocioException("Não é possível excluir a aula prática pois existem "
                    + totalAgendamentos + " agendamentos vinculados a ela.");
        }

        long totalPacotes = itemPacoteAulaRepository.count("oferta.id", id);
        if (totalPacotes > 0) {
            throw new RegraNegocioException("Não é possível excluir a aula prática pois ela faz parte de "
                    + totalPacotes + " pacote(s) de aulas.");
        }

        repository.delete(aula);
    }
}