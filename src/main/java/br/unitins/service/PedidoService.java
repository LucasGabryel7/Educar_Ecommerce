package br.unitins.service;

import java.math.BigDecimal;
import java.util.List;

import br.unitins.dto.ItemPedidoDTO;
import br.unitins.dto.PedidoDTO;
import br.unitins.dto.PedidoResponseDTO;
import br.unitins.model.Aluno;
import br.unitins.model.ItemPedido;
import br.unitins.model.OfertaAula;
import br.unitins.model.Pedido;
import br.unitins.model.StatusPedido;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AlunoRepository;
import br.unitins.repository.ItemPedidoRepository;
import br.unitins.repository.OfertaAulaRepository;
import br.unitins.repository.PedidoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class PedidoService {

    @Inject
    PedidoRepository repository;

    @Inject
    ItemPedidoRepository itemPedidoRepository;

    @Inject
    AlunoRepository alunoRepository;

    @Inject
    OfertaAulaRepository ofertaRepository;

    public List<PedidoResponseDTO> listarTodos() {
        return repository.listAll()
                .stream()
                .map(PedidoResponseDTO::fromEntity)
                .toList();
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado."));
        return PedidoResponseDTO.fromEntity(pedido);
    }

    public List<PedidoResponseDTO> buscarPorAluno(Long alunoId) {
        if (alunoId == null) {
            throw new RegraNegocioException("O ID do aluno deve ser informado.");
        }

        alunoRepository.findByIdOptional(alunoId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        return repository.findByAluno(alunoId)
                .stream()
                .map(PedidoResponseDTO::fromEntity)
                .toList();
    }

    public List<PedidoResponseDTO> buscarPorStatus(StatusPedido status) {
        if (status == null) {
            throw new RegraNegocioException("O status do pedido deve ser informado.");
        }

        return repository.findByStatus(status)
                .stream()
                .map(PedidoResponseDTO::fromEntity)
                .toList();
    }

    public List<PedidoResponseDTO> buscarPorStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            throw new RegraNegocioException("O status do pedido deve ser informado.");
        }

        StatusPedido status;
        try {
            status = StatusPedido.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Status de pedido inválido: " + statusStr);
        }

        return buscarPorStatus(status);
    }

    @Transactional
    public PedidoResponseDTO cadastrar(PedidoDTO dto) {
        if (dto == null) {
            throw new RegraNegocioException("Os dados do pedido são obrigatórios.");
        }

        if (dto.idAluno() == null) {
            throw new RegraNegocioException("O ID do aluno é obrigatório.");
        }

        Aluno aluno = alunoRepository.findByIdOptional(dto.idAluno())
                .orElseThrow(() -> new NotFoundException("Aluno com ID " + dto.idAluno() + " não encontrado."));

        if (!Boolean.TRUE.equals(aluno.getAtivo())) {
            throw new RegraNegocioException("O aluno informado está inativo no sistema.");
        }

        if (dto.itens() == null || dto.itens().isEmpty()) {
            throw new RegraNegocioException("O pedido deve possuir pelo menos um item.");
        }

        Pedido pedido = new Pedido();
        pedido.setAluno(aluno);
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemPedidoDTO itemDTO : dto.itens()) {
            if (itemDTO == null) {
                throw new RegraNegocioException("Item de pedido não pode ser nulo.");
            }

            if (itemDTO.idOferta() == null) {
                throw new RegraNegocioException("O ID da oferta é obrigatório.");
            }

            if (itemDTO.quantidade() == null || itemDTO.quantidade() < 1) {
                throw new RegraNegocioException("A quantidade do item deve ser no mínimo 1.");
            }

            OfertaAula oferta = ofertaRepository.findByIdOptional(itemDTO.idOferta())
                    .orElseThrow(() -> new NotFoundException("Oferta de aula com ID " + itemDTO.idOferta() + " não encontrada."));

            if (oferta.getPreco() == null || oferta.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraNegocioException("A oferta '" + oferta.getNome() + "' possui preço inválido para comercialização.");
            }

            BigDecimal precoUnitario = oferta.getPreco();
            BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(itemDTO.quantidade()));

            ItemPedido item = new ItemPedido();
            item.setOferta(oferta);
            item.setNomeOferta(oferta.getNome());
            item.setPrecoUnitario(precoUnitario);
            item.setQuantidade(itemDTO.quantidade());
            item.setSubtotal(subtotal);

            pedido.adicionarItem(item);
            valorTotal = valorTotal.add(subtotal);
        }

        pedido.setValorTotal(valorTotal);

        repository.persist(pedido);
        return PedidoResponseDTO.fromEntity(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado."));

        if (novoStatus == null) {
            throw new RegraNegocioException("O novo status não pode ser nulo.");
        }

        pedido.setStatus(novoStatus);
        return PedidoResponseDTO.fromEntity(pedido);
    }

    @Transactional
    public PedidoResponseDTO atualizarStatus(Long id, String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            throw new RegraNegocioException("O novo status não pode ser nulo.");
        }

        StatusPedido novoStatus;
        try {
            novoStatus = StatusPedido.valueOf(statusStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraNegocioException("Status de pedido inválido: " + statusStr);
        }

        return atualizarStatus(id, novoStatus);
    }

    @Transactional
    public void excluir(Long id) {
        Pedido pedido = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pedido não encontrado."));

        repository.delete(pedido);
        repository.flush();
    }
}
