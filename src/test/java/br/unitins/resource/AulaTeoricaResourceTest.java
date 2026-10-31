package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import br.unitins.dto.AulaTeoricaDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class AulaTeoricaResourceTest {

    @Test
    public void testCadastrarEBuscarPorId() {
        AulaTeoricaDTO dto = new AulaTeoricaDTO(
                "Direção Defensiva Rest",
                "Módulo conceitual e prático de direção defensiva",
                new BigDecimal("50.00"),
                16
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/aulas-teoricas")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Direção Defensiva Rest"))
                .body("cargaHoraria", equalTo(16))
            .extract()
                .path("id");

        given()
            .when()
                .get("/aulas-teoricas/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("Direção Defensiva Rest"));
    }

    @Test
    public void testCadastrarCargaHorariaInvalidaRetorna400() {
        AulaTeoricaDTO dtoInvalido = new AulaTeoricaDTO(
                "",
                "",
                new BigDecimal("-10.00"),
                -5
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/aulas-teoricas")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodos() {
        given()
            .when()
                .get("/aulas-teoricas")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/aulas-teoricas/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarEExcluir() {
        AulaTeoricaDTO dto = new AulaTeoricaDTO(
                "Primeiros Socorros no Trânsito",
                "Procedimentos básicos de emergência",
                new BigDecimal("45.00"),
                12
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/aulas-teoricas")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        AulaTeoricaDTO dtoAtualizado = new AulaTeoricaDTO(
                "Primeiros Socorros Avançado",
                "Módulo expandido de primeiros socorros",
                new BigDecimal("60.00"),
                18
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/aulas-teoricas/" + id)
            .then()
                .statusCode(200)
                .body("nome", equalTo("Primeiros Socorros Avançado"))
                .body("cargaHoraria", equalTo(18));

        given()
            .when()
                .delete("/aulas-teoricas/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/aulas-teoricas/" + id)
            .then()
                .statusCode(404);
    }
}
