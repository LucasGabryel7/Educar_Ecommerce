package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import org.junit.jupiter.api.Test;

import br.unitins.dto.AutoescolaDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class AutoescolaResourceTest {

    @Test
    public void testCadastrarEBuscarPorId() {
        AutoescolaDTO dto = new AutoescolaDTO(
                "Autoescola Rest Norte",
                "88.777.666/0001-55",
                "(63) 3211-1000",
                "contato@autoescolanorte.com",
                "Av. Teotônio Segurado, 1000"
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/autoescolas")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Autoescola Rest Norte"))
                .body("cnpj", equalTo("88777666000155"))
            .extract()
                .path("id");

        given()
            .when()
                .get("/autoescolas/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("nome", equalTo("Autoescola Rest Norte"));
    }

    @Test
    public void testCadastrarComDadosInvalidosRetorna400() {
        AutoescolaDTO dtoInvalido = new AutoescolaDTO(
                "",
                "123",
                "",
                "email_invalido",
                ""
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/autoescolas")
            .then()
                .statusCode(400);
    }

    @Test
    public void testCadastrarCnpjDuplicadoRetorna400() {
        String cnpj = "77.888.999/0001-44";

        AutoescolaDTO dto1 = new AutoescolaDTO(
                "Autoescola Original",
                cnpj,
                "3211-2000",
                "original@autoescola.com",
                "Rua 10"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto1)
            .when()
                .post("/autoescolas")
            .then()
                .statusCode(201);

        AutoescolaDTO dto2 = new AutoescolaDTO(
                "Autoescola Clonada",
                cnpj,
                "3211-2001",
                "clonada@autoescola.com",
                "Rua 11"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto2)
            .when()
                .post("/autoescolas")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodos() {
        given()
            .when()
                .get("/autoescolas")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/autoescolas/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testBuscarPorCnpj() {
        String cnpjFormatado = "66.555.444/0001-33";
        String cnpjNormalizado = "66555444000133";

        AutoescolaDTO dto = new AutoescolaDTO(
                "Autoescola Busca CNPJ",
                cnpjFormatado,
                "3211-3000",
                "busca.cnpj@autoescola.com",
                "Rua 20"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/autoescolas")
            .then()
                .statusCode(201);

        given()
            .when()
                .get("/autoescolas/cnpj/" + cnpjNormalizado)
            .then()
                .statusCode(200)
                .body("cnpj", equalTo(cnpjNormalizado))
                .body("nome", equalTo("Autoescola Busca CNPJ"));
    }

    @Test
    public void testAtualizarEExcluir() {
        AutoescolaDTO dto = new AutoescolaDTO(
                "Autoescola Para Atualizar",
                "55.444.333/0001-22",
                "3211-4000",
                "atualizar@autoescola.com",
                "Rua 30"
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/autoescolas")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        AutoescolaDTO dtoAtualizado = new AutoescolaDTO(
                "Autoescola Atualizada Com Sucesso",
                "55.444.333/0001-22",
                "3211-4999",
                "atualizada@autoescola.com",
                "Rua 30, Sl 2"
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/autoescolas/" + id)
            .then()
                .statusCode(200)
                .body("nome", equalTo("Autoescola Atualizada Com Sucesso"))
                .body("telefone", equalTo("3211-4999"));

        given()
            .when()
                .delete("/autoescolas/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/autoescolas/" + id)
            .then()
                .statusCode(404);
    }
}
