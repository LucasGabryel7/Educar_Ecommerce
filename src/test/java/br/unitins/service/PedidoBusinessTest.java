package br.unitins.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AlunoDTO;
import br.unitins.dto.AlunoResponseDTO;
import br.unitins.dto.AulaTeoricaDTO;
import br.unitins.dto.AulaTeoricaResponseDTO;
import br.unitins.dto.ItemPedidoDTO;
import br.unitins.dto.PedidoDTO;
import br.unitins.dto.PedidoResponseDTO;
import br.unitins.model.Aluno;      
import br.unitins.model.StatusPedido;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AlunoRepository;
import br.unitins.repository.AulaTeoricaRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@QuarkusTest
public class PedidoBusinessTest {

    @Inject
    PedidoService pedidoService;

    @Inject
    AlunoService alunoService;

    @Inject
    AulaTeoricaService aulaTeoricaService;

    @Inject
    AlunoRepository alunoRepository;

    @Inject
    AulaTeoricaRepository aulaTeoricaRepository;

    private Long idAlunoAtivo;
    private Long idAlunoInativo;
    private Long idAlunoAtivoNulo;
    private Long idOferta1;
    private Long idOferta2;

    @BeforeEach
    @Transactional
    public void setup() {
        AlunoResponseDTO aluno1 = alunoRepository.findByCpf("11122233399")
                .map(AlunoResponseDTO::fromEntity)
                .orElseGet(() -> alunoService.cadastrar(new AlunoDTO(
                        "Aluno Pedido Teste Ativo",
                        "111.222.333-99",
                        "(63) 98888-1111",
                        "aluno.pedido.ativo@email.com",
                        true
                )));
        idAlunoAtivo = aluno1.id();

        AlunoResponseDTO aluno2 = alunoRepository.findByCpf("22233344499")
                .map(AlunoResponseDTO::fromEntity)
                .orElseGet(() -> alunoService.cadastrar(new AlunoDTO(
                        "Aluno Pedido Teste Inativo",
                        "222.333.444-99",
                        "(63) 98888-2222",
                        "aluno.pedido.inativo@email.com",
                        false
                )));
        idAlunoInativo = aluno2.id();

        Aluno alunoNulo = alunoRepository.findByCpf("33344455599").orElseGet(() -> {
            Aluno a = new Aluno();
            a.setNome("Aluno Pedido Teste Ativo Nulo");
            a.setCpf("33344455599");
            a.setTelefone("9888-3333");
            a.setEmail("aluno.pedido.ativonulo@email.com");
            a.setAtivo(null);
            alunoRepository.persist(a);
            return a;
        });
        idAlunoAtivoNulo = alunoNulo.getId();

        AulaTeoricaResponseDTO oferta1 = aulaTeoricaRepository.find("nome", "Legislação para Pedido Teste 1").firstResultOptional()
                .map(AulaTeoricaResponseDTO::fromEntity)
                .orElseGet(() -> aulaTeoricaService.cadastrar(new AulaTeoricaDTO(
                        "Legislação para Pedido Teste 1",
                        "Módulo 1 de legislação",
                        new BigDecimal("50.00"),
                        10
                )));
        idOferta1 = oferta1.id();

        AulaTeoricaResponseDTO oferta2 = aulaTeoricaRepository.find("nome", "Primeiros Socorros Pedido Teste 2").firstResultOptional()
                .map(AulaTeoricaResponseDTO::fromEntity)
                .orElseGet(() -> aulaTeoricaService.cadastrar(new AulaTeoricaDTO(
                        "Primeiros Socorros Pedido Teste 2",
                        "Módulo 2 de primeiros socorros",
                        new BigDecimal("75.00"),
                        8
                )));
        idOferta2 = oferta2.id();
    }

    @Test
    public void testCadastrarPedidoComSucessoECalculoDeValores() {
        // Aluno ativo aceito com sucesso
        List<ItemPedidoDTO> itens = List.of(
                new ItemPedidoDTO(idOferta1, 2),
                new ItemPedidoDTO(idOferta2, 3)
        );

        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, itens);
        PedidoResponseDTO pedido = pedidoService.cadastrar(dto);

