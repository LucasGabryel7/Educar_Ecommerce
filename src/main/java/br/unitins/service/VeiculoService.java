package br.unitins.service;

import java.time.Year;
import java.util.List;

import br.unitins.dto.VeiculoDTO;
import br.unitins.dto.VeiculoResponseDTO;
import br.unitins.model.Autoescola;
import br.unitins.model.CategoriaHabilitacao;
import br.unitins.model.DocumentoVeiculo;
import br.unitins.model.Veiculo;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AgendamentoAulaRepository;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.VeiculoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class VeiculoService {

    @Inject
    VeiculoRepository repository;

    @Inject
    AutoescolaRepository autoescolaRepository;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    AgendamentoAulaRepository agendamentoRepository;

    public List<VeiculoResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(VeiculoResponseDTO::fromEntity)
                .toList();
    }

    public VeiculoResponseDTO buscarPorId(Long id) {
        Veiculo veiculo = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));
        return VeiculoResponseDTO.fromEntity(veiculo);
    }

    public VeiculoResponseDTO buscarPorPlaca(String placa) {
        String placaNormalizada = normalizarPlaca(placa);
        Veiculo veiculo = repository.findByPlaca(placaNormalizada)
                .orElseThrow(() -> new NotFoundException("Veículo com a placa informada não encontrado."));
        return VeiculoResponseDTO.fromEntity(veiculo);
    }

    public List<VeiculoResponseDTO> buscarPorCategoria(Long idCategoria) {
        return repository.findByCategoria(idCategoria)
                .stream()
                .map(VeiculoResponseDTO::fromEntity)
                .toList();
    }

    public List<VeiculoResponseDTO> buscarPorAtivo(Boolean ativo) {
        return repository.findByAtivo(ativo)
                .stream()
                .map(VeiculoResponseDTO::fromEntity)
                .toList();
    }

    public List<VeiculoResponseDTO> buscarPorAutoescola(Long idAutoescola) {
        return repository.findByAutoescola(idAutoescola)
                .stream()
                .map(VeiculoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public VeiculoResponseDTO cadastrar(VeiculoDTO dto) {
        String placaNormalizada = normalizarPlaca(dto.placa());
        if (repository.findByPlaca(placaNormalizada).isPresent()) {
            throw new RegraNegocioException("Placa já cadastrada para outro veículo.");
        }

        validarAnoFabricacao(dto.anoFabricacao());

        Autoescola autoescola = autoescolaRepository.findByIdOptional(dto.idAutoescola())
                .orElseThrow(() -> new NotFoundException("Autoescola não encontrada."));

        CategoriaHabilitacao categoria = categoriaRepository.findByIdOptional(dto.idCategoriaHabilitacao())
                .orElseThrow(() -> new NotFoundException("Categoria de habilitação não encontrada."));

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placaNormalizada);
        veiculo.setMarca(dto.marca().trim());
        veiculo.setModelo(dto.modelo().trim());
        veiculo.setAnoFabricacao(dto.anoFabricacao());
        veiculo.setAtivo(dto.ativo() != null ? dto.ativo() : true);
        veiculo.setAutoescola(autoescola);
        veiculo.setCategoriaHabilitacao(categoria);

        DocumentoVeiculo doc = new DocumentoVeiculo();
        doc.setNumero(dto.documento().numero().trim());
        doc.setDataValidade(dto.documento().dataValidade());
        veiculo.setDocumentoVeiculo(doc);

        repository.persist(veiculo);
        return VeiculoResponseDTO.fromEntity(veiculo);
    }

    @Transactional
    public VeiculoResponseDTO atualizar(Long id, VeiculoDTO dto) {
        Veiculo veiculo = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));

        String placaNormalizada = normalizarPlaca(dto.placa());
        repository.findByPlaca(placaNormalizada).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RegraNegocioException("Placa já cadastrada para outro veículo.");
            }
        });

        validarAnoFabricacao(dto.anoFabricacao());

        Autoescola autoescola = autoescolaRepository.findByIdOptional(dto.idAutoescola())
                .orElseThrow(() -> new NotFoundException("Autoescola não encontrada."));

        CategoriaHabilitacao categoria = categoriaRepository.findByIdOptional(dto.idCategoriaHabilitacao())
                .orElseThrow(() -> new NotFoundException("Categoria de habilitação não encontrada."));

        veiculo.setPlaca(placaNormalizada);
        veiculo.setMarca(dto.marca().trim());
        veiculo.setModelo(dto.modelo().trim());
        veiculo.setAnoFabricacao(dto.anoFabricacao());
        if (dto.ativo() != null) {
            veiculo.setAtivo(dto.ativo());
        }
        veiculo.setAutoescola(autoescola);
        veiculo.setCategoriaHabilitacao(categoria);

        // Atualização consistente do documento único 1:1
        DocumentoVeiculo doc = veiculo.getDocumentoVeiculo();
        if (doc == null) {
            doc = new DocumentoVeiculo();
            veiculo.setDocumentoVeiculo(doc);
        }
        doc.setNumero(dto.documento().numero().trim());
        doc.setDataValidade(dto.documento().dataValidade());

        return VeiculoResponseDTO.fromEntity(veiculo);
    }

    @Transactional
    public void excluir(Long id) {
        Veiculo veiculo = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Veículo não encontrado."));

        long totalAgendamentos = agendamentoRepository.countByVeiculo(id);
        if (totalAgendamentos > 0) {
            throw new RegraNegocioException("Não é possível excluir o veículo pois existem "
                    + totalAgendamentos + " agendamentos vinculados ao seu histórico. "
                    + "Para preservar a integridade dos dados históricos, recomenda-se desativar o veículo (ativo = false).");
        }

        repository.delete(veiculo);
        repository.flush();
    }

    private void validarAnoFabricacao(Integer ano) {
        int anoMaximo = Year.now().getValue() + 1;
        if (ano == null || ano < 1900 || ano > anoMaximo) {
            throw new RegraNegocioException("O ano de fabricação deve estar entre 1900 e " + anoMaximo + ".");
        }
    }

    private String normalizarPlaca(String placa) {
        return placa != null ? placa.replaceAll("[^A-Za-z0-9]", "").toUpperCase() : null;
    }
}
