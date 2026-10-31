package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AgendamentoAulaDTO;
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
import br.unitins.repository.AlunoRepository;
import br.unitins.repository.AulaPraticaRepository;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.repository.InstrutorRepository;
import br.unitins.repository.VeiculoRepository;
import br.unitins.service.AlunoService;
import br.unitins.service.AulaPraticaService;
import br.unitins.service.AutoescolaService;
import br.unitins.service.CategoriaHabilitacaoService;
import br.unitins.service.InstrutorService;
import br.unitins.service.VeiculoService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
public class AgendamentoAulaResourceTest {

    @Inject
    AutoescolaRepository autoescolaRepository;

    @Inject
    AutoescolaService autoescolaService;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    CategoriaHabilitacaoService categoriaService;

    @Inject
    AlunoRepository alunoRepository;

    @Inject
    AlunoService alunoService;

    @Inject
    InstrutorRepository instrutorRepository;

    @Inject
    InstrutorService instrutorService;

    @Inject
    VeiculoRepository veiculoRepository;

    @Inject
    VeiculoService veiculoService;

    @Inject
    AulaPraticaRepository aulaPraticaRepository;

    @Inject
    AulaPraticaService aulaPraticaService;

    private Long idAluno;
    private Long idInstrutor;
    private Long idVeiculo;
    private Long idAulaPratica;

    @BeforeEach
    public void setup() {
        // 1. Autoescola
        AutoescolaResponseDTO auto = autoescolaRepository.findByCnpj("77111222000199")
                .map(AutoescolaResponseDTO::fromEntity)
                .orElseGet(() -> autoescolaService.cadastrar(new AutoescolaDTO(
                        "Autoescola Base Agenda",
                        "77.111.222/0001-99",
                        "3215-7777",
                        "base.agenda@autoescola.com",
                        "Rua Agenda, 1"
                )));

        // 2. Categoria B
        CategoriaHabilitacaoResponseDTO cat = categoriaRepository.find("upper(nome)", "B").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("B", "Carros de Passeio")));

        // 3. Aluno
        AlunoResponseDTO aluno = alunoRepository.findByCpf("77766655588")
                .map(AlunoResponseDTO::fromEntity)
                .orElseGet(() -> alunoService.cadastrar(new AlunoDTO(
                        "Aluno Agenda Rest",
                        "777.666.555-88",
                        "9888-7777",
                        "aluno.agenda.rest@email.com",
                        true
                )));
        idAluno = aluno.id();

        // 4. Instrutor
        InstrutorResponseDTO inst = instrutorRepository.findByCpf("66655544477")
                .map(InstrutorResponseDTO::fromEntity)
                .orElseGet(() -> instrutorService.cadastrar(new InstrutorDTO(
                        "Instrutor Agenda Rest",
                        "666.555.444-77",
                        "REG-AGENDA-01",
                        "9888-6666",
                        true,
                        auto.id(),
                        List.of(cat.id())
                )));
        idInstrutor = inst.id();

        // 5. Veículo
        VeiculoResponseDTO veic = veiculoRepository.findByPlaca("AGE1B99")
                .map(VeiculoResponseDTO::fromEntity)
                .orElseGet(() -> veiculoService.cadastrar(new VeiculoDTO(
                        "AGE1B99",
                        "Fiat",
                        "Mobi",
                        2023,
                        true,
                        auto.id(),
                        cat.id(),
                        new DocumentoVeiculoDTO("CRLV-AGE-01", LocalDate.of(2028, 12, 31))
                )));
        idVeiculo = veic.id();

        // 6. Aula Prática
        AulaPraticaResponseDTO aula = aulaPraticaRepository.find("nome", "Aula Prática Cat B Agenda").firstResultOptional()
                .map(AulaPraticaResponseDTO::fromEntity)
                .orElseGet(() -> aulaPraticaService.cadastrar(new AulaPraticaDTO(
                        "Aula Prática Cat B Agenda",
                        "Aula para agendamento",
                        new BigDecimal("80.00"),
                        cat.id()
                )));
        idAulaPratica = aula.id();
    }

    @Test
    public void testCadastrarEBuscarPorId() {
        AgendamentoAulaDTO dto = new AgendamentoAulaDTO(
                LocalDate.of(2026, 12, 15),
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                StatusAgendamento.AGENDADO,
                idAluno,
                idInstrutor,
                idVeiculo,
                idAulaPratica
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/agendamentos-aulas")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("status", equalTo("AGENDADO"))
            .extract()
                .path("id");

        given()
            .when()
                .get("/agendamentos-aulas/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("status", equalTo("AGENDADO"));
    }

    @Test
    public void testRejeitarHorarioTerminoAnteriorAoInicio() {
        AgendamentoAulaDTO dtoInvalido = new AgendamentoAulaDTO(
                LocalDate.of(2026, 12, 16),
                LocalTime.of(10, 0),
                LocalTime.of(9, 0), // Término antes do início!
                StatusAgendamento.AGENDADO,
                idAluno,
                idInstrutor,
                idVeiculo,
                idAulaPratica
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/agendamentos-aulas")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodosEBuscarPorAluno() {
        given()
            .when()
                .get("/agendamentos-aulas")
            .then()
                .statusCode(200);

        given()
            .when()
                .get("/agendamentos-aulas/aluno/" + idAluno)
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/agendamentos-aulas/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarStatusEExcluir() {
        AgendamentoAulaDTO dto = new AgendamentoAulaDTO(
                LocalDate.of(2026, 12, 17),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                StatusAgendamento.AGENDADO,
                idAluno,
                idInstrutor,
                idVeiculo,
                idAulaPratica
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/agendamentos-aulas")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        given()
            .when()
                .patch("/agendamentos-aulas/" + id + "/status?status=CONFIRMADO")
            .then()
                .statusCode(200)
                .body("status", equalTo("CONFIRMADO"));

        given()
            .when()
                .delete("/agendamentos-aulas/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/agendamentos-aulas/" + id)
            .then()
                .statusCode(404);
    }
}
