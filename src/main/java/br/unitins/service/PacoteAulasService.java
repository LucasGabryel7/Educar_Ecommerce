package br.unitins.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.unitins.dto.ItemPacoteAulaDTO;
import br.unitins.dto.PacoteAulasDTO;
import br.unitins.dto.PacoteAulasResponseDTO;
import br.unitins.model.ItemPacoteAula;
import br.unitins.model.OfertaAula;
import br.unitins.model.PacoteAulas;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.OfertaAulaRepository;
import br.unitins.repository.PacoteAulasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class PacoteAulasService {

    @Inject
    PacoteAulasRepository repository;

    @Inject
    OfertaAulaRepository ofertaRepository;

    public List<PacoteAulasResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(PacoteAulasResponseDTO::fromEntity)
                .toList();
    }

    public PacoteAulasResponseDTO buscarPorId(Long id) {
        PacoteAulas pacote = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pacote de aulas não encontrado."));
        return PacoteAulasResponseDTO.fromEntity(pacote);
    }

    public List<PacoteAulasResponseDTO> buscarPorNome(String nome) {
        return repository.findByNome(nome)
                .stream()
                .map(PacoteAulasResponseDTO::fromEntity)
                .toList();
    }

    public List<PacoteAulasResponseDTO> buscarPorFaixaPreco(BigDecimal min, BigDecimal max) {
        return repository.findByFaixaPreco(min, max)
                .stream()
                .map(PacoteAulasResponseDTO::fromEntity)
                .toList();
    }

    @Transactional
    public PacoteAulasResponseDTO cadastrar(PacoteAulasDTO dto) {
        validarItensEntrada(dto.itens());

        // Validação antecipada e atômica de todas as ofertas referenciadas
        List<ItemPacoteAula> novosItens = processarItens(dto.itens(), null);

        PacoteAulas pacote = new PacoteAulas();
        pacote.setNome(dto.nome().trim());
        pacote.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        pacote.setPreco(dto.preco());

        for (ItemPacoteAula item : novosItens) {
            pacote.adicionarItem(item);
        }

        // Mantém a coluna do BD sincronizada com o cálculo dinâmico da soma dos itens
        pacote.setQuantidadeAulas(pacote.getQuantidadeAulas());

        repository.persist(pacote);
        return PacoteAulasResponseDTO.fromEntity(pacote);
    }

    @Transactional
    public PacoteAulasResponseDTO atualizar(Long id, PacoteAulasDTO dto) {
        PacoteAulas pacote = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pacote de aulas não encontrado."));

        validarItensEntrada(dto.itens());

        // Validação antecipada e atômica de todas as ofertas referenciadas
        List<ItemPacoteAula> novosItens = processarItens(dto.itens(), id);

        pacote.setNome(dto.nome().trim());
        pacote.setDescricao(dto.descricao() != null ? dto.descricao().trim() : null);
        pacote.setPreco(dto.preco());

        // Atualização da composição de itens (com remoção de órfãos pelo JPA)
        pacote.getItens().clear();
        for (ItemPacoteAula item : novosItens) {
            pacote.adicionarItem(item);
        }

        pacote.setQuantidadeAulas(pacote.getQuantidadeAulas());

        return PacoteAulasResponseDTO.fromEntity(pacote);
    }

    @Transactional
    public void excluir(Long id) {
        PacoteAulas pacote = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pacote de aulas não encontrado."));

        // A exclusão do pacote remove seus ItemPacoteAula por composição (cascade ALL + orphanRemoval),
        // preservando intactas as entidades OfertaAula (AulaPratica ou AulaTeorica) referenciadas.
        repository.delete(pacote);
        repository.flush();
    }

    private void validarItensEntrada(List<ItemPacoteAulaDTO> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("O pacote de aulas deve possuir pelo menos um item.");
        }
    }

    private List<ItemPacoteAula> processarItens(List<ItemPacoteAulaDTO> itensDTO, Long pacoteIdAtual) {
        List<ItemPacoteAula> itens = new ArrayList<>();

        for (ItemPacoteAulaDTO itemDTO : itensDTO) {
            if (itemDTO.quantidade() == null || itemDTO.quantidade() < 1) {
                throw new RegraNegocioException("A quantidade de aulas de cada item deve ser no mínimo 1.");
            }

            OfertaAula oferta = ofertaRepository.findByIdOptional(itemDTO.idOferta())
                    .orElseThrow(() -> new NotFoundException("Oferta de aula com ID " + itemDTO.idOferta() + " não encontrada."));

            // Regra de negócio: um pacote não pode conter outro pacote como item
            if (oferta instanceof PacoteAulas) {
                throw new RegraNegocioException("Um pacote de aulas não pode conter outro pacote de aulas como item.");
            }

            ItemPacoteAula item = new ItemPacoteAula();
            item.setOferta(oferta);
            item.setQuantidade(itemDTO.quantidade());
            itens.add(item);
        }

        return itens;
    }
}
