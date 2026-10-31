package br.unitins.service;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.dto.AulaTeoricaDTO;
import br.unitins.dto.AulaTeoricaResponseDTO;
import br.unitins.model.AulaTeorica;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AulaTeoricaRepository;
import br.unitins.repository.ItemPacoteAulaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AulaTeoricaService {

    @Inject
    AulaTeoricaRepository repository;

    @Inject
    ItemPacoteAulaRepository itemPacoteAulaRepository;

    public List<AulaTeoricaResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(AulaTeoricaResponseDTO::fromEntity)
                .toList();
    }

    public AulaTeoricaResponseDTO buscarPorId(Long id) {
        AulaTeorica aula = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aula teórica não encontrada."));
        return AulaTeoricaResponseDTO.fromEntity(aula);
    }

    public List<AulaTeoricaResponseDTO> buscarPorNome(String nome) {
        return repository.findByNome(nome)
                .stream()
                .map(AulaTeoricaResponseDTO::fromEntity)
                .toList();
    }

    public List<AulaTeoricaResponseDTO> buscarPorFaixaPreco(BigDecimal min, BigDecimal max) {
        return repository.findByFaixaPreco(min, max)
                .stream()
                .map(AulaTeoricaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AulaTeoricaResponseDTO cadastrar(AulaTeoricaDTO dto) {
        AulaTeorica aula = new AulaTeorica();
        aula.setNome(dto.nome().trim());
        aula.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        aula.setPreco(dto.preco());
        aula.setCargaHoraria(dto.cargaHoraria());
        repository.persist(aula);
        return AulaTeoricaResponseDTO.fromEntity(aula);
    }

    @Transactional
    public AulaTeoricaResponseDTO atualizar(Long id, AulaTeoricaDTO dto) {
        AulaTeorica aula = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aula teórica não encontrada."));

        aula.setNome(dto.nome().trim());
        aula.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        aula.setPreco(dto.preco());
        aula.setCargaHoraria(dto.cargaHoraria());

        return AulaTeoricaResponseDTO.fromEntity(aula);
    }

    @Transactional
    public void excluir(Long id) {
        AulaTeorica aula = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Aula teórica não encontrada."));

        long totalPacotes = itemPacoteAulaRepository.count("oferta.id", id);
        if (totalPacotes > 0) {
            throw new RegraNegocioException("Não é possível excluir a aula teórica pois ela faz parte de "
                    + totalPacotes + " pacote(s) de aulas.");
        }

        repository.delete(aula);
    }
}