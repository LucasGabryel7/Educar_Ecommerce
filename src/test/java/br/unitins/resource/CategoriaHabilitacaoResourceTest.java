package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.Test;

import br.unitins.dto.CategoriaHabilitacaoDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class CategoriaHabilitacaoResourceTest {

    @Test
    public void testCadastrarEBuscarPorId() {
        CategoriaHabilitacaoDTO dto = new CategoriaHabilitacaoDTO(
                "D",
                "Veículos de transporte de passageiros (acima de 8 passageiros)"
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/categorias-habilitacao")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("D"))
            .extract()
                .path("id");

        given()
            .when()
                .get("/categorias-habilitacao/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("D"));
    }

    @Test
    public void testCadastrarComDadosInvalidosRetorna400() {
        CategoriaHabilitacaoDTO dtoInvalido = new CategoriaHabilitacaoDTO(
                "",
                ""
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/categorias-habilitacao")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodos() {
        given()
            .when()
                .get("/categorias-habilitacao")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/categorias-habilitacao/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarEExcluir() {
        CategoriaHabilitacaoDTO dto = new CategoriaHabilitacaoDTO(
                "E",
                "Combinação de veículos com reboque"
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/categorias-habilitacao")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        CategoriaHabilitacaoDTO dtoAtualizado = new CategoriaHabilitacaoDTO(
                "E",
                "Veículos articulados e carretas atualizada"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/categorias-habilitacao/" + id)
            .then()
                .statusCode(200)
                .body("descricao", equalTo("Veículos articulados e carretas atualizada"));

        given()
            .when()
                .delete("/categorias-habilitacao/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/categorias-habilitacao/" + id)
            .then()
                .statusCode(404);
    }
}
