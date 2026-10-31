package br.unitins.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.unitins.dto.InstrutorDTO;
import br.unitins.dto.InstrutorResponseDTO;
import br.unitins.model.Autoescola;
import br.unitins.model.CategoriaHabilitacao;
import br.unitins.model.Instrutor;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AgendamentoAulaRepository;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.InstrutorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class InstrutorService {

    @Inject
    InstrutorRepository repository;

    @Inject
    AutoescolaRepository autoescolaRepository;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    AgendamentoAulaRepository agendamentoRepository;

    public List<InstrutorResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(InstrutorResponseDTO::fromEntity)
                .toList();
    }

    public InstrutorResponseDTO buscarPorId(Long id) {
        Instrutor instrutor = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Instrutor não encontrado."));
        return InstrutorResponseDTO.fromEntity(instrutor);
    }

    public List<InstrutorResponseDTO> buscarPorNome(String nome) {
        return repository.findByNome(nome)
                .stream()
                .map(InstrutorResponseDTO::fromEntity)
                .toList();
    }

    public InstrutorResponseDTO buscarPorCpf(String cpf) {
        String cpfNormalizado = normalizarDocumento(cpf);
        Instrutor instrutor = repository.findByCpf(cpfNormalizado)
                .orElseThrow(() -> new NotFoundException("Instrutor com o CPF informado não encontrado."));
        return InstrutorResponseDTO.fromEntity(instrutor);
    }

    public List<InstrutorResponseDTO> buscarPorCategoria(Long idCategoria) {
        return repository.findByCategoria(idCategoria)
                .stream()
                .map(InstrutorResponseDTO::fromEntity)
                .toList();
    }

    public List<InstrutorResponseDTO> buscarPorAtivo(Boolean ativo) {
        return repository.findByAtivo(ativo)
                .stream()
                .map(InstrutorResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public InstrutorResponseDTO cadastrar(InstrutorDTO dto) {
        String cpfNormalizado = normalizarDocumento(dto.cpf());
        if (repository.findByCpf(cpfNormalizado).isPresent()) {
            throw new RegraNegocioException("CPF já cadastrado para outro instrutor.");
        }

        String registro = dto.registroProfissional().trim();
        if (repository.findByRegistroProfissional(registro).isPresent()) {
            throw new RegraNegocioException("Registro profissional já cadastrado para outro instrutor.");
        }

        Autoescola autoescola = autoescolaRepository.findByIdOptional(dto.idAutoescola())
                .orElseThrow(() -> new NotFoundException("Autoescola não encontrada."));

        Set<CategoriaHabilitacao> categorias = carregarCategorias(dto.idsCategorias());

        Instrutor instrutor = new Instrutor();
        instrutor.setNome(dto.nome().trim());
        instrutor.setCpf(cpfNormalizado);
        instrutor.setRegistroProfissional(registro);
        instrutor.setTelefone(dto.telefone().trim());
        instrutor.setAtivo(dto.ativo() != null ? dto.ativo() : true);
        instrutor.setAutoescola(autoescola);
        instrutor.setCategorias(categorias);

        repository.persist(instrutor);
        return InstrutorResponseDTO.fromEntity(instrutor);
    }

    @Transactional
    public InstrutorResponseDTO atualizar(Long id, InstrutorDTO dto) {
        Instrutor instrutor = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Instrutor não encontrado."));

        String cpfNormalizado = normalizarDocumento(dto.cpf());
        repository.findByCpf(cpfNormalizado).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("CPF já cadastrado para outro instrutor.");
            }
        });

        String registro = dto.registroProfissional().trim();
        repository.findByRegistroProfissional(registro).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("Registro profissional já cadastrado para outro instrutor.");
            }
        });

        Autoescola autoescola = autoescolaRepository.findByIdOptional(dto.idAutoescola())
                .orElseThrow(() -> new NotFoundException("Autoescola não encontrada."));

        Set<CategoriaHabilitacao> categorias = carregarCategorias(dto.idsCategorias());

        instrutor.setNome(dto.nome().trim());
        instrutor.setCpf(cpfNormalizado);
        instrutor.setRegistroProfissional(registro);
        instrutor.setTelefone(dto.telefone().trim());
        if (dto.ativo() != null) {
            instrutor.setAtivo(dto.ativo());
        }
        instrutor.setAutoescola(autoescola);
        instrutor.setCategorias(categorias);

        return InstrutorResponseDTO.fromEntity(instrutor);
    }

    @Transactional
    public void excluir(Long id) {
        Instrutor instrutor = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Instrutor não encontrado."));

        long totalAgendamentos = agendamentoRepository.countByInstrutor(id);
        if (totalAgendamentos > 0) {
            throw new RegraNegocioException("Não é possível excluir o instrutor pois existem "
                    + totalAgendamentos + " agendamentos vinculados ao seu histórico. "
                    + "Para manter a integridade histórica, desative o cadastro (ativo = false).");
        }

        repository.delete(instrutor);
        repository.flush();
    }

    private Set<CategoriaHabilitacao> carregarCategorias(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RegraNegocioException("O instrutor deve possuir pelo menos uma categoria de habilitação.");
        }
        Set<CategoriaHabilitacao> categorias = new HashSet<>();
        for (Long catId : ids) {
            CategoriaHabilitacao categoria = categoriaRepository.findByIdOptional(catId)
                    .orElseThrow(() -> new NotFoundException("Categoria de habilitação com ID " + catId + " não encontrada."));
            categorias.add(categoria);
        }
        return categorias;
    }

    private String normalizarDocumento(String doc) {
        return doc != null ? doc.replaceAll("\\D", "") : null;
    }
}
