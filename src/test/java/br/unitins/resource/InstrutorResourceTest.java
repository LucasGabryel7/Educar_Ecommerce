package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AutoescolaDTO;
import br.unitins.dto.AutoescolaResponseDTO;
import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.dto.InstrutorDTO;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.service.AutoescolaService;
import br.unitins.service.CategoriaHabilitacaoService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
public class InstrutorResourceTest {

    @Inject
    AutoescolaRepository autoescolaRepository;

    @Inject
    AutoescolaService autoescolaService;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    CategoriaHabilitacaoService categoriaService;

    private Long idAutoescola;
    private Long idCategoria;

    @BeforeEach
    public void setup() {
        AutoescolaResponseDTO auto = autoescolaRepository.findByCnpj("99111222000133")
                .map(AutoescolaResponseDTO::fromEntity)
                .orElseGet(() -> autoescolaService.cadastrar(new AutoescolaDTO(
                        "Autoescola Base Instrutores",
                        "99.111.222/0001-33",
                        "3215-9999",
                        "base.instrutor@autoescola.com",
                        "Rua Base, 10"
                )));
        idAutoescola = auto.id();

        CategoriaHabilitacaoResponseDTO cat = categoriaRepository.find("upper(nome)", "B").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("B", "Carros Passeio")));
        idCategoria = cat.id();
    }

    @Test
    public void testCadastrarEBuscarPorId() {
        InstrutorDTO dto = new InstrutorDTO(
                "Marcos Instrutor Rest",
                "555.666.777-11",
                "REG-REST-01",
                "(63) 98888-5555",
                true,
                idAutoescola,
                List.of(idCategoria)
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/instrutores")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Marcos Instrutor Rest"))
                .body("registroProfissional", equalTo("REG-REST-01"))
            .extract()
                .path("id");

        given()
            .when()
                .get("/instrutores/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("Marcos Instrutor Rest"));
    }

    @Test
    public void testCadastrarComDadosInvalidosRetorna400() {
        InstrutorDTO dtoInvalido = new InstrutorDTO(
                "",
                "123",
                "",
                "",
                true,
                null,
                List.of()
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/instrutores")
            .then()
                .statusCode(400);
    }

    @Test
    public void testCadastrarCpfDuplicadoRetorna400() {
        String cpf = "666.777.888-22";

        InstrutorDTO dto1 = new InstrutorDTO(
                "Instrutor Um",
                cpf,
                "REG-REST-10",
                "9888-6666",
                true,
                idAutoescola,
                List.of(idCategoria)
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto1)
            .when()
                .post("/instrutores")
            .then()
                .statusCode(201);

        InstrutorDTO dto2 = new InstrutorDTO(
                "Instrutor Dois",
                cpf,
                "REG-REST-11",
                "9888-6667",
                true,
                idAutoescola,
                List.of(idCategoria)
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto2)
            .when()
                .post("/instrutores")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodosEBuscarAtivos() {
        given()
            .when()
                .get("/instrutores")
            .then()
                .statusCode(200);

        given()
            .when()
                .get("/instrutores/ativos?ativo=true")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/instrutores/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarEExcluir() {
        InstrutorDTO dto = new InstrutorDTO(
                "Instrutor Exclusao",
                "777.888.999-33",
                "REG-REST-20",
                "9888-7777",
                true,
                idAutoescola,
                List.of(idCategoria)
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/instrutores")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        InstrutorDTO dtoAtualizado = new InstrutorDTO(
                "Instrutor Atualizado Rest",
                "777.888.999-33",
                "REG-REST-20",
                "9888-7999",
                false,
                idAutoescola,
                List.of(idCategoria)
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/instrutores/" + id)
            .then()
                .statusCode(200)
                .body("nome", equalTo("Instrutor Atualizado Rest"))
                .body("ativo", equalTo(false));

        given()
            .when()
                .delete("/instrutores/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/instrutores/" + id)
            .then()
                .statusCode(404);
    }
}
