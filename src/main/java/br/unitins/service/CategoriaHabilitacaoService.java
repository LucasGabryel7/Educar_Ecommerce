package br.unitins.service;

import java.util.List;

import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.model.CategoriaHabilitacao;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AulaPraticaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.InstrutorRepository;
import br.unitins.repository.VeiculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class CategoriaHabilitacaoService {

    @Inject
    CategoriaHabilitacaoRepository repository;

    @Inject
    AulaPraticaRepository aulaPraticaRepository;

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    InstrutorRepository instrutorRepository;

    public List<CategoriaHabilitacaoResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .toList();
    }

    public CategoriaHabilitacaoResponseDTO buscarPorId(Long id) {
        CategoriaHabilitacao categoria = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Categoria de habilitação não encontrada."));
        return CategoriaHabilitacaoResponseDTO.fromEntity(categoria);
    }

    public List<CategoriaHabilitacaoResponseDTO> buscarPorNome(String nome) {
        return repository.findByNome(nome)
                .stream()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public CategoriaHabilitacaoResponseDTO cadastrar(CategoriaHabilitacaoDTO dto) {
        String nomeNormalizado = dto.nome().trim().toUpperCase();
        if (repository.find("upper(nome)", nomeNormalizado).firstResultOptional().isPresent()) {
            throw new RegraNegocioException("Já existe uma categoria de habilitação cadastrada com o nome '" + nomeNormalizado + "'.");
        }

        CategoriaHabilitacao categoria = new CategoriaHabilitacao();
        categoria.setNome(nomeNormalizado);
        categoria.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        repository.persist(categoria);
        return CategoriaHabilitacaoResponseDTO.fromEntity(categoria);
    }

    @Transactional
    public CategoriaHabilitacaoResponseDTO atualizar(Long id, CategoriaHabilitacaoDTO dto) {
        CategoriaHabilitacao categoria = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Categoria de habilitação não encontrada."));

        String nomeNormalizado = dto.nome().trim().toUpperCase();
        repository.find("upper(nome)", nomeNormalizado).firstResultOptional().ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("Já existe uma categoria de habilitação cadastrada com o nome '" + nomeNormalizado + "'.");
            }
        });

        categoria.setNome(nomeNormalizado);
        categoria.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        return CategoriaHabilitacaoResponseDTO.fromEntity(categoria);
    }

    @Transactional
    public void excluir(Long id) {
        CategoriaHabilitacao categoria = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Categoria de habilitação não encontrada."));

        if (!aulaPraticaRepository.findByCategoria(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir a categoria pois existem aulas práticas vinculadas a ela.");
        }
        if (!veiculoRepository.findByCategoria(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir a categoria pois existem veículos vinculados a ela.");
        }
        if (!instrutorRepository.findByCategoria(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir a categoria pois existem instrutores habilitados para ela.");
        }

        repository.delete(categoria);
    }
}
