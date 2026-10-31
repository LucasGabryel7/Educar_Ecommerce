package br.unitins.service;

import java.util.List;

import br.unitins.dto.AutoescolaDTO;
import br.unitins.dto.AutoescolaResponseDTO;
import br.unitins.model.Autoescola;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.InstrutorRepository;
import br.unitins.repository.VeiculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class AutoescolaService {

    @Inject
    AutoescolaRepository repository;

    @Inject
    InstrutorRepository instrutorRepository;

    @Inject
    VeiculoRepository veiculoRepository;

    public List<AutoescolaResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(AutoescolaResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AutoescolaResponseDTO buscarPorId(Long id) {
        Autoescola autoescola = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Autoescola não encontrada."));
        return AutoescolaResponseDTO.fromEntity(autoescola);
    }

    public List<AutoescolaResponseDTO> buscarPorNome(String nome) {
        return repository.findByNome(nome)
                .stream()
                .map(AutoescolaResponseDTO::fromEntity)
                .toList();
    }

    public AutoescolaResponseDTO buscarPorCnpj(String cnpj) {
        String cnpjNormalizado = normalizarDocumento(cnpj);
        Autoescola autoescola = repository.findByCnpj(cnpjNormalizado)
                .orElseThrow(() -> new NotFoundException("Autoescola com CNPJ informado não encontrada."));
        return AutoescolaResponseDTO.fromEntity(autoescola);
    }

    @Transactional
    public AutoescolaResponseDTO cadastrar(AutoescolaDTO dto) {
        String cnpjNormalizado = normalizarDocumento(dto.cnpj());
        if (repository.findByCnpj(cnpjNormalizado).isPresent()) {
            throw new RegraNegocioException("CNPJ já cadastrado para outra autoescola.");
        }

        Autoescola autoescola = new Autoescola();
        autoescola.setNome(dto.nome().trim());
        autoescola.setCnpj(cnpjNormalizado);
        autoescola.setTelefone(dto.telefone().trim());
        autoescola.setEmail(dto.email().trim().toLowerCase());
        autoescola.setEndereco(dto.endereco().trim());

        repository.persist(autoescola);
        return AutoescolaResponseDTO.fromEntity(autoescola);
    }

    @Transactional
    public AutoescolaResponseDTO atualizar(Long id, AutoescolaDTO dto) {
        Autoescola autoescola = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Autoescola não encontrada."));

        String cnpjNormalizado = normalizarDocumento(dto.cnpj());
        repository.findByCnpj(cnpjNormalizado).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("CNPJ já cadastrado para outra autoescola.");
            }
        });

        autoescola.setNome(dto.nome().trim());
        autoescola.setCnpj(cnpjNormalizado);
        autoescola.setTelefone(dto.telefone().trim());
        autoescola.setEmail(dto.email().trim().toLowerCase());
        autoescola.setEndereco(dto.endereco().trim());

        return AutoescolaResponseDTO.fromEntity(autoescola);
    }

    @Transactional
    public void excluir(Long id) {
        Autoescola autoescola = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Autoescola não encontrada."));

        if (!instrutorRepository.findByAutoescola(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir a autoescola pois existem instrutores vinculados a ela.");
        }
        if (!veiculoRepository.findByAutoescola(id).isEmpty()) {
            throw new RegraNegocioException("Não é possível excluir a autoescola pois existem veículos vinculados a ela.");
        }

        repository.delete(autoescola);
        repository.flush();
        repository.getEntityManager().clear();
    }

    private String normalizarDocumento(String doc) {
        return doc != null ? doc.replaceAll("\\D", "") : null;
    }
}