        assertNotNull(pedido.id());
        assertEquals(idAlunoAtivo, pedido.idAluno());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, pedido.status());
        assertNotNull(pedido.dataCriacao());
        assertEquals(0, new BigDecimal("325.00").compareTo(pedido.valorTotal()));
        assertEquals(2, pedido.itens().size());

        // Validar item 1
        var item1 = pedido.itens().stream().filter(i -> i.idOferta().equals(idOferta1)).findFirst().orElseThrow();
        assertEquals("Legislação para Pedido Teste 1", item1.nomeOferta());
        assertEquals(0, new BigDecimal("50.00").compareTo(item1.precoUnitario()));
        assertEquals(2, item1.quantidade());
        assertEquals(0, new BigDecimal("100.00").compareTo(item1.subtotal()));

        // Validar item 2
        var item2 = pedido.itens().stream().filter(i -> i.idOferta().equals(idOferta2)).findFirst().orElseThrow();
        assertEquals("Primeiros Socorros Pedido Teste 2", item2.nomeOferta());
        assertEquals(0, new BigDecimal("75.00").compareTo(item2.precoUnitario()));
        assertEquals(3, item2.quantidade());
        assertEquals(0, new BigDecimal("225.00").compareTo(item2.subtotal()));

        // Limpeza
        pedidoService.excluir(pedido.id());
    }

    @Test
    public void testRejeitarPedidoAlunoInexistente() {
        PedidoDTO dto = new PedidoDTO(999999L, List.of(new ItemPedidoDTO(idOferta1, 1)));
        assertThrows(NotFoundException.class, () -> pedidoService.cadastrar(dto));
    }

    @Test
    public void testRejeitarPedidoAlunoInativo() {
        PedidoDTO dto = new PedidoDTO(idAlunoInativo, List.of(new ItemPedidoDTO(idOferta1, 1)));
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> pedidoService.cadastrar(dto));
        assertEquals("O aluno informado está inativo no sistema.", ex.getMessage());
    }

    @Test
    public void testRejeitarPedidoAlunoComAtivoNulo() {
        // Validação estrita: !Boolean.TRUE.equals(aluno.getAtivo())
        PedidoDTO dto = new PedidoDTO(idAlunoAtivoNulo, List.of(new ItemPedidoDTO(idOferta1, 1)));
        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> pedidoService.cadastrar(dto));
        assertEquals("O aluno informado está inativo no sistema.", ex.getMessage());
    }

    @Test
    public void testRejeitarPedidoSemItens() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of());
        assertThrows(RegraNegocioException.class, () -> pedidoService.cadastrar(dto));
    }

    @Test
    public void testRejeitarPedidoOfertaInexistente() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of(new ItemPedidoDTO(999999L, 1)));
        assertThrows(NotFoundException.class, () -> pedidoService.cadastrar(dto));
    }

    @Test
    public void testRejeitarPedidoQuantidadeInvalida() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of(new ItemPedidoDTO(idOferta1, 0)));
        assertThrows(RegraNegocioException.class, () -> pedidoService.cadastrar(dto));
    }

    @Test
    public void testConsultarPorIdEListarTodos() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of(new ItemPedidoDTO(idOferta1, 1)));
        PedidoResponseDTO pedido = pedidoService.cadastrar(dto);

        PedidoResponseDTO encontrado = pedidoService.buscarPorId(pedido.id());
        assertNotNull(encontrado);
        assertEquals(pedido.id(), encontrado.id());

        List<PedidoResponseDTO> todos = pedidoService.listarTodos();
        assertTrue(todos.stream().anyMatch(p -> p.id().equals(pedido.id())));

        pedidoService.excluir(pedido.id());
    }

    @Test
    public void testConsultarPedidosPorAluno() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of(new ItemPedidoDTO(idOferta1, 1)));
        PedidoResponseDTO pedido = pedidoService.cadastrar(dto);

        List<PedidoResponseDTO> pedidosDoAluno = pedidoService.buscarPorAluno(idAlunoAtivo);
        assertTrue(pedidosDoAluno.stream().anyMatch(p -> p.id().equals(pedido.id())));

        assertThrows(NotFoundException.class, () -> pedidoService.buscarPorAluno(999999L));

        pedidoService.excluir(pedido.id());
    }

    @Test
    public void testConsultarPedidosPorStatus() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of(new ItemPedidoDTO(idOferta1, 1)));
        PedidoResponseDTO pedido = pedidoService.cadastrar(dto);

        List<PedidoResponseDTO> aguardando = pedidoService.buscarPorStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        assertTrue(aguardando.stream().anyMatch(p -> p.id().equals(pedido.id())));

        List<PedidoResponseDTO> aguardandoStr = pedidoService.buscarPorStatus("AGUARDANDO_PAGAMENTO");
        assertTrue(aguardandoStr.stream().anyMatch(p -> p.id().equals(pedido.id())));

        assertThrows(RegraNegocioException.class, () -> pedidoService.buscarPorStatus("STATUS_INVALIDO"));

        pedidoService.excluir(pedido.id());
    }

    @Test
    public void testAtualizarStatusPreservandoValoresEItens() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of(new ItemPedidoDTO(idOferta1, 2)));
        PedidoResponseDTO pedido = pedidoService.cadastrar(dto);

        BigDecimal valorTotalOriginal = pedido.valorTotal();
        int qtdItensOriginal = pedido.itens().size();

        // Atualizar usando Enum
        PedidoResponseDTO pago = pedidoService.atualizarStatus(pedido.id(), StatusPedido.PAGO);
        assertEquals(StatusPedido.PAGO, pago.status());
        // Garantir que valores monetários e itens permanecem inalterados
        assertEquals(0, valorTotalOriginal.compareTo(pago.valorTotal()));
        assertEquals(qtdItensOriginal, pago.itens().size());
        assertEquals(pedido.itens().get(0).precoUnitario(), pago.itens().get(0).precoUnitario());
        assertEquals(pedido.itens().get(0).subtotal(), pago.itens().get(0).subtotal());

        // Atualizar usando String
        PedidoResponseDTO cancelado = pedidoService.atualizarStatus(pedido.id(), "CANCELADO");
        assertEquals(StatusPedido.CANCELADO, cancelado.status());
        assertEquals(0, valorTotalOriginal.compareTo(cancelado.valorTotal()));
        assertEquals(qtdItensOriginal, cancelado.itens().size());

        // Rejeitar status inválido
        assertThrows(RegraNegocioException.class, () -> pedidoService.atualizarStatus(pedido.id(), "INVALIDO"));

        // Rejeitar pedido inexistente
        assertThrows(NotFoundException.class, () -> pedidoService.atualizarStatus(999999L, StatusPedido.PAGO));

        pedidoService.excluir(pedido.id());
    }

    @Test
    public void testExcluirPedidoEPreservarAlunoEOferta() {
        PedidoDTO dto = new PedidoDTO(idAlunoAtivo, List.of(new ItemPedidoDTO(idOferta1, 2)));
        PedidoResponseDTO pedido = pedidoService.cadastrar(dto);

        // Excluir pedido
        pedidoService.excluir(pedido.id());

        // Pedido não existe mais
        assertThrows(NotFoundException.class, () -> pedidoService.buscarPorId(pedido.id()));

        // Aluno continua existindo e intacto
        AlunoResponseDTO alunoAindaExiste = alunoService.buscarPorId(idAlunoAtivo);
        assertNotNull(alunoAindaExiste);

        // Oferta continua existindo e intacta
        AulaTeoricaResponseDTO ofertaAindaExiste = aulaTeoricaService.buscarPorId(idOferta1);
        assertNotNull(ofertaAindaExiste);
    }
}
