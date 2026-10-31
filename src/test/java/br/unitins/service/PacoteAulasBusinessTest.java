package br.unitins.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AulaPraticaDTO;
import br.unitins.dto.AulaPraticaResponseDTO;
import br.unitins.dto.AulaTeoricaDTO;
import br.unitins.dto.AulaTeoricaResponseDTO;
import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.dto.ItemPacoteAulaDTO;
import br.unitins.dto.PacoteAulasDTO;
import br.unitins.dto.PacoteAulasResponseDTO;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AulaPraticaRepository;
import br.unitins.repository.AulaTeoricaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.PacoteAulasRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class PacoteAulasBusinessTest {

    @Inject
    PacoteAulasService pacoteService;

    @Inject
    AulaPraticaService aulaPraticaService;

    @Inject
    AulaTeoricaService aulaTeoricaService;

    @Inject
    CategoriaHabilitacaoService categoriaService;

    @Inject
    PacoteAulasRepository pacoteRepository;

    @Inject
    AulaPraticaRepository aulaPraticaRepository;

    @Inject
    AulaTeoricaRepository aulaTeoricaRepository;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    private Long idCatB;
    private Long idPraticaB;
    private Long idTeorica;

    @BeforeEach
    @Transactional
    public void setup() {
        // Obter ou criar Categoria B
        CategoriaHabilitacaoResponseDTO cat = categoriaRepository.find("upper(nome)", "B").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("B", "Carros de passeio")));
        idCatB = cat.id();

        // Obter ou criar Aula Prática
        AulaPraticaResponseDTO pratica = aulaPraticaRepository.find("nome", "Aula Prática Carro Teste").firstResultOptional()
                .map(AulaPraticaResponseDTO::fromEntity)
                .orElseGet(() -> aulaPraticaService.cadastrar(new AulaPraticaDTO("Aula Prática Carro Teste", "Aula de direção veicular", new BigDecimal("70.00"), idCatB)));
        idPraticaB = pratica.id();

        // Obter ou criar Aula Teórica
        AulaTeoricaResponseDTO teorica = aulaTeoricaRepository.find("nome", "Legislação de Trânsito Teste").firstResultOptional()
                .map(AulaTeoricaResponseDTO::fromEntity)
                .orElseGet(() -> aulaTeoricaService.cadastrar(new AulaTeoricaDTO("Legislação de Trânsito Teste", "Módulo de legislação", new BigDecimal("40.00"), 10)));
        idTeorica = teorica.id();
    }

    @Test
    public void testCadastrarEAtualizarPacoteComComposicaoDeItens() {
        // Criar pacote com 2 itens (5 práticas + 3 teóricas = 8 aulas)
        List<ItemPacoteAulaDTO> itens = List.of(
                new ItemPacoteAulaDTO(idPraticaB, 5),
                new ItemPacoteAulaDTO(idTeorica, 3)
        );

        PacoteAulasDTO dto = new PacoteAulasDTO(
                "Pacote Completo Cat B Teste",
                "Pacote com aulas práticas e teóricas",
                new BigDecimal("450.00"),
                itens
        );

        PacoteAulasResponseDTO criado = pacoteService.cadastrar(dto);

        assertNotNull(criado.id());
        assertEquals("Pacote Completo Cat B Teste", criado.nome());
        assertEquals(8, criado.quantidadeAulas()); // 5 + 3
        assertEquals(2, criado.itens().size());

        // Atualizar pacote para 10 práticas e 5 teóricas = 15 aulas
        List<ItemPacoteAulaDTO> novosItens = List.of(
                new ItemPacoteAulaDTO(idPraticaB, 10),
                new ItemPacoteAulaDTO(idTeorica, 5)
        );
        PacoteAulasDTO dtoAtualizado = new PacoteAulasDTO(
                "Pacote Completo Cat B Atualizado",
                "Pacote expandido",
                new BigDecimal("750.00"),
                novosItens
        );

        PacoteAulasResponseDTO atualizado = pacoteService.atualizar(criado.id(), dtoAtualizado);
        assertEquals(15, atualizado.quantidadeAulas()); // 10 + 5
        assertEquals(2, atualizado.itens().size());

        // Limpeza
        pacoteService.excluir(criado.id());
    }

    @Test
    public void testRejeitarPacoteSemItens() {
        PacoteAulasDTO dto = new PacoteAulasDTO(
                "Pacote Vazio", "Sem itens", new BigDecimal("100.00"), List.of()
        );

        assertThrows(RegraNegocioException.class, () -> pacoteService.cadastrar(dto));
    }

    @Test
    public void testRejeitarPacoteContendoOutroPacote() {
        // Primeiro cria um pacote normal
        List<ItemPacoteAulaDTO> itens = List.of(new ItemPacoteAulaDTO(idPraticaB, 2));
        PacoteAulasDTO dto1 = new PacoteAulasDTO("Subpacote", "Pacote base", new BigDecimal("120.00"), itens);
        PacoteAulasResponseDTO pacote1 = pacoteService.cadastrar(dto1);

        // Tenta criar um novo pacote contendo o pacote1 como item
        List<ItemPacoteAulaDTO> itensComPacote = List.of(
                new ItemPacoteAulaDTO(pacote1.id(), 1)
        );
        PacoteAulasDTO dto2 = new PacoteAulasDTO("Superpacote", "Pacote aninhado", new BigDecimal("200.00"), itensComPacote);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> pacoteService.cadastrar(dto2));
        assertEquals("Um pacote de aulas não pode conter outro pacote de aulas como item.", ex.getMessage());

        // Limpeza
        pacoteService.excluir(pacote1.id());
    }

    @Test
    public void testPreservarOfertasAoExcluirPacote() {
        // Cria pacote
        List<ItemPacoteAulaDTO> itens = List.of(new ItemPacoteAulaDTO(idPraticaB, 2));
        PacoteAulasDTO dto = new PacoteAulasDTO("Pacote Teste Exclusao", "Desc", new BigDecimal("100.00"), itens);
        PacoteAulasResponseDTO pacote = pacoteService.cadastrar(dto);

        // Exclui pacote
        pacoteService.excluir(pacote.id());

        // A aula prática original continua existindo e intacta!
        AulaPraticaResponseDTO praticaAindaExiste = aulaPraticaService.buscarPorId(idPraticaB);
        assertNotNull(praticaAindaExiste);
        assertEquals(idPraticaB, praticaAindaExiste.id());
    }
}
