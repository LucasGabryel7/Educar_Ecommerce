package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AulaTeoricaDTO;
import br.unitins.dto.ItemPacoteAulaDTO;
import br.unitins.dto.PacoteAulasDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class PacoteAulasResourceTest {

    private Long idOfertaBase;

    @BeforeEach
    public void setup() {
        AulaTeoricaDTO aulaDto = new AulaTeoricaDTO(
                "Legislação para Pacote Rest",
                "Módulo básico de legislação",
                new BigDecimal("40.00"),
                10
        );

        Integer aulaId = given()
                .contentType(ContentType.JSON)
                .body(aulaDto)
            .when()
                .post("/aulas-teoricas")
            .then()
                .statusCode(201)
            .extract()
                .path("id");
        idOfertaBase = aulaId.longValue();
    }

    @Test
    public void testCadastrarEBuscarPorId() {
        ItemPacoteAulaDTO item = new ItemPacoteAulaDTO(idOfertaBase, 5);
        PacoteAulasDTO dto = new PacoteAulasDTO(
                "Pacote Promocional Rest",
                "Pacote com 5 aulas teóricas",
                new BigDecimal("180.00"),
                List.of(item)
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/pacotes-aulas")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Pacote Promocional Rest"))
                .body("quantidadeAulas", equalTo(5))
            .extract()
                .path("id");

        given()
            .when()
                .get("/pacotes-aulas/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("Pacote Promocional Rest"));
    }

    @Test
    public void testCadastrarSemItensRetorna400() {
        PacoteAulasDTO dtoInvalido = new PacoteAulasDTO(
                "Pacote Sem Itens",
                "Descrição",
                new BigDecimal("100.00"),
                List.of()
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/pacotes-aulas")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodos() {
        given()
            .when()
                .get("/pacotes-aulas")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/pacotes-aulas/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarEExcluir() {
        ItemPacoteAulaDTO item = new ItemPacoteAulaDTO(idOfertaBase, 3);
        PacoteAulasDTO dto = new PacoteAulasDTO(
                "Pacote Inicial Rest",
                "Pacote de teste para atualização",
                new BigDecimal("110.00"),
                List.of(item)
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/pacotes-aulas")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        ItemPacoteAulaDTO itemAtualizado = new ItemPacoteAulaDTO(idOfertaBase, 6);
        PacoteAulasDTO dtoAtualizado = new PacoteAulasDTO(
                "Pacote Inicial Expandido",
                "Pacote expandido com 6 aulas",
                new BigDecimal("210.00"),
                List.of(itemAtualizado)
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/pacotes-aulas/" + id)
            .then()
                .statusCode(200)
                .body("nome", equalTo("Pacote Inicial Expandido"))
                .body("quantidadeAulas", equalTo(6));

        given()
            .when()
                .delete("/pacotes-aulas/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/pacotes-aulas/" + id)
            .then()
                .statusCode(404);
    }
}
