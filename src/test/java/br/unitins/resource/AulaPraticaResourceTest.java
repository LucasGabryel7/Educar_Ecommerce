package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AulaPraticaDTO;
import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.service.CategoriaHabilitacaoService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
public class AulaPraticaResourceTest {

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    CategoriaHabilitacaoService categoriaService;

    private Long idCategoria;

    @BeforeEach
    public void setup() {
        CategoriaHabilitacaoResponseDTO cat = categoriaRepository.find("upper(nome)", "B").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("B", "Carros de Passeio")));
        idCategoria = cat.id();
    }

    @Test
    public void testCadastrarEBuscarPorId() {
        AulaPraticaDTO dto = new AulaPraticaDTO(
                "Aula Prática Baliza Rest",
                "Treinamento intensivo de baliza e estacionamento",
                new BigDecimal("85.00"),
                idCategoria
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/aulas-praticas")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Aula Prática Baliza Rest"))
                .body("preco", equalTo(85.00f))
            .extract()
                .path("id");

        given()
            .when()
                .get("/aulas-praticas/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("Aula Prática Baliza Rest"));
    }

    @Test
    public void testCadastrarPrecoInvalidoRetorna400() {
        AulaPraticaDTO dtoInvalido = new AulaPraticaDTO(
                "",
                "",
                new BigDecimal("-20.00"),
                null
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/aulas-praticas")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodos() {
        given()
            .when()
                .get("/aulas-praticas")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/aulas-praticas/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarEExcluir() {
        AulaPraticaDTO dto = new AulaPraticaDTO(
                "Aula Prática Percurso",
                "Circuito de rua",
                new BigDecimal("90.00"),
                idCategoria
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/aulas-praticas")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        AulaPraticaDTO dtoAtualizado = new AulaPraticaDTO(
                "Aula Prática Percurso Noturno",
                "Circuito de rua noturno",
                new BigDecimal("110.00"),
                idCategoria
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/aulas-praticas/" + id)
            .then()
                .statusCode(200)
                .body("nome", equalTo("Aula Prática Percurso Noturno"))
                .body("preco", equalTo(110.00f));

        given()
            .when()
                .delete("/aulas-praticas/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/aulas-praticas/" + id)
            .then()
                .statusCode(404);
    }
}
