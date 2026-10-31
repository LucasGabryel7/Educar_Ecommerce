package br.unitins.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AgendamentoAulaDTO;
import br.unitins.dto.AgendamentoAulaResponseDTO;
import br.unitins.dto.AlunoDTO;
import br.unitins.dto.AlunoResponseDTO;
import br.unitins.dto.AulaPraticaDTO;
import br.unitins.dto.AulaPraticaResponseDTO;
import br.unitins.dto.AutoescolaDTO;
import br.unitins.dto.AutoescolaResponseDTO;
import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.dto.DocumentoVeiculoDTO;
import br.unitins.dto.InstrutorDTO;
import br.unitins.dto.InstrutorResponseDTO;
import br.unitins.dto.VeiculoDTO;
import br.unitins.dto.VeiculoResponseDTO;
import br.unitins.model.StatusAgendamento;
import br.unitins.problem.RegraNegocioException;
import br.unitins.repository.AlunoRepository;
import br.unitins.repository.AulaPraticaRepository;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.InstrutorRepository;
import br.unitins.repository.VeiculoRepository;
import br.unitins.util.TestCleanup;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class AgendamentoAulaBusinessTest {

    @Inject
    AgendamentoAulaService agendamentoService;

    @Inject
    AlunoService alunoService;

    @Inject
    InstrutorService instrutorService;

    @Inject
    VeiculoService veiculoService;

    @Inject
    AutoescolaService autoescolaService;

    @Inject
    AulaPraticaService aulaPraticaService;

    @Inject
    CategoriaHabilitacaoService categoriaService;

    @Inject
    TestCleanup testCleanup;

    @Inject
    AlunoRepository alunoRepository;

    @Inject
    InstrutorRepository instrutorRepository;

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    AutoescolaRepository autoescolaRepository;

    @Inject
    AulaPraticaRepository aulaPraticaRepository;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    private Long idAutoescola;
    private Long idCatA;
    private Long idCatB;
    private Long idAluno;
    private Long idInstrutorB;
    private Long idVeiculoB;
    private Long idAulaPraticaB;
    private Long idAulaPraticaA;

    @BeforeEach
    public void setup() {
        // Limpa apenas agendamentos criados por testes anteriores desta classe.
        // SEGURANÇA: opera no banco autoescola_test (perfil %test),
        // jamais toca o banco de desenvolvimento autoescola.
        // O TestCleanup usa @Transactional próprio, garantindo commit antes do teste iniciar.
        testCleanup.limparAgendamentos();

        // 1. Autoescola
        AutoescolaResponseDTO auto = autoescolaRepository.findByCnpj("11222333000199")
                .map(AutoescolaResponseDTO::fromEntity)
                .orElseGet(() -> autoescolaService.cadastrar(new AutoescolaDTO(
                        "Autoescola Teste Agendamento", "11.222.333/0001-99", "3215-1111", "agendamento@autoescola.com", "Rua 1")));
        idAutoescola = auto.id();

        // 2. Categorias A e B
        CategoriaHabilitacaoResponseDTO catA = categoriaRepository.find("upper(nome)", "A").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("A", "Motos")));
        idCatA = catA.id();

        CategoriaHabilitacaoResponseDTO catB = categoriaRepository.find("upper(nome)", "B").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("B", "Carros")));
        idCatB = catB.id();

        // 3. Aulas Práticas
        AulaPraticaResponseDTO aulaB = aulaPraticaRepository.find("nome", "Aula Prática Cat B Agendamento").firstResultOptional()
                .map(AulaPraticaResponseDTO::fromEntity)
                .orElseGet(() -> aulaPraticaService.cadastrar(new AulaPraticaDTO(
                        "Aula Prática Cat B Agendamento", "Carro B", new BigDecimal("80.00"), idCatB)));
        idAulaPraticaB = aulaB.id();

        AulaPraticaResponseDTO aulaA = aulaPraticaRepository.find("nome", "Aula Prática Cat A Agendamento").firstResultOptional()
                .map(AulaPraticaResponseDTO::fromEntity)
                .orElseGet(() -> aulaPraticaService.cadastrar(new AulaPraticaDTO(
                        "Aula Prática Cat A Agendamento", "Moto A", new BigDecimal("70.00"), idCatA)));
        idAulaPraticaA = aulaA.id();

        // 4. Aluno
        AlunoResponseDTO aluno = alunoRepository.findByCpf("99988877701")
                .map(AlunoResponseDTO::fromEntity)
                .orElseGet(() -> alunoService.cadastrar(new AlunoDTO(
                        "Aluno Teste Agendamento", "999.888.777-01", "9999-1111", "aluno.agenda@test.com", true)));
        idAluno = aluno.id();

        // 5. Instrutor habilitado apenas na categoria B
        InstrutorResponseDTO instrutor = instrutorRepository.findByCpf("88877766601")
                .map(InstrutorResponseDTO::fromEntity)
                .orElseGet(() -> instrutorService.cadastrar(new InstrutorDTO(
                        "Instrutor B Teste", "888.777.666-01", "REG-AG-01", "9888-2222", true, idAutoescola, List.of(idCatB))));
        idInstrutorB = instrutor.id();

        // 6. Veículo categoria B
        VeiculoResponseDTO veiculo = veiculoRepository.findByPlaca("AGE1A01")
                .map(VeiculoResponseDTO::fromEntity)
                .orElseGet(() -> veiculoService.cadastrar(new VeiculoDTO(
                        "AGE-1A01", "Fiat", "Mobi", 2023, true, idAutoescola, idCatB,
                        new DocumentoVeiculoDTO("CRLV-AG-01", LocalDate.of(2027, 12, 31)))));
        idVeiculoB = veiculo.id();
    }

    @Test
    public void testRejeitarHorarioTerminoAnteriorOuIgualInicio() {
        AgendamentoAulaDTO dtoInvalido = new AgendamentoAulaDTO(
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0),
                LocalTime.of(9, 0), // Término antes do início!
                StatusAgendamento.AGENDADO,
                idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> agendamentoService.cadastrar(dtoInvalido));
        assertEquals("O horário de término deve ser posterior ao horário de início.", ex.getMessage());

        AgendamentoAulaDTO dtoIgual = new AgendamentoAulaDTO(
                LocalDate.of(2026, 12, 1),
                LocalTime.of(10, 0),
                LocalTime.of(10, 0), // Término igual ao início!
                StatusAgendamento.AGENDADO,
                idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB
        );

        assertThrows(RegraNegocioException.class, () -> agendamentoService.cadastrar(dtoIgual));
    }

    @Test
    public void testRejeitarIncompatibilidadeDeCategoriaInstrutorEVeiculo() {
        // Tentar agendar uma aula prática de Moto (Cat A) usando um instrutor e veículo de Cat B
        AgendamentoAulaDTO dtoIncompativel = new AgendamentoAulaDTO(
                LocalDate.of(2026, 12, 1),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                StatusAgendamento.AGENDADO,
                idAluno, idInstrutorB, idVeiculoB, idAulaPraticaA // Aula de moto!
        );

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> agendamentoService.cadastrar(dtoIncompativel));
        assertTrue(ex.getMessage().contains("não possui a categoria de habilitação exigida"));
    }

    @Test
    public void testRejeitarConflitoDeHorarioParaMesmoInstrutor() {
        LocalDate data = LocalDate.of(2026, 12, 2);

        // Agendamento 1: 08:00 às 09:00
        AgendamentoAulaDTO ag1 = new AgendamentoAulaDTO(
                data, LocalTime.of(8, 0), LocalTime.of(9, 0),
                StatusAgendamento.AGENDADO, idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB);
        agendamentoService.cadastrar(ag1);

        // Agendamento 2 conflitante: 08:30 às 09:30 (sobrepõe o instrutor)
        AgendamentoAulaDTO ag2 = new AgendamentoAulaDTO(
                data, LocalTime.of(8, 30), LocalTime.of(9, 30),
                StatusAgendamento.AGENDADO, idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> agendamentoService.cadastrar(ag2));
        assertTrue(ex.getMessage().contains("Conflito de horário: o instrutor já possui aula agendada"));
    }

    @Test
    public void testAceitarHorariosConsecutivosSemSobreposicao() {
        LocalDate data = LocalDate.of(2026, 12, 3);

        // Aula 1: 08:00 às 09:00
        AgendamentoAulaDTO ag1 = new AgendamentoAulaDTO(
                data, LocalTime.of(8, 0), LocalTime.of(9, 0),
                StatusAgendamento.AGENDADO, idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB);
        AgendamentoAulaResponseDTO r1 = agendamentoService.cadastrar(ag1);
        assertNotNull(r1.id());

        // Aula 2: 09:00 às 10:00 (termina exatamente quando a outra começa -> consecutivo sem conflito!)
        AgendamentoAulaDTO ag2 = new AgendamentoAulaDTO(
                data, LocalTime.of(9, 0), LocalTime.of(10, 0),
                StatusAgendamento.AGENDADO, idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB);
        AgendamentoAulaResponseDTO r2 = agendamentoService.cadastrar(ag2);
        assertNotNull(r2.id());
    }

    @Test
    public void testIgnorarAgendamentoCanceladoAoVerificarConflitos() {
        LocalDate data = LocalDate.of(2026, 12, 4);

        // Cadastra aula 08:00 às 09:00
        AgendamentoAulaDTO ag1 = new AgendamentoAulaDTO(
                data, LocalTime.of(8, 0), LocalTime.of(9, 0),
                StatusAgendamento.AGENDADO, idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB);
        AgendamentoAulaResponseDTO r1 = agendamentoService.cadastrar(ag1);

        // Cancela a aula
        agendamentoService.atualizarStatus(r1.id(), StatusAgendamento.CANCELADO);

        // Novo agendamento no mesmo horário 08:00 às 09:00 deve ser aceito porque a anterior está CANCELADA
        AgendamentoAulaDTO ag2 = new AgendamentoAulaDTO(
                data, LocalTime.of(8, 0), LocalTime.of(9, 0),
                StatusAgendamento.AGENDADO, idAluno, idInstrutorB, idVeiculoB, idAulaPraticaB);
        AgendamentoAulaResponseDTO r2 = agendamentoService.cadastrar(ag2);
        assertNotNull(r2.id());
    }
}
